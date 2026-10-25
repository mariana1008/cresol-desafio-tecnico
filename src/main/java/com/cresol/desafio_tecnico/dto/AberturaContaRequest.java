package com.cresol.desafio_tecnico.dto;


import com.cresol.desafio_tecnico.util.CpfCnpj;
import jakarta.validation.constraints.NotBlank;

public class AberturaContaRequest {

    @NotBlank
    @CpfCnpj
    private String documento;

    public String getDocumento() { return documento; }

    public void setDocumento(String documento) { this.documento = documento; }
}