package ai.acintyo.transactions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class AcintyoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcintyoApplication.class, args);
		log.info("Application Started successfully");
		
	}
	
	@Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:validations");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
