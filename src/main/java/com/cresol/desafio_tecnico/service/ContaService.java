package com.cresol.desafio_tecnico.service;

import com.cresol.desafio_tecnico.dto.*;
import com.cresol.desafio_tecnico.exception.ContaNaoEncontradaException;
import com.cresol.desafio_tecnico.exception.OperacaoInvalidaException;
import com.cresol.desafio_tecnico.model.Conta;
import com.cresol.desafio_tecnico.model.Movimentacao;
import com.cresol.desafio_tecnico.model.TipoMovimentacao;
import com.cresol.desafio_tecnico.repository.ContaRepository;
import com.cresol.desafio_tecnico.repository.MovimentacaoRepository;
import com.cresol.desafio_tecnico.util.CpfCnpjValidador;
import com.cresol.desafio_tecnico.util.DigitoVerificadorConta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class ContaService {

    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    private static final int TAMANHO_MAXIMO_PAGINA = 100;

    private final ContaRepository contaRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public ContaService(ContaRepository contaRepository, MovimentacaoRepository movimentacaoRepository) {
        this.contaRepository = contaRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Transactional
    public ContaResponse abrir(AberturaContaRequest request) {
        String documento = CpfCnpjValidador.normalizar(request.getCpfCnpj());
        long numero = contaRepository.proximoNumero();
        short digito = (short) DigitoVerificadorConta.calcular(numero);
        Conta conta = contaRepository.save(new Conta(numero, digito, documento));
        return new ContaResponse(conta);
    }

    @Transactional
    public MovimentacaoResponse depositar(OperacaoRequest request) {
        Conta conta = buscarParaAtualizar(request.getNumeroConta(), request.getDigitoConta());
        conta.creditar(request.getValor());
        Movimentacao mov = movimentacaoRepository.save(
                new Movimentacao(conta, TipoMovimentacao.DEPOSITO, request.getValor(), null, null));
        return new MovimentacaoResponse(mov);
    }

    @Transactional
    public MovimentacaoResponse sacar(OperacaoRequest request) {
        Conta conta = buscarParaAtualizar(request.getNumeroConta(), request.getDigitoConta());
        conta.debitar(request.getValor());
        Movimentacao mov = movimentacaoRepository.save(
                new Movimentacao(conta, TipoMovimentacao.SAQUE, request.getValor(), null, null));
        return new MovimentacaoResponse(mov);
    }

    @Transactional
    public TransferenciaResponse transferir(TransferenciaRequest r) {
        if (r.getNumeroContaOrigem().equals(r.getNumeroContaDestino())) {
            throw new OperacaoInvalidaException("Conta de origem e destino devem ser diferentes");
        }

        // Lock SEMPRE na mesma ordem (menor número primeiro) => evita deadlock
        // entre A->B e B->A simultâneos.
        Conta origem;
        Conta destino;
        if (r.getNumeroContaOrigem() < r.getNumeroContaDestino()) {
            origem = buscarParaAtualizar(r.getNumeroContaOrigem(), r.getDigitoContaOrigem());
            destino = buscarParaAtualizar(r.getNumeroContaDestino(), r.getDigitoContaDestino());
        } else {
            destino = buscarParaAtualizar(r.getNumeroContaDestino(), r.getDigitoContaDestino());
            origem = buscarParaAtualizar(r.getNumeroContaOrigem(), r.getDigitoContaOrigem());
        }

        origem.debitar(r.getValor());
        destino.creditar(r.getValor());

        UUID transferenciaId = UUID.randomUUID();
        movimentacaoRepository.save(new Movimentacao(origem, TipoMovimentacao.TRANSFERENCIA_ENVIADA,
                r.getValor(), destino.getId(), transferenciaId));
        movimentacaoRepository.save(new Movimentacao(destino, TipoMovimentacao.TRANSFERENCIA_RECEBIDA,
                r.getValor(), origem.getId(), transferenciaId));

        return new TransferenciaResponse(transferenciaId, r.getValor(), origem.getSaldo());
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

    private Conta buscarParaAtualizar(Long numero, Integer digito) {
        if (!DigitoVerificadorConta.isValido(numero, digito)) {
            throw new OperacaoInvalidaException("Dígito verificador inválido. " + numero);
        }
        return contaRepository.buscarParaAtualizar(numero, digito)
                .orElseThrow(() -> new ContaNaoEncontradaException(numero, digito));
    }
}
