package br.com.dio.orcamento.dominio;

public class TransacaoInvalidaException extends RuntimeException {
    public TransacaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
