package com.cresol.desafio_tecnico.repository;

import com.cresol.desafio_tecnico.model.Movimentacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    @Query("""
            select m from Movimentacao m
            where m.conta.id = :contaId
              and m.criadaEm >= :inicio
              and m.criadaEm < :fim
            order by m.criadaEm desc, m.id desc
            """)
    Page<Movimentacao> buscarPorPeriodo(@Param("contaId") Long contaId,
                                        @Param("inicio") Instant inicio,
                                        @Param("fim") Instant fim,
                                        Pageable pageable);
}
