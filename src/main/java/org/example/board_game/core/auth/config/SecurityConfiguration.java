package org.example.board_game.core.auth.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.auth.service.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfiguration {

    JwtAuthenticationFilter jwtAuthFilter;
    UserDetailService userDetailService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        req -> req
                                .requestMatchers("/", "/error/**", "/auth/**",
                                        "/admin/notifications/sse/**", "/client/product/**","/client/voucher/**",
                                        "/admin/notifications", "/client/transaction/**", "/client/order/**")
                                .permitAll()
                                .requestMatchers("/admin/customer/**"
                                        , "/admin/order/**"
                                        , "/admin/notifications/**"
                                        , "/admin/address/**")
                                .hasAnyRole("ADMIN", "EMPLOYEE")
                                .requestMatchers(HttpMethod.GET
                                        , "/admin/product/**", "/admin/author/**"
                                        , "/admin/payment/**", "/admin/publisher/**", "/admin/category/**"
                                        , "/admin/media/**", "/admin/voucher/**")
                                .hasAnyRole("ADMIN", "EMPLOYEE")
                                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                                .requestMatchers("/client/**").hasAnyRole("CUSTOMER")
                                .requestMatchers("/auth/admin-profile").hasAnyRole("ADMIN", "EMPLOYEE")
                                .anyRequest()
                                .authenticated())
                .userDetailsService(userDetailService)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
