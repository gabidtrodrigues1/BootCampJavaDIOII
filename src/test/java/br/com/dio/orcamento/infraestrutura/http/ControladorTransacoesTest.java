package br.com.dio.orcamento.infraestrutura.http;

import br.com.dio.orcamento.aplicacao.CasoDeUsoConsultarResumoFinanceiro;
import br.com.dio.orcamento.aplicacao.CasoDeUsoListarTodasTransacoes;
import br.com.dio.orcamento.aplicacao.CasoDeUsoListarTransacoesPorCategoria;
import br.com.dio.orcamento.aplicacao.CasoDeUsoRegistrarTransacao;
import br.com.dio.orcamento.aplicacao.entrada.EntradaRegistrarTransacao;
import br.com.dio.orcamento.aplicacao.porta.AssistenteFinanceiro;
import br.com.dio.orcamento.aplicacao.saida.ResumoFinanceiroSaida;
import br.com.dio.orcamento.aplicacao.saida.ResumoPorCategoriaSaida;
import br.com.dio.orcamento.aplicacao.saida.TransacaoSaida;
import br.com.dio.orcamento.dominio.Categoria;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ControladorTransacoes.class)
@Import(TratadorGlobalExcecoes.class)
class ControladorTransacoesTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CasoDeUsoRegistrarTransacao casoDeUsoRegistrarTransacao;

    @MockitoBean
    private CasoDeUsoListarTodasTransacoes casoDeUsoListarTodasTransacoes;

    @MockitoBean
    private CasoDeUsoListarTransacoesPorCategoria casoDeUsoListarTransacoesPorCategoria;

    @MockitoBean
    private CasoDeUsoConsultarResumoFinanceiro casoDeUsoConsultarResumoFinanceiro;

    @MockitoBean
    private AssistenteFinanceiro assistenteFinanceiro;

    @Test
    void deveCriarTransacao() throws Exception {
        var saida = transacaoSaida();
        when(casoDeUsoRegistrarTransacao.executar(any())).thenReturn(saida);

        mockMvc.perform(post("/transacoes")
                        .contentType("application/json")
                        .content("""
                                {
                                  "descricao": "Compra no mercado",
                                  "valorEmCentavos": 15000,
                                  "categoria": "MERCADO"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(saida.id()))
                .andExpect(jsonPath("$.descricao").value("Compra no mercado"))
                .andExpect(jsonPath("$.categoria").value("MERCADO"))
                .andExpect(jsonPath("$.valor").value(150.00));

        var captura = ArgumentCaptor.forClass(EntradaRegistrarTransacao.class);
        verify(casoDeUsoRegistrarTransacao).executar(captura.capture());
        assertThat(captura.getValue().descricao()).isEqualTo("Compra no mercado");
        assertThat(captura.getValue().valorEmCentavos()).isEqualTo(15000);
        assertThat(captura.getValue().categoria()).isEqualTo(Categoria.MERCADO);
    }

    @Test
    void deveRetornarErroQuandoDadosForemInvalidos() throws Exception {
        mockMvc.perform(post("/transacoes")
                        .contentType("application/json")
                        .content("""
                                {
                                  "descricao": "",
                                  "valorEmCentavos": 0,
                                  "categoria": "MERCADO"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Dados da requisição inválidos"))
                .andExpect(jsonPath("$.detalhes").isArray());
    }

    @Test
    void deveListarTodasAsTransacoes() throws Exception {
        when(casoDeUsoListarTodasTransacoes.executar()).thenReturn(List.of(transacaoSaida()));

        mockMvc.perform(get("/transacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descricao").value("Compra no mercado"));
    }

    @Test
    void deveListarTransacoesPorCategoria() throws Exception {
        when(casoDeUsoListarTransacoesPorCategoria.executar(Categoria.MERCADO))
                .thenReturn(List.of(transacaoSaida()));

        mockMvc.perform(get("/transacoes/categoria/MERCADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoria").value("MERCADO"));
    }

    @Test
    void deveRetornarErroParaCategoriaInvalida() throws Exception {
        mockMvc.perform(get("/transacoes/categoria/INVALIDA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Parâmetro inválido"))
                .andExpect(jsonPath("$.detalhes[0]").value(containsString("Categoria inválida")));
    }

    @Test
    void deveConsultarResumoFinanceiro() throws Exception {
        var resumo = new ResumoFinanceiroSaida(
                new BigDecimal("150.00"),
                1,
                List.of(new ResumoPorCategoriaSaida(Categoria.MERCADO, new BigDecimal("150.00"), 1)));
        when(casoDeUsoConsultarResumoFinanceiro.executar()).thenReturn(resumo);

        mockMvc.perform(get("/transacoes/resumo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalGeral").value(150.00))
                .andExpect(jsonPath("$.quantidadeTransacoes").value(1))
                .andExpect(jsonPath("$.porCategoria[0].categoria").value("MERCADO"));
    }

    @Test
    void deveProcessarAudioERetornarMp3() throws Exception {
        when(assistenteFinanceiro.transcrever(any(byte[].class), anyString()))
                .thenReturn("gastei 50 reais no mercado");
        when(assistenteFinanceiro.responder("gastei 50 reais no mercado"))
                .thenReturn("Transação registrada no valor de 50 reais.");
        when(assistenteFinanceiro.sintetizarVoz("Transação registrada no valor de 50 reais."))
                .thenReturn(new byte[]{1, 2, 3});

        var arquivo = new MockMultipartFile("file", "audio.m4a", "audio/mp4", "conteudo".getBytes());

        mockMvc.perform(multipart("/transacoes/ia").file(arquivo))
                .andExpect(status().isOk())
                .andExpect(content().contentType("audio/mpeg"))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("resposta.mp3")))
                .andExpect(content().bytes(new byte[]{1, 2, 3}));
    }

    @Test
    void deveRetornarErroQuandoAudioEstiverVazio() throws Exception {
        var arquivo = new MockMultipartFile("file", "audio.m4a", "audio/mp4", new byte[0]);

        mockMvc.perform(multipart("/transacoes/ia").file(arquivo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("O arquivo de áudio é obrigatório"));
    }

    private TransacaoSaida transacaoSaida() {
        return new TransacaoSaida(
                "id-1",
                "Compra no mercado",
                Categoria.MERCADO,
                new BigDecimal("150.00"),
                Instant.parse("2026-01-10T12:00:00Z"));
    }
}
