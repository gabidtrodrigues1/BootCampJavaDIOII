package br.com.dio.orcamento.aplicacao.saida;

import java.math.BigDecimal;
import java.util.List;

public record ResumoFinanceiroSaida(
        BigDecimal totalGeral,
        long quantidadeTransacoes,
        List<ResumoPorCategoriaSaida> porCategoria) {
}
