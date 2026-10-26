package com.cresol.desafio_tecnico.service;

import com.cresol.desafio_tecnico.dto.ExtratoResponse;

import java.time.LocalDate;

public interface ExtraroService {
    ExtratoResponse extrato(Long numero, Integer digito, LocalDate inicio, LocalDate fim, int pagina, int tamanho);
}
