package com.cresol.desafio_tecnico.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ExtratoResponse {

    private final Long numeroConta;
    private final Short digitoConta;
    private final BigDecimal saldoTotal;
    private final LocalDate inicio;
    private final LocalDate fim;
    private final int pagina;
    private final int totalPaginas;
    private final long totalMovimentacoes;
    private final List<MovimentacaoResponse> movimentacoes;

    public ExtratoResponse(Long numeroConta, Short digitoConta, BigDecimal saldoTotal,
                           LocalDate inicio, LocalDate fim, int pagina, int totalPaginas,
                           long totalMovimentacoes, List<MovimentacaoResponse> movimentacoes) {
        this.numeroConta = numeroConta;
        this.digitoConta = digitoConta;
        this.saldoTotal = saldoTotal;
        this.inicio = inicio;
        this.fim = fim;
        this.pagina = pagina;
        this.totalPaginas = totalPaginas;
        this.totalMovimentacoes = totalMovimentacoes;
        this.movimentacoes = movimentacoes;
    }

    public Long getNumeroConta() { return numeroConta; }

    public Short getDigitoConta() { return digitoConta; }

    public BigDecimal getSaldoTotal() { return saldoTotal; }

    public LocalDate getInicio() { return inicio; }

    public LocalDate getFim() { return fim; }

    public int getPagina() { return pagina; }

    public int getTotalPaginas() { return totalPaginas; }

    public long getTotalMovimentacoes() { return totalMovimentacoes; }

    public List<MovimentacaoResponse> getMovimentacoes() { return movimentacoes; }
}
