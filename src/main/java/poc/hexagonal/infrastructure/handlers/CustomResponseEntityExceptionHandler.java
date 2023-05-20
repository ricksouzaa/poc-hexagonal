package poc.hexagonal.infrastructure.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import poc.hexagonal.application.core.exceptions.CoreException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  private final MessageSource messageSource;

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleAllException(Exception ex, WebRequest request) {
    return handleExceptionInternal(ex, null, new HttpHeaders(), INTERNAL_SERVER_ERROR, request);
  }

  @ExceptionHandler(CoreException.class)
  public ResponseEntity<Object> handleCoreException(CoreException ex, WebRequest request) {
    ProblemDetail body = createProblemDetail(ex, BAD_REQUEST, ex.getMessage(), ex.getClass().getName(), null, request);
    return handleExceptionInternal(ex, body, new HttpHeaders(), BAD_REQUEST, request);
  }


  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    var body = ProblemDetail.forStatusAndDetail(status, createErrorDetails(ex.getBindingResult()).toString());
    return handleExceptionInternal(ex, body, new HttpHeaders(), status, request);
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
