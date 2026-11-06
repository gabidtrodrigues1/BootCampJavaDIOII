package br.com.dio.orcamento.dominio;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransacaoTest {

    @Test
    void deveRejeitarDescricaoVazia() {
        assertThatThrownBy(() -> new Transacao("   ", 1000, Categoria.MERCADO))
                .isInstanceOf(TransacaoInvalidaException.class)
                .hasMessageContaining("descrição");
    }

    @Test
    void deveRejeitarValorMenorOuIgualAZero() {
        assertThatThrownBy(() -> new Transacao("Mercado", 0, Categoria.MERCADO))
                .isInstanceOf(TransacaoInvalidaException.class)
                .hasMessageContaining("maior que zero");

        assertThatThrownBy(() -> new Transacao("Mercado", -100, Categoria.MERCADO))
                .isInstanceOf(TransacaoInvalidaException.class)
                .hasMessageContaining("maior que zero");
    }

    @Test
    void deveRejeitarCategoriaNula() {
        assertThatThrownBy(() -> new Transacao("Mercado", 1000, null))
                .isInstanceOf(TransacaoInvalidaException.class)
                .hasMessageContaining("categoria");
    }

    @Test
    void deveConverterCentavosParaReais() {
        var transacao = new Transacao("Mercado", 1290, Categoria.MERCADO);

        assertThat(transacao.valorEmReais()).isEqualByComparingTo(new BigDecimal("12.90"));
    }

    @Test
    void deveRemoverEspacosDaDescricao() {
        var transacao = new Transacao("  Mercado  ", 1000, Categoria.MERCADO);

        assertThat(transacao.getDescricao()).isEqualTo("Mercado");
    }

    @Test
    void deveReconstruirTransacaoComIdEDataInformados() {
        var id = new IdentificadorTransacao();
        var dataHora = Instant.parse("2026-01-10T12:00:00Z");

        var transacao = new Transacao(id, "Farmácia", 2500, Categoria.FARMACIA, dataHora);

        assertThat(transacao.getId()).isEqualTo(id);
        assertThat(transacao.getDataHora()).isEqualTo(dataHora);
    }
}
