package com.cresol.desafio_tecnico.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class TransferenciaRequest {

    @NotNull @Positive
    private Long numeroContaOrigem;
    @NotNull @Min(0) @Max(9)
    private Integer digitoContaOrigem;
    @NotNull @Positive
    private Long numeroContaDestino;
    @NotNull @Min(0) @Max(9)
    private Integer digitoContaDestino;
    @NotNull @DecimalMin("0.01") @Digits(integer = 15, fraction = 2)
    private BigDecimal valor;

    public Long getNumeroContaOrigem() { return numeroContaOrigem; }

    public void setNumeroContaOrigem(Long numeroContaOrigem) { this.numeroContaOrigem = numeroContaOrigem; }

    public Integer getDigitoContaOrigem() { return digitoContaOrigem; }

    public void setDigitoContaOrigem(Integer digitoContaOrigem) { this.digitoContaOrigem = digitoContaOrigem; }

    public Long getNumeroContaDestino() { return numeroContaDestino; }

    public void setNumeroContaDestino(Long numeroContaDestino) { this.numeroContaDestino = numeroContaDestino; }

    public Integer getDigitoContaDestino() { return digitoContaDestino; }

    public void setDigitoContaDestino(Integer digitoContaDestino) { this.digitoContaDestino = digitoContaDestino; }

    public BigDecimal getValor() { return valor; }

    public void setValor(BigDecimal valor) { this.valor = valor; }
}