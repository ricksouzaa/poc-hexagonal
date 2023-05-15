package poc.hexagonal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PocHexagonalApplication {
  public static void main(String[] args) {
    SpringApplication.run(PocHexagonalApplication.class, args);
  }

}
