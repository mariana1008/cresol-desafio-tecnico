package com.cresol.desafio_tecnico.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = Movimentacao.TABLE)
public class Movimentacao {

    public static final String TABLE = "movimentacao";

    public static final class Fields {
        public static final String ID = "id";
        public static final String CONTA_ID = "conta_id";
        public static final String TIPO = "tipo";
        public static final String VALOR = "valor";
        public static final String SALDO_APOS = "saldo_apos";
        public static final String CONTA_CONTRAPARTE_ID = "conta_contraparte_id";
        public static final String TRANSFERENCIA_ID = "transferencia_id";
        public static final String CRIADA_EM = "criada_em";

        private Fields() {
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Fields.ID)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = Fields.CONTA_ID, nullable = false, updatable = false)
    private Conta conta;

    @Enumerated(EnumType.STRING)
    @Column(name = Fields.TIPO, nullable = false, length = 30, updatable = false)
    private TipoMovimentacao tipo;

    @Column(name = Fields.VALOR, nullable = false, precision = 19, scale = 2, updatable = false)
    private BigDecimal valor;

    @Column(name = Fields.SALDO_APOS, nullable = false, precision = 19, scale = 2, updatable = false)
    private BigDecimal saldoApos;

    @Column(name = Fields.CONTA_CONTRAPARTE_ID, updatable = false)
    private Long contaContraparteId;

    @Column(name = Fields.TRANSFERENCIA_ID, updatable = false)
    private UUID transferenciaId;

    @Column(name = Fields.CRIADA_EM, nullable = false, updatable = false)
    private Instant criadaEm;

    protected Movimentacao() {
    }

    public Movimentacao(Conta conta, TipoMovimentacao tipo, BigDecimal valor,
                        Long contaContraparteId, UUID transferenciaId) {
        this.conta = conta;
        this.tipo = tipo;
        this.valor = valor;
        this.saldoApos = conta.getSaldo();
        this.contaContraparteId = contaContraparteId;
        this.transferenciaId = transferenciaId;
        this.criadaEm = Instant.now();
    }

    public Long getId() { return id; }
    public Conta getConta() { return conta; }
    public TipoMovimentacao getTipo() { return tipo; }
    public BigDecimal getValor() { return valor; }
    public BigDecimal getSaldoApos() { return saldoApos; }
    public Long getContaContraparteId() { return contaContraparteId; }
    public UUID getTransferenciaId() { return transferenciaId; }
    public Instant getCriadaEm() { return criadaEm; }
}