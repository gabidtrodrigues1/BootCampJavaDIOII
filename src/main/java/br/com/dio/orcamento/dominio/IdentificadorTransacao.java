package br.com.dio.orcamento.dominio;

import java.util.Objects;
import java.util.UUID;

public record IdentificadorTransacao(UUID uuid) {
    public IdentificadorTransacao {
        Objects.requireNonNull(uuid, "O uuid da transação é obrigatório");
    }

    public IdentificadorTransacao() {
        this(UUID.randomUUID());
    }

    public static IdentificadorTransacao de(String valor) {
        return new IdentificadorTransacao(UUID.fromString(valor));
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}
