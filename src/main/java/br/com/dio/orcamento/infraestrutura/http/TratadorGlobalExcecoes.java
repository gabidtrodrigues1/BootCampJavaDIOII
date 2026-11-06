package br.com.dio.orcamento.infraestrutura.http;

import br.com.dio.orcamento.dominio.Categoria;
import br.com.dio.orcamento.dominio.TransacaoInvalidaException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class TratadorGlobalExcecoes {
    private static final Logger log = LoggerFactory.getLogger(TratadorGlobalExcecoes.class);

    @ExceptionHandler(TransacaoInvalidaException.class)
    public ResponseEntity<RespostaErro> tratarTransacaoInvalida(
            TransacaoInvalidaException exception, HttpServletRequest requisicao) {
        return resposta(HttpStatus.BAD_REQUEST, exception.getMessage(), List.of(), requisicao);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespostaErro> tratarValidacao(
            MethodArgumentNotValidException exception, HttpServletRequest requisicao) {
        List<String> detalhes = exception.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();

        return resposta(HttpStatus.BAD_REQUEST, "Dados da requisição inválidos", detalhes, requisicao);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<RespostaErro> tratarParametroInvalido(
            MethodArgumentTypeMismatchException exception, HttpServletRequest requisicao) {
        String detalhe = "categoria".equals(exception.getName())
                ? "Categoria inválida. Valores aceitos: " + Arrays.toString(Categoria.values())
                : "Valor inválido para o parâmetro " + exception.getName();

        return resposta(HttpStatus.BAD_REQUEST, "Parâmetro inválido", List.of(detalhe), requisicao);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespostaErro> tratarCorpoInvalido(
            HttpMessageNotReadableException exception, HttpServletRequest requisicao) {
        return resposta(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido", List.of(), requisicao);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<RespostaErro> tratarArquivoGrande(
            MaxUploadSizeExceededException exception, HttpServletRequest requisicao) {
        return resposta(HttpStatus.PAYLOAD_TOO_LARGE, "O arquivo de áudio excede o tamanho máximo permitido", List.of(), requisicao);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespostaErro> tratarErroInesperado(
            Exception exception, HttpServletRequest requisicao) {
        log.error("Erro inesperado ao processar a requisição", exception);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao processar a requisição", List.of(), requisicao);
    }

    private ResponseEntity<RespostaErro> resposta(
            HttpStatus status, String mensagem, List<String> detalhes, HttpServletRequest requisicao) {
        var erro = new RespostaErro(
                Instant.now(),
                status.value(),
                mensagem,
                detalhes,
                requisicao.getRequestURI());

        return ResponseEntity.status(status).body(erro);
    }
}
