package org.example.board_game.core.common.support;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Component
public class RestTemplateHelper {

  final RestTemplate restTemplate;

  public <T, R> R postForObject(String url, T requestBody, HttpHeaders headers, Class<R> responseType) {
    HttpEntity<T> requestEntity = new HttpEntity<>(requestBody, headers);
    return restTemplate.postForObject(url, requestEntity, responseType);
  }

  public <R> R getForObject(String url, HttpHeaders headers, Class<R> responseType) {
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
    ResponseEntity<R> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, responseType);
    return responseEntity.getBody();
  }

//  public HttpHeaders createAuthHeaders(Authentication authentication) {
//    HttpHeaders httpHeaders = new HttpHeaders();
//    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//    httpHeaders.add("Authorization", "Bearer " + AuthenticationHelper.getAccessToken(authentication));
//    return httpHeaders;
//  }
}
