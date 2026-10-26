package com.cresol.desafio_tecnico.service;

import com.cresol.desafio_tecnico.dto.*;

public interface ContaService {
    ContaResponse abrir(AberturaContaRequest request);
    MovimentacaoResponse depositar(OperacaoRequest request);
    MovimentacaoResponse sacar(OperacaoRequest request);
    TransferenciaResponse transferir(TransferenciaRequest request);
    PaginaResponse<ContaResponse> listarTodasAsContas(String documento, int pagina, int tamanho);
}
