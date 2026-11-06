package br.com.dio.orcamento.infraestrutura.http;

import br.com.dio.orcamento.aplicacao.entrada.EntradaRegistrarTransacao;
import br.com.dio.orcamento.dominio.Categoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RequisicaoTransacao(
        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
        String descricao,

        @Positive(message = "O valor em centavos deve ser maior que zero")
        long valorEmCentavos,

        @NotNull(message = "A categoria é obrigatória")
        Categoria categoria) {

    public EntradaRegistrarTransacao paraEntrada() {
        return new EntradaRegistrarTransacao(descricao, valorEmCentavos, categoria);
    }
}
