package com.cresol.desafio_tecnico.controller;

import com.cresol.desafio_tecnico.dto.ExtratoResponse;
import com.cresol.desafio_tecnico.service.ExtratoServiceImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/extratos")
public class ExtratoController {

    private final ExtratoServiceImpl service;

    public ExtratoController(ExtratoServiceImpl service) {
        this.service = service;
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
}
