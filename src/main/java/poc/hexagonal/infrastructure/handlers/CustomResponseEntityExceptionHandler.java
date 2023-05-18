package poc.hexagonal.infrastructure.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import poc.hexagonal.application.core.exceptions.CoreException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CustomResponseEntityExceptionHandler {

  private final MessageSource messageSource;

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDetail> handleAllException(Exception ex) {
    ErrorDetail error = ErrorDetail.fromHttpStatus(INTERNAL_SERVER_ERROR)
                                   .message(ex.getMessage())
                                   .build();
    return ResponseEntity.internalServerError().body(error);
  }

  @ExceptionHandler(CoreException.class)
  public ResponseEntity<ErrorDetail> handleCoreException(CoreException ex) {
    ErrorDetail error = ErrorDetail.fromHttpStatus(BAD_REQUEST)
                                   .message(getExceptionMessage(ex))
                                   .build();
    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<ErrorDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
    ErrorDetail error = ErrorDetail.fromHttpStatus(BAD_REQUEST)
                                   .details(createErrorDetails(ex.getBindingResult()))
                                   .build();
    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  protected ResponseEntity<ErrorDetail> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
    ErrorDetail error = ErrorDetail.fromHttpStatus(BAD_REQUEST)
                                   .message(ex.getMessage())
                                   .build();
    return ResponseEntity.badRequest().body(error);
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

  private String getExceptionMessage(CoreException ex) {
    return messageSource.getMessage(ex.getClass().getName(), null,
                                    LocaleContextHolder.getLocale());
  }
}
