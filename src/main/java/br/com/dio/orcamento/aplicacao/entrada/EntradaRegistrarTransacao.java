package br.com.dio.orcamento.aplicacao.entrada;

import br.com.dio.orcamento.dominio.Categoria;
import org.springframework.ai.tool.annotation.ToolParam;

public record EntradaRegistrarTransacao(
        @ToolParam(description = "Descrição do gasto, por exemplo: compra no mercado") String descricao,
        @ToolParam(description = "Valor do gasto em centavos. Exemplo: R$ 50,00 deve ser enviado como 5000") long valorEmCentavos,
        @ToolParam(description = "Categoria da transação: MERCADO, FARMACIA, AUTOMOVEL, RESTAURANTE, LAZER ou OUTROS") Categoria categoria) {
}
