package br.com.dio.orcamento.aplicacao;

import br.com.dio.orcamento.apoio.RepositorioTransacaoEmMemoria;
import br.com.dio.orcamento.aplicacao.saida.ResumoPorCategoriaSaida;
import br.com.dio.orcamento.dominio.Categoria;
import br.com.dio.orcamento.dominio.Transacao;
import br.com.dio.orcamento.dominio.IdentificadorTransacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class CasoDeUsoConsultarResumoFinanceiroTest {
    private RepositorioTransacaoEmMemoria repositorio;
    private CasoDeUsoConsultarResumoFinanceiro casoDeUso;

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioTransacaoEmMemoria();
        casoDeUso = new CasoDeUsoConsultarResumoFinanceiro(repositorio);
    }

    @Test
    void deveRetornarResumoZeradoQuandoNaoHouverTransacoes() {
        var resumo = casoDeUso.executar();

        assertThat(resumo.totalGeral()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resumo.quantidadeTransacoes()).isZero();
        assertThat(resumo.porCategoria()).isEmpty();
    }

    @Test
    void deveCalcularTotalGeralEAgruparPorCategoria() {
        repositorio.salvar(transacao("Mercado", 1000, Categoria.MERCADO, "2026-01-01T10:00:00Z"));
        repositorio.salvar(transacao("Feira", 2000, Categoria.MERCADO, "2026-01-02T10:00:00Z"));
        repositorio.salvar(transacao("Farmácia", 500, Categoria.FARMACIA, "2026-01-03T10:00:00Z"));

        var resumo = casoDeUso.executar();

        assertThat(resumo.totalGeral()).isEqualByComparingTo(new BigDecimal("35.00"));
        assertThat(resumo.quantidadeTransacoes()).isEqualTo(3);
        assertThat(resumo.porCategoria()).extracting(ResumoPorCategoriaSaida::categoria)
                .containsExactly(Categoria.MERCADO, Categoria.FARMACIA);
        assertThat(resumo.porCategoria().get(0).total()).isEqualByComparingTo(new BigDecimal("30.00"));
        assertThat(resumo.porCategoria().get(0).quantidade()).isEqualTo(2);
        assertThat(resumo.porCategoria().get(1).total()).isEqualByComparingTo(new BigDecimal("5.00"));
    }

    private Transacao transacao(String descricao, long valorEmCentavos, Categoria categoria, String dataHora) {
        return new Transacao(
                new IdentificadorTransacao(),
                descricao,
                valorEmCentavos,
                categoria,
                Instant.parse(dataHora));
    }
}
