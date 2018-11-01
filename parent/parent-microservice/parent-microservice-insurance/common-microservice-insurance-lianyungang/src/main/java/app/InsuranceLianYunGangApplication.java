package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

@EnableEurekaClient
@SpringBootApplication
@EnableCircuitBreaker
@EnableFeignClients
@EnableRetry
public class InsuranceLianYunGangApplication { 

	public static void main(String[] args) {
		SpringApplication.run(InsuranceLianYunGangApplication.class, args);
	}

}