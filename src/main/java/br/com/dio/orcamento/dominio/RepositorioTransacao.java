package br.com.dio.orcamento.dominio;

import java.util.List;

public interface RepositorioTransacao {
    Transacao salvar(Transacao transacao);

    List<Transacao> buscarTodas();

    List<Transacao> buscarPorCategoria(Categoria categoria);
}
