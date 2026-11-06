package br.com.dio.orcamento.infraestrutura.persistencia;

import br.com.dio.orcamento.dominio.Categoria;
import br.com.dio.orcamento.dominio.RepositorioTransacao;
import br.com.dio.orcamento.dominio.Transacao;
import br.com.dio.orcamento.dominio.IdentificadorTransacao;
import br.com.dio.orcamento.infraestrutura.persistencia.repositorio.RepositorioTransacaoJpa;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(RepositorioTransacaoJpa.class)
class RepositorioTransacaoJpaTest {
    @Autowired
    private RepositorioTransacao repositorio;

    @Test
    void deveSalvarEBuscarTransacao() {
        var transacao = transacao("Compra no mercado", 1000, Categoria.MERCADO, "2026-01-01T10:00:00Z");

        var salva = repositorio.salvar(transacao);

        assertThat(salva.getId()).isEqualTo(transacao.getId());
        assertThat(repositorio.buscarTodas()).hasSize(1);
        assertThat(repositorio.buscarTodas().getFirst().getDescricao()).isEqualTo("Compra no mercado");
    }

    @Test
    void deveBuscarPorCategoriaEOrdenarPorDataDecrescente() {
        repositorio.salvar(transacao("Mercado antigo", 1000, Categoria.MERCADO, "2026-01-01T10:00:00Z"));
        repositorio.salvar(transacao("Farmácia", 500, Categoria.FARMACIA, "2026-01-02T10:00:00Z"));
        repositorio.salvar(transacao("Mercado recente", 2000, Categoria.MERCADO, "2026-01-03T10:00:00Z"));

        assertThat(repositorio.buscarPorCategoria(Categoria.MERCADO))
                .extracting(Transacao::getDescricao)
                .containsExactly("Mercado recente", "Mercado antigo");
        assertThat(repositorio.buscarTodas())
                .extracting(Transacao::getDescricao)
                .containsExactly("Mercado recente", "Farmácia", "Mercado antigo");
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
