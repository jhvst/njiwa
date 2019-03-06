package io.njiwa.common.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {

    public static String buildJSON(Object resp) {
        ObjectMapper objMapper = new ObjectMapper();
        String objectJson = null;
        try {
            objectJson = objMapper.writeValueAsString(resp);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return objectJson;
    }
}
