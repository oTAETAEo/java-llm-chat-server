package com.example.aisocket.project.domain.encryption;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EncryptedDoubleAttributeConverter implements AttributeConverter<Double, String> {

    @Override
    public String convertToDatabaseColumn(Double attribute) {
        return attribute == null ? null : WorkoutDataEncryptor.encrypt(attribute.toString());
    }

    @Override
    public Double convertToEntityAttribute(String dbData) {
        String decrypted = WorkoutDataEncryptor.decrypt(dbData);
        return decrypted == null ? null : Double.valueOf(decrypted);
    }
}
