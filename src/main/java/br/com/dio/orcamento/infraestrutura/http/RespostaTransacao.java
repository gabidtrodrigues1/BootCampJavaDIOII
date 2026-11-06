package br.com.dio.orcamento.infraestrutura.http;

import br.com.dio.orcamento.aplicacao.saida.TransacaoSaida;
import br.com.dio.orcamento.dominio.Categoria;

import java.math.BigDecimal;
import java.time.Instant;

public record RespostaTransacao(
        String id,
        String descricao,
        Categoria categoria,
        BigDecimal valor,
        Instant dataHora) {

    public static RespostaTransacao de(TransacaoSaida saida) {
        return new RespostaTransacao(
                saida.id(),
                saida.descricao(),
                saida.categoria(),
                saida.valor(),
                saida.dataHora());
    }
}
