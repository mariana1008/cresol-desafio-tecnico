package com.cresol.desafio_tecnico.model;

import com.cresol.desafio_tecnico.exception.SaldoInsuficienteException;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = Conta.TABLE)
public class Conta {

    public static final String TABLE = "conta";

    /** Nomes das colunas do banco. */
    public static final class Fields {
        public static final String ID = "id";
        public static final String NUMERO = "numero";
        public static final String DIGITO = "digito";
        public static final String DOCUMENTO = "documento";
        public static final String SALDO = "saldo";
        public static final String CRIADA_EM = "criada_em";

        private Fields() {
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Fields.ID)
    private Long id;

    @Column(name = Fields.NUMERO, nullable = false, updatable = false)
    private Long numero;

    @Column(name = Fields.DIGITO, nullable = false, updatable = false)
    private Integer digito;

    @Column(name = Fields.DOCUMENTO, nullable = false, length = 14, updatable = false)
    private String documento;

    @Column(name = Fields.SALDO, nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    @Column(name = Fields.CRIADA_EM, nullable = false, updatable = false)
    private Instant criadaEm;

    protected Conta() { // exigido pelo JPA
    }

    public Conta(Long numero, Integer digito, String documento) {
        this.numero = numero;
        this.digito = digito;
        this.documento = documento;
        this.criadaEm = Instant.now();
    }

    public void creditar(BigDecimal valor) {
        this.saldo = this.saldo.add(valor);
    }

    public void debitar(BigDecimal valor) {
        if (this.saldo.compareTo(valor) < 0) {
            throw new SaldoInsuficienteException();
        }
        this.saldo = this.saldo.subtract(valor);
    }

    public Long getId() { return id; }
    public Long getNumero() { return numero; }
    public Integer getDigito() { return digito; }
    public String getDocumento() { return documento; }
    public BigDecimal getSaldo() { return saldo; }
    public Instant getCriadaEm() { return criadaEm; }
}