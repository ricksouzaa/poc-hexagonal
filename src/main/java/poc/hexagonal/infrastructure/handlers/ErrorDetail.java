package poc.hexagonal.infrastructure.handlers;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Getter
@Builder
@JsonInclude(NON_EMPTY)
public class ErrorDetail {
  private int          status;
  private String       error;
  private String       message;
  private List<String> details;

  public static ErrorDetailBuilder fromHttpStatusCode(HttpStatusCode statusCode) {
    ErrorDetailBuilder builder = builder();
    HttpStatus status = HttpStatus.resolve(statusCode.value());
    if (status != null) {
      builder.status(status.value())
          .error(status.getReasonPhrase());
    }
    return builder;

  }
}
