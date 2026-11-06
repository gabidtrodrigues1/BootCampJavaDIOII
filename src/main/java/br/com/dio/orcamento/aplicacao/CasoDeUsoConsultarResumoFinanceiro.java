package br.com.dio.orcamento.aplicacao;

import br.com.dio.orcamento.aplicacao.saida.ResumoFinanceiroSaida;
import br.com.dio.orcamento.aplicacao.saida.ResumoPorCategoriaSaida;
import br.com.dio.orcamento.dominio.Categoria;
import br.com.dio.orcamento.dominio.RepositorioTransacao;
import br.com.dio.orcamento.dominio.Transacao;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class CasoDeUsoConsultarResumoFinanceiro {
    private final RepositorioTransacao repositorioTransacao;

    public CasoDeUsoConsultarResumoFinanceiro(RepositorioTransacao repositorioTransacao) {
        this.repositorioTransacao = repositorioTransacao;
    }

    @Tool(name = "consultar_resumo_financeiro", description = "Consulta o total geral gasto, a quantidade de transações e o total por categoria")
    public ResumoFinanceiroSaida executar() {
        List<Transacao> transacoes = repositorioTransacao.buscarTodas();

        Map<Categoria, List<Transacao>> agrupadas = new EnumMap<>(Categoria.class);
        for (Transacao transacao : transacoes) {
            agrupadas.computeIfAbsent(transacao.getCategoria(), categoria -> new ArrayList<>())
                    .add(transacao);
        }

        List<ResumoPorCategoriaSaida> porCategoria = Arrays.stream(Categoria.values())
                .filter(agrupadas::containsKey)
                .map(categoria -> resumirCategoria(categoria, agrupadas.get(categoria)))
                .toList();

        long totalGeralEmCentavos = transacoes.stream()
                .mapToLong(Transacao::getValorEmCentavos)
                .sum();

        return new ResumoFinanceiroSaida(
                BigDecimal.valueOf(totalGeralEmCentavos, 2),
                transacoes.size(),
                porCategoria);
    }

    private ResumoPorCategoriaSaida resumirCategoria(Categoria categoria, List<Transacao> transacoes) {
        long totalEmCentavos = transacoes.stream()
                .mapToLong(Transacao::getValorEmCentavos)
                .sum();

        return new ResumoPorCategoriaSaida(
                categoria,
                BigDecimal.valueOf(totalEmCentavos, 2),
                transacoes.size());
    }
}
