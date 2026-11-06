package br.com.dio.orcamento.infraestrutura.ia;

import br.com.dio.orcamento.aplicacao.CasoDeUsoConsultarResumoFinanceiro;
import br.com.dio.orcamento.aplicacao.CasoDeUsoListarTodasTransacoes;
import br.com.dio.orcamento.aplicacao.CasoDeUsoListarTransacoesPorCategoria;
import br.com.dio.orcamento.aplicacao.CasoDeUsoRegistrarTransacao;
import br.com.dio.orcamento.aplicacao.porta.AssistenteFinanceiro;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class AssistenteFinanceiroOpenAI implements AssistenteFinanceiro {
    private final TranscriptionModel modeloTranscricao;
    private final ChatClient clienteChat;
    private final TextToSpeechModel modeloSintese;

    public AssistenteFinanceiroOpenAI(
            TranscriptionModel modeloTranscricao,
            ChatClient.Builder construtorChat,
            @Value("classpath:prompts/mensagem-sistema.st") Resource promptSistema,
            TextToSpeechModel modeloSintese,
            CasoDeUsoRegistrarTransacao casoDeUsoRegistrarTransacao,
            CasoDeUsoListarTodasTransacoes casoDeUsoListarTodasTransacoes,
            CasoDeUsoListarTransacoesPorCategoria casoDeUsoListarTransacoesPorCategoria,
            CasoDeUsoConsultarResumoFinanceiro casoDeUsoConsultarResumoFinanceiro) throws IOException {
        this.modeloTranscricao = modeloTranscricao;
        this.clienteChat = construtorChat
                .defaultSystem(promptSistema.getContentAsString(StandardCharsets.UTF_8))
                .defaultTools(
                        casoDeUsoRegistrarTransacao,
                        casoDeUsoListarTodasTransacoes,
                        casoDeUsoListarTransacoesPorCategoria,
                        casoDeUsoConsultarResumoFinanceiro)
                .build();
        this.modeloSintese = modeloSintese;
    }

    @Override
    public String transcrever(byte[] audio, String nomeArquivo) {
        var recurso = new ByteArrayResource(audio) {
            @Override
            public String getFilename() {
                return nomeArquivo;
            }
        };

        return modeloTranscricao.transcribe(recurso);
    }

    @Override
    public String responder(String mensagem) {
        return clienteChat.prompt()
                .user(mensagem)
                .call()
                .content();
    }

    @Override
    public byte[] sintetizarVoz(String texto) {
        return modeloSintese.call(texto);
    }
}
