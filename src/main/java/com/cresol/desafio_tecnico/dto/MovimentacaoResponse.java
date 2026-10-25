package com.cresol.desafio_tecnico.dto;

import com.cresol.desafio_tecnico.model.Movimentacao;
import com.cresol.desafio_tecnico.model.TipoMovimentacao;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class MovimentacaoResponse {

    private final Long id;
    private final TipoMovimentacao tipo;
    private final BigDecimal valor;
    private final BigDecimal saldoApos;
    private final UUID transferenciaId;
    private final Instant criadaEm;

    public MovimentacaoResponse(Movimentacao movimentacao) {
        this.id = movimentacao.getId();
        this.tipo = movimentacao.getTipo();
        this.valor = movimentacao.getValor();
        this.saldoApos = movimentacao.getSaldoApos();
        this.transferenciaId = movimentacao.getTransferenciaId();
        this.criadaEm = movimentacao.getCriadaEm();
    }

    public Long getId() { return id; }

    public TipoMovimentacao getTipo() { return tipo; }

    public BigDecimal getValor() { return valor; }

    public BigDecimal getSaldoApos() { return saldoApos; }

    public UUID getTransferenciaId() { return transferenciaId; }

    public Instant getCriadaEm() { return criadaEm; }
}
