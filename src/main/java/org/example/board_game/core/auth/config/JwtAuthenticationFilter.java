package org.example.board_game.core.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.auth.service.JwtService;
import org.example.board_game.core.common.support.JsonHelper;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.utils.Response;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    JwtService jwtService;
    UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String AUTHORIZATION_HEADER = "Authorization";
        String TOKEN_PREFIX = "Bearer ";
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);
        try {
            String username = jwtService.extractUsername(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isValidToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            setErrorResponse(response, "Token hết hạn.");
        } catch (MalformedJwtException e) {
            setErrorResponse(response, "Token không hợp lệ.");
        } catch (Exception e) {
            setErrorResponse(response, "Lỗi xác thực token.");
        }
    }


    private void setErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(EntityProperties.CODE_GET);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Response<Object> errorResponse = Response.fail(message, HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(JsonHelper.writeAsString(errorResponse));
    }
}
