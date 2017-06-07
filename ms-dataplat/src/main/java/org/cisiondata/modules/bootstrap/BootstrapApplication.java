package org.cisiondata.modules.bootstrap;

import java.util.ArrayList;
import java.util.List;

import org.cisiondata.modules.abstr.web.filter.XssStringJsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = { "org.cisiondata" })
public class BootstrapApplication {

	private static Logger LOG = LoggerFactory.getLogger(BootstrapApplication.class);

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		return objectMapper;
	}

	@Bean
	@Primary
	public ObjectMapper xssObjectMapper(Jackson2ObjectMapperBuilder builder) {
		ObjectMapper objectMapper = builder.createXmlMapper(false).build(); 
		// 注册xss解析器
		SimpleModule xssModule = new SimpleModule("XssStringJsonSerializer");
		xssModule.addSerializer(new XssStringJsonSerializer());
		objectMapper.registerModule(xssModule);
		return objectMapper;
	}
	
	//http://localhost:8080/v2/api-docs
	//http://localhost:8080/swagger-ui.html
	@Bean
    public Docket createApi() {
		List<ResponseMessage> responseMessages = new ArrayList<>();
		responseMessages.add(new ResponseMessageBuilder()
                		.code(500).message("500 message")
                		.responseModel(new ModelRef("Error")).build());
		responseMessages.add(new ResponseMessageBuilder()
                		.code(403).message("403 message")
                		.responseModel(new ModelRef("Forbidden")).build());
        return new Docket(DocumentationType.SWAGGER_2)
        		.apiInfo(apiInfo())
                .select()  //选择那些路径和api会生成document
                .apis(RequestHandlerSelectors.any()) //对所有api进行监控 RequestHandlerSelectors.basePackage(basePackage)
                .paths(PathSelectors.any()) //对所有路径进行监控
                .build()
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, responseMessages);
    }
	
	private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger构建api文档")
                .description("简单优雅的restfun风格")
                .termsOfServiceUrl("http://www.cisiondata.com")
                .version("1.0")
                .build();
    }

	public static void main(String[] args) {
		SpringApplication.run(BootstrapApplication.class, args);
		LOG.info("Server Bootstrap");
	}

}
