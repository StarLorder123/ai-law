package com.scera.ailaw.ai_law_api_server.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class HttpClientUtil {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // GET 请求
    public static String sendGet(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // GET 请求 + Headers
    public static String sendGet(String url, Map<String, String> headers) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10));

        headers.forEach(builder::header);

        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // POST 请求，发送 JSON 数据
    public static String sendPost(String url, String json) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // POST 请求，支持添加自定义 Header
    public static String sendPost(String url, String json, Map<String, String> headers) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(json));

        headers.forEach(builder::header);

        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}

