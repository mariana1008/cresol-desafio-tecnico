package com.cresol.desafio_tecnico.dto;

import org.springframework.data.domain.Page;
import java.util.List;
import java.util.function.Function;

public class PaginaResponse<T> {

    private final List<T> conteudo;
    private final int pagina;
    private final int tamanho;
    private final int totalPaginas;
    private final long totalElementos;

    public PaginaResponse(List<T> conteudo, int pagina, int tamanho,
                          int totalPaginas, long totalElementos) {
        this.conteudo = conteudo;
        this.pagina = pagina;
        this.tamanho = tamanho;
        this.totalPaginas = totalPaginas;
        this.totalElementos = totalElementos;
    }

    public static <E, T> PaginaResponse<T> paginatedRespomse(Page<E> page, Function<E, T> conversor) {
        return new PaginaResponse<>(
                page.getContent().stream().map(conversor).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements());
    }

    public List<T> getConteudo() { return conteudo; }

    public int getPagina() { return pagina; }

    public int getTamanho() { return tamanho; }

    public int getTotalPaginas() { return totalPaginas; }

    public long getTotalElementos() { return totalElementos; }
}
