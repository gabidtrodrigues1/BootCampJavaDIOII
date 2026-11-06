package br.com.dio.orcamento.aplicacao;

import br.com.dio.orcamento.apoio.RepositorioTransacaoEmMemoria;
import br.com.dio.orcamento.aplicacao.entrada.EntradaRegistrarTransacao;
import br.com.dio.orcamento.dominio.Categoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CasoDeUsoRegistrarTransacaoTest {

    @Test
    void deveRegistrarTransacao() {
        var repositorio = new RepositorioTransacaoEmMemoria();
        var casoDeUso = new CasoDeUsoRegistrarTransacao(repositorio);

        var saida = casoDeUso.executar(new EntradaRegistrarTransacao("Compra no mercado", 15000, Categoria.MERCADO));

        assertThat(saida.id()).isNotBlank();
        assertThat(saida.descricao()).isEqualTo("Compra no mercado");
        assertThat(saida.categoria()).isEqualTo(Categoria.MERCADO);
        assertThat(saida.valor()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(repositorio.buscarTodas()).hasSize(1);
    }
}
