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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ContaServiceImpl implements ContaService {
    private static final Logger log = LoggerFactory.getLogger(ContaServiceImpl.class);

    private static final int TAMANHO_MAXIMO_PAGINA = 100;

    private final ContaRepository contaRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public ContaServiceImpl(ContaRepository contaRepository, MovimentacaoRepository movimentacaoRepository) {
        this.contaRepository = contaRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Transactional
    public ContaResponse abrir(AberturaContaRequest request) {
        String documento = CpfCnpjValidador.normalizar(request.getDocumento());
        long numero = contaRepository.proximoNumero();
        short digito = (short) DigitoVerificadorConta.calcular(numero);
        Conta conta = contaRepository.save(new Conta(numero, digito, documento));
        log.info("Conta aberta: {}-{}", conta.getNumero(), conta.getDigito());

        return new ContaResponse(conta);
    }

    @Transactional
    public MovimentacaoResponse depositar(OperacaoRequest request) {
        Conta conta = buscarParaAtualizar(request.getNumeroConta(), request.getDigitoConta());
        conta.creditar(request.getValor());
        Movimentacao mov = movimentacaoRepository.save(
                new Movimentacao(conta, TipoMovimentacao.DEPOSITO, request.getValor(), null, null));
        log.info("Deposito: conta={}-{} valor={} saldo={}", request.getNumeroConta(), request.getDigitoConta(), request.getValor(), conta.getSaldo());

        return new MovimentacaoResponse(mov);
    }

    @Transactional
    public MovimentacaoResponse sacar(OperacaoRequest request) {
        Conta conta = buscarParaAtualizar(request.getNumeroConta(), request.getDigitoConta());
        BigDecimal saldoAntes = conta.getSaldo();
        conta.debitar(request.getValor());
        log.info("Saque: conta={}-{} valor={} saldo anterior={} saldo atual={}",
                request.getNumeroConta(), request.getDigitoConta(), request.getValor(), saldoAntes, conta.getSaldo());

        Movimentacao mov = movimentacaoRepository.save(
                new Movimentacao(conta, TipoMovimentacao.SAQUE, request.getValor(), null, null));
        return new MovimentacaoResponse(mov);
    }

    @Transactional
    public TransferenciaResponse transferir(TransferenciaRequest request) {
        log.info("Transferência iniciada: origem={}-{} destino={}-{} valor={}",
                request.getNumeroContaOrigem(), request.getDigitoContaOrigem(),
                request.getNumeroContaDestino(), request.getDigitoContaDestino(), request.getValor());

        if (request.getNumeroContaOrigem().equals(request.getNumeroContaDestino())) {
            throw new OperacaoInvalidaException("Conta de origem e destino devem ser diferentes");
        }

        Conta origem;
        Conta destino;
        if (request.getNumeroContaOrigem() < request.getNumeroContaDestino()) {
            origem = buscarParaAtualizar(request.getNumeroContaOrigem(), request.getDigitoContaOrigem());
            destino = buscarParaAtualizar(request.getNumeroContaDestino(), request.getDigitoContaDestino());
        } else {
            destino = buscarParaAtualizar(request.getNumeroContaDestino(), request.getDigitoContaDestino());
            origem = buscarParaAtualizar(request.getNumeroContaOrigem(), request.getDigitoContaOrigem());
        }

        origem.debitar(request.getValor());
        destino.creditar(request.getValor());

        UUID transferenciaId = UUID.randomUUID();
        movimentacaoRepository.save(new Movimentacao(origem, TipoMovimentacao.TRANSFERENCIA_ENVIADA,
                request.getValor(), destino.getId(), transferenciaId));
        movimentacaoRepository.save(new Movimentacao(destino, TipoMovimentacao.TRANSFERENCIA_RECEBIDA,
                request.getValor(), origem.getId(), transferenciaId));

        log.info("Transferência concluída: id={} saldoOrigem={} saldoDestino={}", transferenciaId, origem.getSaldo(), destino.getSaldo());

        return new TransferenciaResponse(transferenciaId, request.getValor(), origem.getSaldo());
    }

    @Transactional(readOnly = true)
    public PaginaResponse<ContaResponse> listarTodasAsContas(String documento, int pagina, int tamanho) {
        int tam = Math.min(Math.max(tamanho, 1), TAMANHO_MAXIMO_PAGINA);

        Pageable pageable = PageRequest.of(Math.max(pagina, 0), tam, Sort.by("id"));

        Page<Conta> resultado;
        if (documento == null || documento.isBlank()) {
            resultado = contaRepository.findAll(pageable);
        } else {
            resultado = contaRepository.findByDocumento(CpfCnpjValidador.normalizar(documento), pageable);
        }

        return PaginaResponse.paginatedRespomse(resultado, ContaResponse::new);
    }

    private Conta buscarParaAtualizar(Long numero, Integer digito) {
        if (!DigitoVerificadorConta.isValido(numero, digito)) {
            log.warn("Dígito verificador inválido: {}-{}", numero, digito);

            throw new OperacaoInvalidaException("Dígito verificador inválido. " + numero);
        }

        return contaRepository.buscarParaAtualizar(numero, digito)
                .orElseThrow(() -> new ContaNaoEncontradaException(numero, digito));
    }
}
