package br.com.dio.orcamento.dominio;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class Transacao {
    private final IdentificadorTransacao id;
    private final String descricao;
    private final long valorEmCentavos;
    private final Categoria categoria;
    private final Instant dataHora;

    public Transacao(String descricao, long valorEmCentavos, Categoria categoria) {
        this(new IdentificadorTransacao(), descricao, valorEmCentavos, categoria, Instant.now());
    }

    public Transacao(IdentificadorTransacao id, String descricao, long valorEmCentavos, Categoria categoria, Instant dataHora) {
        if (descricao == null || descricao.isBlank()) {
            throw new TransacaoInvalidaException("A descrição da transação é obrigatória");
        }
        if (valorEmCentavos <= 0) {
            throw new TransacaoInvalidaException("O valor da transação deve ser maior que zero");
        }
        if (categoria == null) {
            throw new TransacaoInvalidaException("A categoria da transação é obrigatória");
        }

        this.id = Objects.requireNonNull(id, "O identificador da transação é obrigatório");
        this.descricao = descricao.trim();
        this.valorEmCentavos = valorEmCentavos;
        this.categoria = categoria;
        this.dataHora = Objects.requireNonNull(dataHora, "A data e hora da transação é obrigatória");
    }

    public BigDecimal valorEmReais() {
        return BigDecimal.valueOf(valorEmCentavos, 2);
    }

    public IdentificadorTransacao getId() {
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
