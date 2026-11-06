package br.com.dio.orcamento.aplicacao;

import br.com.dio.orcamento.aplicacao.entrada.EntradaRegistrarTransacao;
import br.com.dio.orcamento.aplicacao.saida.TransacaoSaida;
import br.com.dio.orcamento.dominio.RepositorioTransacao;
import br.com.dio.orcamento.dominio.Transacao;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class CasoDeUsoRegistrarTransacao {
    private final RepositorioTransacao repositorioTransacao;

    public CasoDeUsoRegistrarTransacao(RepositorioTransacao repositorioTransacao) {
        this.repositorioTransacao = repositorioTransacao;
    }

    @Tool(name = "registrar_transacao", description = "Registra uma nova transação financeira a partir de uma descrição, valor em centavos e categoria")
    public TransacaoSaida executar(EntradaRegistrarTransacao entrada) {
        var transacao = repositorioTransacao.salvar(
                new Transacao(entrada.descricao(), entrada.valorEmCentavos(), entrada.categoria()));

        return TransacaoSaida.de(transacao);
    }
}
