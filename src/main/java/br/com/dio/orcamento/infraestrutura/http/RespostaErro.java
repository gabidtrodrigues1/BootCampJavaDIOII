package br.com.dio.orcamento.infraestrutura.http;

import java.time.Instant;
import java.util.List;

public record RespostaErro(
        Instant dataHora,
        int status,
        String erro,
        List<String> detalhes,
        String caminho) {
}
