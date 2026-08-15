package com.example.aisocket.project.domain.encryption;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EncryptedIntegerAttributeConverter implements AttributeConverter<Integer, String> {

    @Override
    public String convertToDatabaseColumn(Integer attribute) {
        return attribute == null ? null : WorkoutDataEncryptor.encrypt(attribute.toString());
    }

    @Override
    public Integer convertToEntityAttribute(String dbData) {
        String decrypted = WorkoutDataEncryptor.decrypt(dbData);
        return decrypted == null ? null : Integer.valueOf(decrypted);
    }
}
