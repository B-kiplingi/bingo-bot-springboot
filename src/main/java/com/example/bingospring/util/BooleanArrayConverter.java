package com.example.bingospring.util;

import com.google.gson.Gson;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BooleanArrayConverter implements AttributeConverter<boolean[][], String> {
    private static final Gson gson = new Gson();

    @Override
    public String convertToDatabaseColumn(boolean[][] attribute) {
        return attribute == null ? null : gson.toJson(attribute);
    }

    @Override
    public boolean[][] convertToEntityAttribute(String dbData) {
        return dbData == null ? null : gson.fromJson(dbData, boolean[][].class);
    }
}