package com.cresol.desafio_tecnico.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class OperacaoRequest {

    @NotNull @Positive
    private Long numeroConta;

    @NotNull @Min(0) @Max(9)
    private Integer digitoConta;

    @NotNull @DecimalMin("0.01") @Digits(integer = 15, fraction = 2)
    private BigDecimal valor;

    public Long getNumeroConta() { return numeroConta; }

    public void setNumeroConta(Long numeroConta) { this.numeroConta = numeroConta; }

    public Integer getDigitoConta() { return digitoConta; }

    public void setDigitoConta(Integer digitoConta) { this.digitoConta = digitoConta; }

    public BigDecimal getValor() { return valor; }

    public void setValor(BigDecimal valor) { this.valor = valor; }
}