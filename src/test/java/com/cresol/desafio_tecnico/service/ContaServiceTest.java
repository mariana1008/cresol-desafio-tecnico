package com.cresol.desafio_tecnico.service;

import com.cresol.desafio_tecnico.TestcontainersConfiguration;
import com.cresol.desafio_tecnico.dto.*;
import com.cresol.desafio_tecnico.exception.SaldoInsuficienteException;
import com.cresol.desafio_tecnico.model.TipoMovimentacao;
import com.cresol.desafio_tecnico.repository.MovimentacaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class ContaServiceTest {

    @Autowired
    ContaServiceImpl contaService;
    @Autowired
    ExtratoServiceImpl extratoService;

    @MockitoSpyBean MovimentacaoRepository movimentacaoRepository;

    @Test
    void testdepositosConcorrentes() throws Exception {
        ContaResponse conta = novaConta("52998224725");
        int n = 100;
        ExecutorService pool = Executors.newFixedThreadPool(20);
        List<Future<?>> fs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            fs.add(pool.submit(() -> contaService.depositar(criarOpRequest(conta, "10.00"))));
        }
        for (Future<?> f : fs) f.get(30, TimeUnit.SECONDS);
        pool.shutdown();

        assertThat(getSaldo(conta)).isEqualByComparingTo("1000.00");
        var extrato = extratoService.extrato(conta.getNumero(), conta.getDigito().intValue(),
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), 0, 100);
        assertThat(extrato.getMovimentacoes()).hasSize(n);
    }

    @Test
    void testTransferenciasCruzadas() throws Exception {
        ContaResponse a = novaConta("52998224725");
        ContaResponse b = novaConta("11222333000181");
        contaService.depositar(criarOpRequest(a, "1000.00"));
        contaService.depositar(criarOpRequest(b, "1000.00"));

        ExecutorService pool = Executors.newFixedThreadPool(20);
        List<Future<?>> fs = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            fs.add(pool.submit(() -> contaService.transferir(criarTransferencia(a, b, "10.00"))));
            fs.add(pool.submit(() -> contaService.transferir(criarTransferencia(b, a, "10.00"))));
        }
        for (Future<?> f : fs) f.get(30, TimeUnit.SECONDS);
        pool.shutdown();

        assertThat(getSaldo(a).add(getSaldo(b))).isEqualByComparingTo("2000.00");
    }

    @Test
    void testSaqueSemSaldo() {
        ContaResponse conta = novaConta("52998224725");
        contaService.depositar(criarOpRequest(conta, "50.00"));

        assertThatThrownBy(() -> contaService.sacar(criarOpRequest(conta, "80.00")))
                .isInstanceOf(SaldoInsuficienteException.class);

        assertThat(getSaldo(conta)).isEqualByComparingTo("50.00");
    }

    @Test
    void testRollbackCompleto() {
        ContaResponse origem = novaConta("52998224725");
        ContaResponse destino = novaConta("11222333000181");
        contaService.depositar(criarOpRequest(origem, "100.00"));

        doThrow(new RuntimeException("falha"))
                .when(movimentacaoRepository)
                .save(argThat(m -> m.getTipo() == TipoMovimentacao.TRANSFERENCIA_RECEBIDA));

        assertThatThrownBy(() -> contaService.transferir(criarTransferencia(origem, destino, "40.00")))
                .hasMessageContaining("falha");

        assertThat(getSaldo(origem)).isEqualByComparingTo("100.00");
        assertThat(getSaldo(destino)).isEqualByComparingTo("0.00");
        var ext = extratoService.extrato(origem.getNumero(), origem.getDigito().intValue(),
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), 0, 100);
        assertThat(ext.getMovimentacoes()).hasSize(1);
    }

    private ContaResponse novaConta(String doc) {
        AberturaContaRequest contaRequest = new AberturaContaRequest();
        contaRequest.setDocumento(doc);
        return contaService.abrir(contaRequest);
    }

    private OperacaoRequest criarOpRequest(ContaResponse response, String valor) {
        OperacaoRequest operacaoRequest = new OperacaoRequest();
        operacaoRequest.setNumeroConta(response.getNumero());
        operacaoRequest.setDigitoConta(response.getDigito().intValue());
        operacaoRequest.setValor(new BigDecimal(valor));
        return operacaoRequest;
    }

    private TransferenciaRequest criarTransferencia(ContaResponse o, ContaResponse d, String valor) {
        TransferenciaRequest r = new TransferenciaRequest();
        r.setNumeroContaOrigem(o.getNumero());
        r.setDigitoContaOrigem(o.getDigito().intValue());
        r.setNumeroContaDestino(d.getNumero());
        r.setDigitoContaDestino(d.getDigito().intValue());
        r.setValor(new BigDecimal(valor));
        return r;
    }

    private BigDecimal getSaldo(ContaResponse c) {
        return extratoService.extrato(c.getNumero(), c.getDigito().intValue(), null, null, 0, 1).getSaldoTotal();
    }
}