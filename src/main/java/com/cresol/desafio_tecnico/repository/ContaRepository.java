package com.cresol.desafio_tecnico.repository;

import com.cresol.desafio_tecnico.model.Conta;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByNumeroAndDigito(Long numero, Integer digito);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
    @Query("select c from Conta c where c.numero = :numero and c.digito = :digito")
    Optional<Conta> buscarParaAtualizar(@Param("numero") Long numero, @Param("digito") Integer digito);

    @Query(value = "select nextval('conta_numero_seq')", nativeQuery = true)
    long proximoNumero();
}