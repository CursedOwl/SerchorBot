package org.chorser.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.chorser.entity.Authentication;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

public class YmlReader {
    public static final ObjectMapper ymlMapper;

    static {
        ymlMapper= new ObjectMapper(new com.fasterxml.jackson.dataformat.yaml.YAMLFactory());
    }

    public static Authentication readAuthentication(String path) throws IOException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        Authentication authentication = new Authentication();
        JsonNode authenticationNode = ymlMapper.readTree(inputStream).path("authentication");
        if(authenticationNode.isMissingNode()) {
            return null;
        }
//        根据authenticationNode的内容，设置authentication的属性
        for (Field field : authentication.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String text = authenticationNode.path(field.getName()).asText();
            try {
                if(text!=null&&!text.isEmpty()){
                    field.set(authentication, text);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return authentication;
    }


}
