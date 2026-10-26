package com.cresol.desafio_tecnico.service;

import com.cresol.desafio_tecnico.dto.ExtratoResponse;
import com.cresol.desafio_tecnico.dto.MovimentacaoResponse;
import com.cresol.desafio_tecnico.exception.ContaNaoEncontradaException;
import com.cresol.desafio_tecnico.exception.OperacaoInvalidaException;
import com.cresol.desafio_tecnico.model.Conta;
import com.cresol.desafio_tecnico.model.Movimentacao;
import com.cresol.desafio_tecnico.repository.ContaRepository;
import com.cresol.desafio_tecnico.repository.MovimentacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class ExtratoServiceImpl implements ExtraroService {
    private static final Logger log = LoggerFactory.getLogger(ExtratoServiceImpl.class);
    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    private static final int TAMANHO_MAXIMO_PAGINA = 100;

    private final ContaRepository contaRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public ExtratoServiceImpl(ContaRepository contaRepository, MovimentacaoRepository movimentacaoRepository) {
        this.contaRepository = contaRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }
    @Transactional(readOnly = true)
    public ExtratoResponse extrato(Long numero, Integer digito, LocalDate inicio, LocalDate fim,
                                   int pagina, int tamanho) {
        Conta conta = contaRepository.findByNumeroAndDigito(numero, digito)
                .orElseThrow(() -> {
                    log.warn("Extrato: conta não encontrada {}-{}", numero, digito);
                    return new ContaNaoEncontradaException(numero, digito);
                });

        LocalDate hoje = LocalDate.now(ZONA);
        LocalDate de = inicio != null ? inicio : hoje.withDayOfMonth(1);
        LocalDate ate = fim != null ? fim : hoje;
        if (ate.isBefore(de)) {
            log.warn("Extrato: período inválido conta={}-{} de={} ate={}", numero, digito, de, ate);
            throw new OperacaoInvalidaException("A data final não pode ser anterior à inicial");
        }

        Instant ini = de.atStartOfDay(ZONA).toInstant();
        Instant lim = ate.plusDays(1).atStartOfDay(ZONA).toInstant();
        int tam = Math.min(Math.max(tamanho, 1), TAMANHO_MAXIMO_PAGINA);

        BigDecimal saldoTotalPeriodo = getSaldoTotalPeriodo(conta, lim, numero, digito);

        Page<Movimentacao> p = movimentacaoRepository.buscarPorPeriodo(
                conta.getId(), ini, lim, PageRequest.of(Math.max(pagina, 0), tam));
        log.info("Extrato gerado: conta={}-{} movimentacoes={} paginas={} saldoTotal={}",
                numero, digito, p.getTotalElements(), p.getTotalPages(), saldoTotalPeriodo);

        return new ExtratoResponse(conta.getNumero(), conta.getDigito(), conta.getSaldo(), de, ate,
                p.getNumber(), p.getTotalPages(), p.getTotalElements(),
                p.getContent().stream().map(MovimentacaoResponse::new).toList());
    }

    private BigDecimal getSaldoTotalPeriodo(Conta conta, Instant lim, Long numero, Integer digito ){
        BigDecimal saldoFinalPeriodo = movimentacaoRepository
                .findFirstByContaIdAndCriadaEmBeforeOrderByCriadaEmDescIdDesc(conta.getId(), lim)
                .map(Movimentacao::getSaldoApos)
                .orElse(BigDecimal.ZERO);
        log.debug("Extrato: conta={}-{} saldoFinalPeriodo={} saldoAtual={}",
                numero, digito, saldoFinalPeriodo, conta.getSaldo());

        // se inclui o momento atual o saldo ttal e o saldo por periode devem ser iguais, apenas log
        if (lim.isAfter(Instant.now()) && saldoFinalPeriodo.compareTo(conta.getSaldo()) != 0) {
            log.warn("Extrato: divergência de saldo conta={}-{} saldoFinalPeriodo={} saldoAtual={}",
                    numero, digito, saldoFinalPeriodo, conta.getSaldo());
        }

        return saldoFinalPeriodo;
    }
}
