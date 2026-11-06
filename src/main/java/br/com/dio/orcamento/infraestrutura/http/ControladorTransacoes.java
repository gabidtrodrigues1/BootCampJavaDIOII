package br.com.dio.orcamento.infraestrutura.http;

import br.com.dio.orcamento.aplicacao.CasoDeUsoConsultarResumoFinanceiro;
import br.com.dio.orcamento.aplicacao.CasoDeUsoListarTodasTransacoes;
import br.com.dio.orcamento.aplicacao.CasoDeUsoListarTransacoesPorCategoria;
import br.com.dio.orcamento.aplicacao.CasoDeUsoRegistrarTransacao;
import br.com.dio.orcamento.aplicacao.porta.AssistenteFinanceiro;
import br.com.dio.orcamento.aplicacao.saida.ResumoFinanceiroSaida;
import br.com.dio.orcamento.dominio.Categoria;
import br.com.dio.orcamento.dominio.TransacaoInvalidaException;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/transacoes")
public class ControladorTransacoes {
    private static final MediaType AUDIO_MPEG = MediaType.parseMediaType("audio/mpeg");

    private final CasoDeUsoRegistrarTransacao casoDeUsoRegistrarTransacao;
    private final CasoDeUsoListarTodasTransacoes casoDeUsoListarTodasTransacoes;
    private final CasoDeUsoListarTransacoesPorCategoria casoDeUsoListarTransacoesPorCategoria;
    private final CasoDeUsoConsultarResumoFinanceiro casoDeUsoConsultarResumoFinanceiro;
    private final AssistenteFinanceiro assistenteFinanceiro;

    public ControladorTransacoes(
            CasoDeUsoRegistrarTransacao casoDeUsoRegistrarTransacao,
            CasoDeUsoListarTodasTransacoes casoDeUsoListarTodasTransacoes,
            CasoDeUsoListarTransacoesPorCategoria casoDeUsoListarTransacoesPorCategoria,
            CasoDeUsoConsultarResumoFinanceiro casoDeUsoConsultarResumoFinanceiro,
            AssistenteFinanceiro assistenteFinanceiro) {
        this.casoDeUsoRegistrarTransacao = casoDeUsoRegistrarTransacao;
        this.casoDeUsoListarTodasTransacoes = casoDeUsoListarTodasTransacoes;
        this.casoDeUsoListarTransacoesPorCategoria = casoDeUsoListarTransacoesPorCategoria;
        this.casoDeUsoConsultarResumoFinanceiro = casoDeUsoConsultarResumoFinanceiro;
        this.assistenteFinanceiro = assistenteFinanceiro;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RespostaTransacao criar(@Valid @RequestBody RequisicaoTransacao requisicao) {
        return RespostaTransacao.de(casoDeUsoRegistrarTransacao.executar(requisicao.paraEntrada()));
    }

    @GetMapping
    public List<RespostaTransacao> listarTodas() {
        return casoDeUsoListarTodasTransacoes.executar().stream()
                .map(RespostaTransacao::de)
                .toList();
    }

    @GetMapping("/categoria/{categoria}")
    public List<RespostaTransacao> listarPorCategoria(@PathVariable Categoria categoria) {
        return casoDeUsoListarTransacoesPorCategoria.executar(categoria).stream()
                .map(RespostaTransacao::de)
                .toList();
    }

    @GetMapping("/resumo")
    public ResumoFinanceiroSaida consultarResumo() {
        return casoDeUsoConsultarResumoFinanceiro.executar();
    }

    @PostMapping(value = "/ia", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/mpeg")
    public ResponseEntity<Resource> processarAudio(@RequestParam("file") MultipartFile arquivo) throws IOException {
        if (arquivo.isEmpty()) {
            throw new TransacaoInvalidaException("O arquivo de áudio é obrigatório");
        }

        String nomeArquivo = arquivo.getOriginalFilename() != null
                ? arquivo.getOriginalFilename()
                : "audio.m4a";

        String transcricao = assistenteFinanceiro.transcrever(arquivo.getBytes(), nomeArquivo);
        String resposta = assistenteFinanceiro.responder(transcricao);
        byte[] audioResposta = assistenteFinanceiro.sintetizarVoz(resposta);

        var recurso = new ByteArrayResource(audioResposta);

        return ResponseEntity.ok()
                .contentType(AUDIO_MPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("resposta.mp3")
                                .build()
                                .toString())
                .body(recurso);
    }
}
