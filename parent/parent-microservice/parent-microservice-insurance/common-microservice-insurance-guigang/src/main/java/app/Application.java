package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Description: 贵港社保
 * @author lzh
 * @date 2018年2月9日
 */
@EnableEurekaClient
@SpringBootApplication
@EnableCircuitBreaker
@EnableFeignClients
public class Application { 

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
