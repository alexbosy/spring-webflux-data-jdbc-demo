package io.cryptorush.userservice.domain.validation;

public class BusinessFieldValidationException extends RuntimeException {

    private final String fieldName;

    public BusinessFieldValidationException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
