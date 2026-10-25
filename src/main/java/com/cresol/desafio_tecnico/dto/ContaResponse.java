package com.cresol.desafio_tecnico.dto;


import com.cresol.desafio_tecnico.model.Conta;

import java.math.BigDecimal;
import java.time.Instant;

public class ContaResponse {

    private final Long numero;
    private final Short digito;
    private final String documento;
    private final BigDecimal saldo;
    private final Instant criadaEm;

    public ContaResponse(Conta conta) {
        this.numero = conta.getNumero();
        this.digito = conta.getDigito();
        this.documento = conta.getDocumento();
        this.saldo = conta.getSaldo();
        this.criadaEm = conta.getCriadaEm();
    }

    public Long getNumero() { return numero; }

    public Short getDigito() { return digito; }

    public String getDocumento() { return documento; }

    public BigDecimal getSaldo() { return saldo; }

    public Instant getCriadaEm() { return criadaEm; }
}
