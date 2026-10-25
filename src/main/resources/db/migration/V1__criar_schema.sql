CREATE SEQUENCE conta_numero_seq
    START WITH 1000 INCREMENT BY 1 MAXVALUE 99999999 NO CYCLE;

CREATE TABLE conta (
                       id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       numero     BIGINT        NOT NULL,
                       digito     SMALLINT      NOT NULL,
                       documento  VARCHAR(14)   NOT NULL,
                       saldo      NUMERIC(19,2) NOT NULL DEFAULT 0,
                       criada_em  TIMESTAMPTZ   NOT NULL DEFAULT now(),
                       CONSTRAINT uk_conta_numero_digito UNIQUE (numero, digito),
                       CONSTRAINT ck_conta_saldo  CHECK (saldo >= 0),
                       CONSTRAINT ck_conta_digito CHECK (digito BETWEEN 0 AND 9)
);

CREATE INDEX idx_conta_documento ON conta (documento);

CREATE TABLE movimentacao (
                              id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                              conta_id             BIGINT        NOT NULL REFERENCES conta (id),
                              tipo                 VARCHAR(30)   NOT NULL,
                              valor                NUMERIC(19,2) NOT NULL CHECK (valor > 0),
                              saldo_apos           NUMERIC(19,2) NOT NULL,
                              conta_contraparte_id BIGINT        REFERENCES conta (id),
                              transferencia_id     UUID,
                              criada_em            TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_movimentacao_conta_data ON movimentacao (conta_id, criada_em DESC);

CREATE INDEX idx_movimentacao_transferencia
    ON movimentacao (transferencia_id) WHERE transferencia_id IS NOT NULL;