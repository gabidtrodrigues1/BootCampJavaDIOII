package br.com.dio.orcamento.aplicacao.porta;

public interface AssistenteFinanceiro {
    String transcrever(byte[] audio, String nomeArquivo);

    String responder(String mensagem);

    byte[] sintetizarVoz(String texto);
}
