package br.com.dio.orcamento.aplicacao;

import br.com.dio.orcamento.aplicacao.saida.TransacaoSaida;
import br.com.dio.orcamento.dominio.Categoria;
import br.com.dio.orcamento.dominio.RepositorioTransacao;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CasoDeUsoListarTransacoesPorCategoria {
    private final RepositorioTransacao repositorioTransacao;

    public CasoDeUsoListarTransacoesPorCategoria(RepositorioTransacao repositorioTransacao) {
        this.repositorioTransacao = repositorioTransacao;
    }

    @Tool(name = "listar_transacoes_por_categoria", description = "Lista as transações financeiras de uma categoria específica")
    public List<TransacaoSaida> executar(
            @ToolParam(description = "Categoria das transações: MERCADO, FARMACIA, AUTOMOVEL, RESTAURANTE, LAZER ou OUTROS") Categoria categoria) {
        return repositorioTransacao.buscarPorCategoria(categoria).stream()
                .map(TransacaoSaida::de)
                .toList();
    }
}
