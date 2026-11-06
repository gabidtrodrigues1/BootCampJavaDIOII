package br.com.dio.orcamento.aplicacao;

import br.com.dio.orcamento.apoio.RepositorioTransacaoEmMemoria;
import br.com.dio.orcamento.aplicacao.saida.TransacaoSaida;
import br.com.dio.orcamento.dominio.Categoria;
import br.com.dio.orcamento.dominio.Transacao;
import br.com.dio.orcamento.dominio.IdentificadorTransacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class CasoDeUsoListarTransacoesTest {
    private RepositorioTransacaoEmMemoria repositorio;

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioTransacaoEmMemoria();
        repositorio.salvar(new Transacao(
                new IdentificadorTransacao(),
                "Mercado antigo",
                1000,
                Categoria.MERCADO,
                Instant.parse("2026-01-01T10:00:00Z")));
        repositorio.salvar(new Transacao(
                new IdentificadorTransacao(),
                "Mercado recente",
                2000,
                Categoria.MERCADO,
                Instant.parse("2026-01-02T10:00:00Z")));
        repositorio.salvar(new Transacao(
                new IdentificadorTransacao(),
                "Farmácia",
                500,
                Categoria.FARMACIA,
                Instant.parse("2026-01-03T10:00:00Z")));
    }

    @Test
    void deveListarTodasAsTransacoesDaMaisRecenteParaAMaisAntiga() {
        var casoDeUso = new CasoDeUsoListarTodasTransacoes(repositorio);

        var saida = casoDeUso.executar();

        assertThat(saida).extracting(TransacaoSaida::descricao)
                .containsExactly("Farmácia", "Mercado recente", "Mercado antigo");
    }

    @Test
    void deveListarTransacoesPorCategoria() {
        var casoDeUso = new CasoDeUsoListarTransacoesPorCategoria(repositorio);

        var saida = casoDeUso.executar(Categoria.MERCADO);

        assertThat(saida).extracting(TransacaoSaida::descricao)
                .containsExactly("Mercado recente", "Mercado antigo");
    }
}
