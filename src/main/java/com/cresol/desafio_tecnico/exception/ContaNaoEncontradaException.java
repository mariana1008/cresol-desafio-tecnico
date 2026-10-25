package com.cresol.desafio_tecnico.exception;

public class ContaNaoEncontradaException extends RuntimeException {
    public ContaNaoEncontradaException(Long numero, Integer digito) {
        super("Conta %d-%d não encontrada".formatted(numero, digito));
    }
}