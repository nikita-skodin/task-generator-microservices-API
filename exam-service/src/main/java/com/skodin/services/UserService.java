package com.skodin.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class UserService {

    public boolean isTeacher(String authHeader) {
        return getRoleFromAuthHeader(authHeader, "ROLE_TEACHER");
    }

    public boolean isStudent(String authHeader) {
        return getRoleFromAuthHeader(authHeader, "ROLE_STUDENT");
    }

    public boolean getRoleFromAuthHeader(String authHeader, String expectedRole) {
        try {
            String[] chunks = authHeader.substring(7).split("\\.");

            Base64.Decoder decoder = Base64.getUrlDecoder();

            String payload = new String(decoder.decode(chunks[1]));

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(payload);

            String role = jsonNode
                    .path("roles")
                    .path(0)
                    .path("authority")
                    .asText();

            return role.equalsIgnoreCase(expectedRole);

        } catch (Exception e) {
            return false;
        }
    }

}
