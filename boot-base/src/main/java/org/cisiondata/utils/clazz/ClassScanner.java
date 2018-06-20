package org.cisiondata.utils.clazz;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

public class ClassScanner {
	
	private String[] packages = null;
	private ClassResourceHandler handler = null;
	
	public ClassScanner(String[] packages, ClassResourceHandler handler) {
		this.packages = packages;
		this.handler = handler;
	}
	
	public void scan() {
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
		if (!ArrayUtils.isEmpty(packages) && handler != null) {
			try {
				for (String entityPackage : packages) {
					String packagePath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX 
							+ resolveBasePackage(entityPackage)+"/**/*.class";
					Resource[] resources = resourcePatternResolver.getResources(packagePath);
					for (Resource resource : resources) {
						MetadataReader meta = metadataReaderFactory.getMetadataReader(resource);
						handler.handle(meta);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	protected String resolveBasePackage(String basePackage) {
		return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
	}
	
	public interface ClassResourceHandler {
		void handle(MetadataReader meta);
	}
}
