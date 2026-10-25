package com.cresol.desafio_tecnico.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfCnpjValidator implements ConstraintValidator<CpfCnpj, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || CpfCnpjValidador.isValido(value);
    }
}