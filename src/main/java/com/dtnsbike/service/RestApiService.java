package com.dtnsbike.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RestApiService {
    private static ObjectMapper mapper = new ObjectMapper();

    public JsonNode get(String uri) throws IOException {
        return request("GET", uri, null);
    }

    public JsonNode post(String uri, Object data) throws IOException {
        return request("POST", uri, data);
    }

    public JsonNode push(String uri, Object data) throws IOException {
        return request("PUT", uri, data);
    }

    public JsonNode delete(String uri) throws IOException {
        return request("DELETE", uri, null);
    }

    private JsonNode request(String method, String uri, Object data) throws IOException {
        URL newURL = new URL(GetUrlApi.url(uri));
        HttpURLConnection connection = (HttpURLConnection) newURL.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod(method);
        if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT")) {
            connection.setDoOutput(true);
            mapper.writeValue(connection.getOutputStream(), data);
        }
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            return mapper.readTree(connection.getInputStream());
        }
        connection.disconnect();
        return null;
    }
}
