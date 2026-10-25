package com.cresol.desafio_tecnico.dto;


import com.cresol.desafio_tecnico.util.CpfCnpj;
import jakarta.validation.constraints.NotBlank;

public class AberturaContaRequest {

    @NotBlank
    @CpfCnpj
    private String cpfCnpj;

    public String getCpfCnpj() { return cpfCnpj; }

    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }
}