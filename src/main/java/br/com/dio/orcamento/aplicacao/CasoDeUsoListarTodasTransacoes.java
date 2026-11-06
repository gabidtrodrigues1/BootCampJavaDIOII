package br.com.dio.orcamento.aplicacao;

import br.com.dio.orcamento.aplicacao.saida.TransacaoSaida;
import br.com.dio.orcamento.dominio.RepositorioTransacao;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CasoDeUsoListarTodasTransacoes {
    private final RepositorioTransacao repositorioTransacao;

    public CasoDeUsoListarTodasTransacoes(RepositorioTransacao repositorioTransacao) {
        this.repositorioTransacao = repositorioTransacao;
    }

    @Tool(name = "listar_todas_transacoes", description = "Lista todas as transações financeiras registradas")
    public List<TransacaoSaida> executar() {
        return repositorioTransacao.buscarTodas().stream()
                .map(TransacaoSaida::de)
                .toList();
    }
}
