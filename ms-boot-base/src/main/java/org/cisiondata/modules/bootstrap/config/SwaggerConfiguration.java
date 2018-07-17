package org.cisiondata.modules.bootstrap.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@ConditionalOnClass({ApiInfoBuilder.class})
public class SwaggerConfiguration {

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
                .enableUrlTemplating(true)
                .forCodeGeneration(true)
                .pathMapping("/api/v1")
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, responseMessages);
    }
	
	private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("项目构建API文档")
                .description("项目前后端交互构建API文档")
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .termsOfServiceUrl("http://www.cisiondata.com")
                .version("1.0")
                .build();
    }
	
}
