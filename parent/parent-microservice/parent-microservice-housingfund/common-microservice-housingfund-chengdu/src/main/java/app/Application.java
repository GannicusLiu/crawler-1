package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @Description: 成都公积金
 * @author tz
 */
@EnableEurekaClient
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
@EnableFeignClients
public class Application { 

	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "false");
		SpringApplication.run(Application.class, args);
	}

}
