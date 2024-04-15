package com.acintyo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@SpringBootApplication
public class AcintyoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcintyoApplication.class, args);
		System.out.println("Hello world");
	}
	
	@Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:validations");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
