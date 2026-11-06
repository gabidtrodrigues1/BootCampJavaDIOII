package br.com.dio.orcamento.aplicacao.saida;

import br.com.dio.orcamento.dominio.Categoria;

import java.math.BigDecimal;

public record ResumoPorCategoriaSaida(
        Categoria categoria,
        BigDecimal total,
        long quantidade) {
}
