package poc.hexagonal.infrastructure.handlers;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

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

  public static ErrorDetailBuilder fromHttpStatus(HttpStatus status) {
    ErrorDetailBuilder builder = builder();
    if (status != null) {
      builder.status(status.value())
          .error(status.getReasonPhrase());
    }
    return builder;

  }
}
