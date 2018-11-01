package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @Description: 武汉社保
 */

@EnableEurekaClient
@SpringBootApplication
@EnableCircuitBreaker
@EnableAsync
@EnableFeignClients
//启动服务的main方法

public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
