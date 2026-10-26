package com.cresol.desafio_tecnico.service;

import com.cresol.desafio_tecnico.dto.ExtratoResponse;
import com.cresol.desafio_tecnico.dto.MovimentacaoResponse;
import com.cresol.desafio_tecnico.exception.ContaNaoEncontradaException;
import com.cresol.desafio_tecnico.exception.OperacaoInvalidaException;
import com.cresol.desafio_tecnico.model.Conta;
import com.cresol.desafio_tecnico.model.Movimentacao;
import com.cresol.desafio_tecnico.repository.ContaRepository;
import com.cresol.desafio_tecnico.repository.MovimentacaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class ExtratoServiceImpl implements ExtraroService {
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
                .orElseThrow(() -> new ContaNaoEncontradaException(numero, digito));

        LocalDate hoje = LocalDate.now(ZONA);
        LocalDate de = inicio != null ? inicio : hoje.withDayOfMonth(1);
        LocalDate ate = fim != null ? fim : hoje;
        if (ate.isBefore(de)) {
            throw new OperacaoInvalidaException("A data final não pode ser anterior à inicial");
        }

        Instant ini = de.atStartOfDay(ZONA).toInstant();
        Instant lim = ate.plusDays(1).atStartOfDay(ZONA).toInstant();
        int tam = Math.min(Math.max(tamanho, 1), TAMANHO_MAXIMO_PAGINA);

        Page<Movimentacao> p = movimentacaoRepository.buscarPorPeriodo(
                conta.getId(), ini, lim, PageRequest.of(Math.max(pagina, 0), tam));

        return new ExtratoResponse(conta.getNumero(), conta.getDigito(), conta.getSaldo(), de, ate,
                p.getNumber(), p.getTotalPages(), p.getTotalElements(),
                p.getContent().stream().map(MovimentacaoResponse::new).toList());
    }
}
