package br.com.sistemaos.infraestrura.dto;

import br.com.sistemaos.domain.model.StatusOs;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class SalvarOsDTO {
    @NotNull(message = "Cliente é obrigatório")
    private final Long clienteId;
    private final String modelo;
    private final String cor;
    private final StatusOs situacao;
    @Valid
    @NotNull(message = "Orçamento é obrigatório")
    private final SalvarOrcamentoDTO orcamento;
    @Positive(message = "Responsável inválido")
    private final Long responsavelId;

    @JsonCreator
    public SalvarOsDTO(@JsonProperty("clienteId") Long clienteId,
                       @JsonProperty("modelo") String modelo,
                       @JsonProperty("cor") String cor,
                       @JsonProperty("situacao") StatusOs situacao,
                       @JsonProperty("orcamento") SalvarOrcamentoDTO orcamento,
                       @JsonProperty("responsavelId") Long responsavelId) {
        this.clienteId = clienteId;
        this.modelo = modelo;
        this.cor = cor;
        this.situacao = situacao;
        this.orcamento = orcamento;
        this.responsavelId = responsavelId;
    }

    /** Construtor preservado para chamadores/testes escritos antes do campo de responsável. */
    public SalvarOsDTO(Long clienteId, String modelo, String cor, StatusOs situacao,
                       SalvarOrcamentoDTO orcamento) {
        this(clienteId, modelo, cor, situacao, orcamento, null);
    }
}
