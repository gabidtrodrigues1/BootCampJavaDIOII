package br.com.dio.orcamento.infraestrutura.persistencia.entidade;

import br.com.dio.orcamento.dominio.Categoria;
import br.com.dio.orcamento.dominio.IdentificadorTransacao;
import br.com.dio.orcamento.dominio.Transacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transacoes")
public class EntidadeTransacao {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String descricao;

    @Column(name = "valor_em_centavos", nullable = false)
    private long valorEmCentavos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    @Column(name = "data_hora", nullable = false)
    private Instant dataHora;

    protected EntidadeTransacao() {
    }

    public EntidadeTransacao(UUID id, String descricao, long valorEmCentavos, Categoria categoria, Instant dataHora) {
        this.id = id;
        this.descricao = descricao;
        this.valorEmCentavos = valorEmCentavos;
        this.categoria = categoria;
        this.dataHora = dataHora;
    }

    public static EntidadeTransacao de(Transacao transacao) {
        return new EntidadeTransacao(
                transacao.getId().uuid(),
                transacao.getDescricao(),
                transacao.getValorEmCentavos(),
                transacao.getCategoria(),
                transacao.getDataHora());
    }

    public Transacao paraDominio() {
        return new Transacao(
                new IdentificadorTransacao(id),
                descricao,
                valorEmCentavos,
                categoria,
                dataHora);
    }

    public UUID getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public long getValorEmCentavos() {
        return valorEmCentavos;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Instant getDataHora() {
        return dataHora;
    }
}
