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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpBuilder {

    public static final OkHttpClient httpClient = new OkHttpClient();

    public static final Gson gson = new Gson();

    public static <T> List<T> getResponseList(String url, Class<T> clazz) {
        @SuppressWarnings("unchecked")
        List<T> responseList=(List<T>) getResponse(url,TypeToken.getParameterized(List.class, clazz).getType());
        return responseList;
    }


    public static Object getResponse(String url,Type type) {
        Request request = new Request.Builder().url(url).build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.body() != null) {
                InputStream inputStream = response.body().byteStream();
                JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
                return gson.fromJson(jsonReader, type);
            }else {
                throw new RuntimeException("response body is null");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static InputStream getResponseStream(String url) {
        Request request = new Request.Builder().url(url).build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.body() != null) {
                return response.body().byteStream();
            }else {
                throw new RuntimeException("response body is null");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
