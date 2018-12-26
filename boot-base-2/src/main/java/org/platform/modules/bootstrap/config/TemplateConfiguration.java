package org.platform.modules.bootstrap.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
public class TemplateConfiguration {
	
	public ITemplateResolver xmlTemplateResolver() {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setOrder(Integer.valueOf(0));
		templateResolver.setPrefix("templates/");
		templateResolver.setSuffix(".xml");
		templateResolver.setCacheable(false);
		templateResolver.setTemplateMode(TemplateMode.XML);
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setResolvablePatterns(Collections.singleton("*/xml/*"));
		return templateResolver;
	}

	public ITemplateResolver textTemplateResolver() {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setOrder(Integer.valueOf(1));
		templateResolver.setPrefix("templates/");
		templateResolver.setSuffix(".txt");
		templateResolver.setCacheable(false);
		templateResolver.setTemplateMode(TemplateMode.TEXT);
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setResolvablePatterns(Collections.singleton("*/text/*"));
		return templateResolver;
	}

	public ITemplateResolver htmlTemplateResolver() {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setOrder(Integer.valueOf(2));
		templateResolver.setPrefix("templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setCacheable(false);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setResolvablePatterns(Collections.singleton("*/html/*"));
		return templateResolver;
	}

	@Bean(name = "templateEngine")
	public SpringWebFluxTemplateEngine templateEngine() {
		SpringWebFluxTemplateEngine templateEngine = new SpringWebFluxTemplateEngine();
		Set<ITemplateResolver> templateResolvers = new HashSet<ITemplateResolver>();
		templateResolvers.add(xmlTemplateResolver());
		templateResolvers.add(textTemplateResolver());
		templateResolvers.add(htmlTemplateResolver());
		templateEngine.setTemplateResolvers(templateResolvers);
		return templateEngine;
	}

}
