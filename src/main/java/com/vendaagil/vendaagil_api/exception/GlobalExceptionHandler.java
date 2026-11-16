package com.vendaagil.vendaagil_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroRespostaDTO> handleIllegalArgument(IllegalArgumentException ex) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Requisição Inválida",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroRespostaDTO> handleRuntime(RuntimeException ex) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado ou erro de processamento",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDTO> handleValidation(MethodArgumentNotValidException ex) {
        String mensagens = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErroRespostaDTO erro = new ErroRespostaDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação",
                mensagens
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }
}