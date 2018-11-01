package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Description: 芜湖公积金
 * @author zcx
 */
@EnableEurekaClient
@SpringBootApplication
@EnableCircuitBreaker
@EnableFeignClients
public class HousingFundWHApplication {
	public static void main(String[] args) {
		SpringApplication.run(HousingFundWHApplication.class, args);
	}
}
