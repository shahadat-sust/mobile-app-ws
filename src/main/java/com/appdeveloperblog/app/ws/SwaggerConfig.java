package com.appdeveloperblog.app.ws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	Contact contact = new Contact(
			"Shahadat Hossain", 
			"http://www.shahadathossain.com", 
			"shahadat.sust@gmail.com");
	List<VendorExtension> vendorExtensions = new ArrayList<>();
	ApiInfo apiInfo = new ApiInfo(
			"Photo app RESTful Web Service Documentation", 
			"This pages documents Photo app RESTful Web Service endpoints", 
			"1.0", 
			"urn:tos", 
			contact, 
			"Apache 2.0", 
			"http://www.apache.org/licenses/LICENSE-2.0",
			vendorExtensions);
	
	@Bean
	public Docket apiDocket() {
		Docket docket = new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo)
				.protocols(new HashSet<>(Arrays.asList("HTTP")))
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.appdeveloperblog.app.ws"))
				.paths(PathSelectors.any())
				.build();
		return docket;
	}
	
}
