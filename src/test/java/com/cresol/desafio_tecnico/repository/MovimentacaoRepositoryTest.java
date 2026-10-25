package com.cresol.desafio_tecnico.repository;

import com.cresol.desafio_tecnico.model.Conta;
import com.cresol.desafio_tecnico.model.Movimentacao;
import com.cresol.desafio_tecnico.model.TipoMovimentacao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.cresol.desafio_tecnico.TestcontainersConfiguration;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class MovimentacaoRepositoryTest {

    @Autowired
    private ContaRepository contaRepository;
    @Autowired
    private MovimentacaoRepository movimentacaoRepository;


    @Test
    void testMovimentacoesOrdenadas() {
        Conta conta = novaConta();
        Movimentacao primeira = deposito(conta, "10.00");
        Movimentacao segunda = deposito(conta, "20.00");

        Instant inicio = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant fim = Instant.now().plus(1, ChronoUnit.DAYS);

        Page<Movimentacao> pagina = movimentacaoRepository
                .buscarPorPeriodo(conta.getId(), inicio, fim, PageRequest.of(0, 10));

        assertThat(pagina.getContent()).extracting(Movimentacao::getId)
                .containsExactly(segunda.getId(), primeira.getId());
        assertThat(pagina.getContent().getFirst().getSaldoApos()).isEqualByComparingTo("30.00");
    }

    @Test
    void testMovimentacoesvaziasNoPeriodo() {
        Conta conta = novaConta();
        deposito(conta, "10.00");

        Instant inicio = Instant.now().minus(60, ChronoUnit.DAYS);
        Instant fim = Instant.now().minus(30, ChronoUnit.DAYS);

        Page<Movimentacao> pagina = movimentacaoRepository
                .buscarPorPeriodo(conta.getId(), inicio, fim, PageRequest.of(0, 10));

        assertThat(pagina.getContent()).isEmpty();
    }

    @Test
    void testBuscarPorCOnta() {
        Conta a = novaConta();
        Conta b = novaConta();
        deposito(a, "10.00");
        deposito(b, "99.00");

        Page<Movimentacao> pagina = movimentacaoRepository.buscarPorPeriodo(a.getId(),
                Instant.now().minus(1, ChronoUnit.DAYS), Instant.now().plus(1, ChronoUnit.DAYS),
                PageRequest.of(0, 10));

        assertThat(pagina.getContent()).hasSize(1);
    }

    @Test
    void testPaginacao() {
        Conta conta = novaConta();
        for (int i = 0; i < 5; i++) {
            deposito(conta, "1.00");
        }

        Page<Movimentacao> pagina = movimentacaoRepository.buscarPorPeriodo(conta.getId(),
                Instant.now().minus(1, ChronoUnit.DAYS), Instant.now().plus(1, ChronoUnit.DAYS),
                PageRequest.of(1, 2));

        assertThat(pagina.getContent()).hasSize(2);
        assertThat(pagina.getTotalElements()).isEqualTo(5);
        assertThat(pagina.getTotalPages()).isEqualTo(3);
    }


    private Conta novaConta() {
        return contaRepository.saveAndFlush(new Conta(contaRepository.proximoNumero(), (short)2, "52998224725"));
    }

    private Movimentacao deposito(Conta conta, String valor) {
        conta.creditar(new BigDecimal(valor));
        return movimentacaoRepository.saveAndFlush(
                new Movimentacao(conta, TipoMovimentacao.DEPOSITO, new BigDecimal(valor), null, null));
    }
}
