package ru.yandex.test;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;

public class TestRequest<T> extends Thread {
    private TestRestTemplate restTemplate;
    private ResponseEntity<T> response;
    private Class<T> responseType;
    private HttpMethod httpMethod;
    private String uri;
    private int num;

    public TestRequest(int num, TestRestTemplate restTemplate, String uri,
                       HttpMethod httpMethod, Class<T> responseType) {
        this.restTemplate = restTemplate;
        this.uri = uri;
        this.responseType = responseType;
        this.httpMethod = httpMethod;
        this.num = num;
    }

    @Override
    public void run() {
        try {
            System.out.println("thread[" + num + "]: run");
            response = restTemplate.exchange(uri, httpMethod,
                    new RequestEntity<>(httpMethod, new URI("")), responseType);
        } catch (URISyntaxException e) {
            System.err.println("[" + num + "]: " + e.getMessage());
        }
    }

    public ResponseEntity<T> getResponse() {
        return this.response;
    }
}
