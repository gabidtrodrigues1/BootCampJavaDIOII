package br.com.dio.orcamento.infraestrutura.persistencia.repositorio;

import br.com.dio.orcamento.dominio.Categoria;
import br.com.dio.orcamento.dominio.RepositorioTransacao;
import br.com.dio.orcamento.dominio.Transacao;
import br.com.dio.orcamento.infraestrutura.persistencia.entidade.EntidadeTransacao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RepositorioTransacaoJpa implements RepositorioTransacao {
    private final RepositorioEntidadeTransacao repositorioEntidadeTransacao;

    public RepositorioTransacaoJpa(RepositorioEntidadeTransacao repositorioEntidadeTransacao) {
        this.repositorioEntidadeTransacao = repositorioEntidadeTransacao;
    }

    @Override
    public Transacao salvar(Transacao transacao) {
        return repositorioEntidadeTransacao.save(EntidadeTransacao.de(transacao)).paraDominio();
    }

    @Override
    public List<Transacao> buscarTodas() {
        return repositorioEntidadeTransacao.findAllByOrderByDataHoraDesc().stream()
                .map(EntidadeTransacao::paraDominio)
                .toList();
    }

    @Override
    public List<Transacao> buscarPorCategoria(Categoria categoria) {
        return repositorioEntidadeTransacao.findAllByCategoriaOrderByDataHoraDesc(categoria).stream()
                .map(EntidadeTransacao::paraDominio)
                .toList();
    }
}
