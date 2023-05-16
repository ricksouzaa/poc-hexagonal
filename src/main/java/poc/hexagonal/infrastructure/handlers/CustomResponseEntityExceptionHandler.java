package poc.hexagonal.infrastructure.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  private final MessageSource messageSource;

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleAllException(Exception ex, WebRequest request) {
    ErrorDetail error = ErrorDetail.fromHttpStatusCode(INTERNAL_SERVER_ERROR)
                                   .message(ex.getMessage())
                                   .build();

    return handleExceptionInternal(ex, error, new HttpHeaders(), INTERNAL_SERVER_ERROR, request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, HttpStatusCode status, @NonNull WebRequest request) {
    HttpStatus httpStatus = HttpStatus.resolve(status.value());
    ErrorDetail error = ErrorDetail.fromHttpStatusCode(status)
                                   .details(createErrorDetails(ex.getBindingResult()))
                                   .build();
    return handleExceptionInternal(ex, error, headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    HttpStatus httpStatus = HttpStatus.resolve(status.value());
    ErrorDetail error = ErrorDetail.fromHttpStatusCode(status)
                                   .message(ex.getMessage())
                                   .build();
    return handleExceptionInternal(ex, error, headers, status, request);
  }

  private List<String> createErrorDetails(BindingResult bindingResult) {
    return bindingResult
        .getAllErrors()
        .stream()
        .map(this::getMessage)
        .toList();
  }

  private String getMessage(ObjectError field) {
    return messageSource.getMessage(field, LocaleContextHolder.getLocale());
  }
}
