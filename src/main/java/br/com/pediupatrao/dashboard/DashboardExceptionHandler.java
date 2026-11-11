package br.com.pediupatrao.dashboard;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = DashboardController.class)
public class DashboardExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErroDashboardApi> tratarRegraDeNegocio(RuntimeException exception) {
        return ResponseEntity.badRequest().body(new ErroDashboardApi(
                LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), exception.getMessage()));
    }
}
