package org.platform.modules.codegen.service.impl;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.Table;

import org.apache.ibatis.javassist.Modifier;
import org.platform.modules.codegen.service.ICodeGenService;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.file.FileUtils;
import org.platform.utils.reflect.ReflectUtils;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine;

@Service("codeGenService")
public class CodeGenServiceImpl implements ICodeGenService {

	private static final String DAO_SUFFIX = "DAO.java";
	
	private static final String MAPPER_SUFFIX = "Mapper.xml";
	
	private static final String SERVICE_SUFFIX = "Service.java";
	
	private static final String SERVICE_IMPL_SUFFIX = "ServiceImpl.java";
	
	@Resource(name = "templateEngine")
	protected SpringWebFluxTemplateEngine templateEngine = null;
	
	@Override
	public void genGenericCode(Class<?> clazz) throws BusinessException {
		String packageName = clazz.getPackage().getName();
		String module = packageName.substring(packageName.indexOf("modules") + 8, packageName.lastIndexOf("."));
		String entity = clazz.getSimpleName();
		
		String userDir = System.getProperty("user.dir");
		String moduleDirPath = userDir + File.separator + "src" + File.separator + "main" + File.separator + "java" + 
			File.separator + "org" + File.separator + "platform" + File.separator + "modules" + File.separator + module;
		System.err.println(moduleDirPath);
		File moduleDir = new File(moduleDirPath);
		if (!moduleDir.exists()) moduleDir.mkdirs();
		
		Context context = genContext(module, entity, clazz);
		
		String daoDirPath = moduleDirPath + File.separator + "dao";
		File daoDir = new File(daoDirPath);
		if (!daoDir.exists()) daoDir.mkdirs();
		FileUtils.write(daoDirPath + File.separator + entity + DAO_SUFFIX, 
			templateEngine.process("codegen/text/StandardDAO", context));
		
		String mapperDirPath = moduleDirPath + File.separator + "mapper";
		File mapperDir = new File(mapperDirPath);
		if (!mapperDir.exists()) mapperDir.mkdirs();
		FileUtils.write(mapperDirPath + File.separator + entity + MAPPER_SUFFIX, 
			templateEngine.process("codegen/text/StandardMapper", context));
		
		String serviceDirPath = moduleDirPath + File.separator + "service";
		File serviceDir = new File(serviceDirPath);
		if (!serviceDir.exists()) serviceDir.mkdirs();
		FileUtils.write(serviceDirPath + File.separator + "I" + entity + SERVICE_SUFFIX, 
			templateEngine.process("codegen/text/IStandardService", context));
		
		String serviceImplDirPath = moduleDirPath + File.separator + "service" + File.separator + "impl";
		File serviceImplDir = new File(serviceImplDirPath);
		if (!serviceImplDir.exists()) serviceImplDir.mkdirs();
		FileUtils.write(serviceImplDirPath + File.separator + entity + SERVICE_IMPL_SUFFIX, 
			templateEngine.process("codegen/text/StandardServiceImpl", context));
		
	}
	
	private Context genContext(String module, String entity, Class<?> clazz) {
		Context context = new Context();
		context.setVariable("module", module);
		context.setVariable("entity", entity);
		context.setVariable("table", clazz.getAnnotation(Table.class).name());
		List<String> attriList = new ArrayList<String>();
		List<String> columnList = new ArrayList<String>();
		Map<String, String> attriColumn = new HashMap<String, String>();
		Field[] fields = ReflectUtils.getFields(clazz);
		for (int i = 0, len = fields.length; i < len; i++) {
			Field field = fields[i];
			String fieldName = field.getName();
			if (Modifier.isStatic(field.getModifiers())) continue;
			if ("id".equalsIgnoreCase(fieldName)) continue;
			String columnName = field.getAnnotation(Column.class).name();
			attriList.add("#{" + fieldName + "}");
			columnList.add(columnName);
			attriColumn.put(fieldName, columnName);
		}
		context.setVariable("attriColumn", attriColumn);
		context.setVariable("attri", String.join(",", attriList));
		context.setVariable("column", String.join(",", columnList));
		return context;
	}
	
}
