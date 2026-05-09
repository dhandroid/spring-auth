package com.example.minimalapi.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Locale;

class RegistrationRoleJsonDeserializer extends JsonDeserializer<RegistrationRole> {

    @Override
    public RegistrationRole deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.currentToken() == JsonToken.VALUE_NULL) {
            return RegistrationRole.USER;
        }
        String text = p.getValueAsString();
        if (text == null || text.isBlank()) {
            return RegistrationRole.USER;
        }
        return RegistrationRole.valueOf(text.trim().toUpperCase(Locale.ROOT));
    }
}
