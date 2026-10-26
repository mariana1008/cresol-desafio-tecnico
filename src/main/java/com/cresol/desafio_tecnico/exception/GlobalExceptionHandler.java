package com.cresol.desafio_tecnico.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ContaNaoEncontradaException.class)
    public ProblemDetail contaNaoEncontrada(ContaNaoEncontradaException ex) {
        log.warn("404: {}", ex.getMessage());

        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({SaldoInsuficienteException.class, OperacaoInvalidaException.class})
    public ProblemDetail regraDeNegocio(RuntimeException ex) {
        log.warn("422: {}", ex.getMessage());

        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validacao(MethodArgumentNotValidException ex) {
        log.warn("400: {}", ex.getMessage());

        String detalhe = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b).orElse("Requisição inválida");

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detalhe);
    }

    @ExceptionHandler(PessimisticLockingFailureException.class)
    public ProblemDetail conflitoDeLock(PessimisticLockingFailureException ex) {
        log.warn("4009: {}", ex.getMessage());

        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "Conta em uso por otra operação. Tente novamente.");
    }
}