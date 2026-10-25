package com.cresol.desafio_tecnico.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class TransferenciaResponse {

    private final UUID transferenciaId;
    private final BigDecimal valor;
    private final BigDecimal saldoOrigemApos;

    public TransferenciaResponse(UUID transferenciaId, BigDecimal valor, BigDecimal saldoOrigemApos) {
        this.transferenciaId = transferenciaId;
        this.valor = valor;
        this.saldoOrigemApos = saldoOrigemApos;
    }

    public UUID getTransferenciaId() { return transferenciaId; }

    public BigDecimal getValor() { return valor; }

    public BigDecimal getSaldoOrigemApos() { return saldoOrigemApos; }
}