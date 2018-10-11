package org.mybatis.spring;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

public class CustomSqlSessionFactoryBean extends SqlSessionFactoryBean {
	
	private Logger LOG = LoggerFactory.getLogger(CustomSqlSessionFactoryBean.class);

	@Override
	public void afterPropertiesSet() throws Exception {
		super.setTypeAliasesPackage(getEntityClassPackages());
		super.afterPropertiesSet();
	}
	
	private String getEntityClassPackages() {
		Set<String> entityClassPackages = new HashSet<String>();
		Map<String, String> entityToPackage = new HashMap<String, String>();
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		String basePackage = "org.cisiondata.modules";
		try {
			String packagePath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX 
					+ ClassUtils.convertClassNameToResourcePath(
							SystemPropertyUtils.resolvePlaceholders(basePackage)) + "/**/*.class";
			Resource[] resources = resourcePatternResolver.getResources(packagePath);
			for (Resource resource : resources) {
				String entityClassPath = resource.getURL().getPath().replaceAll("/", ".")
						.replaceAll("\\" + File.separator, ".").replace(".class", "");
				String entityClass = entityClassPath.substring(entityClassPath.lastIndexOf(".") + 1);
				String entityClassPackage = entityClassPath.substring(
						entityClassPath.indexOf(basePackage), entityClassPath.lastIndexOf("."));
				if (!entityClassPackage.endsWith(".entity") || entityToPackage.containsKey(entityClass)) continue;
				entityToPackage.put(entityClass, entityClassPackage);
				if (!entityClassPackages.contains(entityClassPackage)) {
					entityClassPackages.add(entityClassPackage);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		StringBuilder sb = new StringBuilder();
		for (String entityClassPackage : entityClassPackages) {
			sb.append(entityClassPackage).append(",");
		}
		if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		LOG.info("TypeAliasesPackage: {}", sb.toString());
		return sb.toString();
	}
	
}
