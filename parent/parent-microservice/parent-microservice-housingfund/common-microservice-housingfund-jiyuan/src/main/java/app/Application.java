package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @Description: 济源公积金
 * @author tz
 */
@EnableEurekaClient
@SpringBootApplication
@EnableCircuitBreaker
@EnableAsync
@EnableFeignClients
public class Application { 

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
