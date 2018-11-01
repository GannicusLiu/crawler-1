package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableEurekaClient
@SpringBootApplication
@EnableCircuitBreaker
@EnableAsync
@EnableFeignClients
public class InsuranceSXApplication {
	public static void main(String[] args) {
		SpringApplication.run(InsuranceSXApplication.class, args);
	}
}