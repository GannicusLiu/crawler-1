package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableEurekaClient
@SpringBootApplication   
@EnableCircuitBreaker
@EnableFeignClients
public class HousingFundSGApplication {

	public static void main(String[] args) {
		SpringApplication.run(HousingFundSGApplication.class, args);
	}
}
