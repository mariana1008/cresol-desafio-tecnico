package com.cresol.desafio_tecnico.repository;

import com.cresol.desafio_tecnico.model.Conta;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import com.cresol.desafio_tecnico.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class ContaRepositoryTest {

    @Autowired
    private ContaRepository repository;

    @Test
    void testSalvarEBuscarPorNumeroEDigito() {
        long numero = repository.proximoNumero();
        repository.saveAndFlush(new Conta(numero, (short) 5, "52998224725"));

        assertThat(repository.findByNumeroAndDigito(numero, 5)).isPresent()
                .get().extracting(Conta::getDocumento).isEqualTo("52998224725");
    }

    @Test
    void testBuscarComDigitoDiferente() {
        long numero = repository.proximoNumero();
        repository.saveAndFlush(new Conta(numero, (short)5, "52998224725"));

        assertThat(repository.findByNumeroAndDigito(numero, 6)).isEmpty();
    }

    @Test
    void testComNumeroEDigitoDuplicados() {
        long numero = repository.proximoNumero();
        repository.saveAndFlush(new Conta(numero, (short)1, "52998224725"));

        assertThatThrownBy(() -> repository.saveAndFlush(new Conta(numero, (short)1, "11222333000181")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void testSaldoNegativo() {
        long numero = repository.proximoNumero();
        Conta conta = new Conta(numero, (short)1, "52998224725");
        conta.creditar(new java.math.BigDecimal("-1.00"));

        assertThatThrownBy(() -> repository.saveAndFlush(conta))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
