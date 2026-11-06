package br.com.dio.orcamento.infraestrutura.persistencia.repositorio;

import br.com.dio.orcamento.dominio.Categoria;
import br.com.dio.orcamento.infraestrutura.persistencia.entidade.EntidadeTransacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RepositorioEntidadeTransacao extends JpaRepository<EntidadeTransacao, UUID> {
    List<EntidadeTransacao> findAllByOrderByDataHoraDesc();

    List<EntidadeTransacao> findAllByCategoriaOrderByDataHoraDesc(Categoria categoria);
}
