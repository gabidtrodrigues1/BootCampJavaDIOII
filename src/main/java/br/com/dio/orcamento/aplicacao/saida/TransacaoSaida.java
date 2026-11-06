package br.com.dio.orcamento.aplicacao.saida;

import br.com.dio.orcamento.dominio.Categoria;
import br.com.dio.orcamento.dominio.Transacao;

import java.math.BigDecimal;
import java.time.Instant;

public record TransacaoSaida(
        String id,
        String descricao,
        Categoria categoria,
        BigDecimal valor,
        Instant dataHora) {

    public static TransacaoSaida de(Transacao transacao) {
        return new TransacaoSaida(
                transacao.getId().uuid().toString(),
                transacao.getDescricao(),
                transacao.getCategoria(),
                transacao.valorEmReais(),
                transacao.getDataHora());
    }
}
