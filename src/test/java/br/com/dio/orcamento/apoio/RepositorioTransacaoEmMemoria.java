package br.com.dio.orcamento.apoio;

import br.com.dio.orcamento.dominio.Categoria;
import br.com.dio.orcamento.dominio.RepositorioTransacao;
import br.com.dio.orcamento.dominio.Transacao;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RepositorioTransacaoEmMemoria implements RepositorioTransacao {
    private final Map<UUID, Transacao> transacoes = new LinkedHashMap<>();

    @Override
    public Transacao salvar(Transacao transacao) {
        transacoes.put(transacao.getId().uuid(), transacao);
        return transacao;
    }

    @Override
    public List<Transacao> buscarTodas() {
        return transacoes.values().stream()
                .sorted(Comparator.comparing(Transacao::getDataHora).reversed())
                .toList();
    }

    @Override
    public List<Transacao> buscarPorCategoria(Categoria categoria) {
        return buscarTodas().stream()
                .filter(transacao -> transacao.getCategoria() == categoria)
                .toList();
    }
}
