package ru.practicum.client;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.exception.InternalServerErrorException;

import java.util.List;
import java.util.Map;

public abstract class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, Object.class, null, body);
    }

    protected <R> ResponseEntity<R> get(String path, @Nullable Map<String, Object> parameters, Class<R> responseClass) {
        return makeAndSendRequest(HttpMethod.GET, path, responseClass, parameters, null);
    }

    private <T, R> ResponseEntity<R> makeAndSendRequest(
            HttpMethod method,
            String path,
            Class<R> responseClass,
            @Nullable Map<String, Object> parameters,
            @Nullable T body
    ) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<R> statsServerResponse;
        try {
            if (parameters != null) {
                statsServerResponse = rest.exchange(path, method, requestEntity, responseClass, parameters);
            } else {
                statsServerResponse = rest.exchange(path, method, requestEntity, responseClass);
            }
        } catch (HttpStatusCodeException e) {
            throw new InternalServerErrorException("Stats request failed");
        }
        return prepareResponse(statsServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static <R> ResponseEntity<R> prepareResponse(ResponseEntity<R> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}