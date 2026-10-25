package com.cresol.desafio_tecnico.controller;

import com.cresol.desafio_tecnico.dto.*;
import com.cresol.desafio_tecnico.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService service;

    public ContaController(ContaService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContaResponse abrir(@Valid @RequestBody AberturaContaRequest request) {
        return service.abrir(request);
    }

    @PostMapping("/deposito")
    @ResponseStatus(HttpStatus.CREATED)
    public MovimentacaoResponse depositar(@Valid @RequestBody OperacaoRequest request) {
        return service.depositar(request);
    }

    @PostMapping("/saque")
    @ResponseStatus(HttpStatus.CREATED)
    public MovimentacaoResponse sacar(@Valid @RequestBody OperacaoRequest request) {
        return service.sacar(request);
    }

    @PostMapping("/transferencia")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferenciaResponse transferir(@Valid @RequestBody TransferenciaRequest request) {
        return service.transferir(request);
    }

    @GetMapping("/{numero}/{digito}/extrato")
    public ExtratoResponse extrato(
            @PathVariable Long numero,
            @PathVariable Integer digito,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "50") int tamanho) {
        return service.extrato(numero, digito, inicio, fim, pagina, tamanho);
    }

    @GetMapping
    public PaginaResponse<ContaResponse> listar(
            @RequestParam(required = false) String documento,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "50") int tamanho) {
        return service.listarTodasAsContas(documento, pagina, tamanho);
    }
}
