package org.chorser.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class HttpBuilder {

    public static final OkHttpClient httpClient = new OkHttpClient();

    public static final Gson gson = new Gson();

    public static <T> List<T> getResponseList(String url, Class<T> clazz) {
        Request request = new Request.Builder().url(url).build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.body() != null) {
                InputStream inputStream = response.body().byteStream();
                JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
                Type type = new TypeToken<List<T>>() {}.getType();

                return gson.fromJson(jsonReader, type);
            }else {
                throw new RuntimeException("response body is null");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
