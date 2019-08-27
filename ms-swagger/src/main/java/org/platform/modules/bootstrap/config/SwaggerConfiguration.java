package org.platform.modules.bootstrap.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicate;

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
@ConditionalOnClass({ApiInfoBuilder.class})
public class SwaggerConfiguration {
	
	@Value("${spring.profiles.active}")
	private String profilesActive = null;

	@Bean
    public Docket createApi() {
		List<ResponseMessage> responseMessages = new ArrayList<ResponseMessage>();
		responseMessages.add(new ResponseMessageBuilder()
			.code(403).message("403 Message").responseModel(new ModelRef("Forbidden")).build());
		responseMessages.add(new ResponseMessageBuilder()
    		.code(500).message("500 Message").responseModel(new ModelRef("Error")).build());
		Predicate<String> selector = null;
		if ("production".equalsIgnoreCase(profilesActive)) {
			selector = PathSelectors.none();
		} else {
			selector = PathSelectors.any();
		}
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select()  //选择那些路径和API会生成document
				.apis(RequestHandlerSelectors.any()) //对所有api进行监控 RequestHandlerSelectors.basePackage(basePackage)
				.paths(selector) //对所有路径进行监控
				.build();
				/**
				.enableUrlTemplating(true)
				.forCodeGeneration(true)
				.pathMapping("/api/v1")
				.useDefaultResponseMessages(false)
				.globalResponseMessage(RequestMethod.GET, responseMessages);
				*/
    }
	
	private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
        		.version("1.0")
                .title("项目API文档")
                .description("项目前后端交互API文档")
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .termsOfServiceUrl("http://www.platform.com")
                .build();
    }
	
}
