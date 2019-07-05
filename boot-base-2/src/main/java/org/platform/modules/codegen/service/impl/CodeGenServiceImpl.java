package org.platform.modules.codegen.service.impl;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.ibatis.javassist.Modifier;
import org.platform.modules.codegen.service.ICodeGenService;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.file.FileUtils;
import org.platform.utils.reflect.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine;

@Service("codeGenService")
public class CodeGenServiceImpl implements ICodeGenService {
	
	private Logger LOG = LoggerFactory.getLogger(CodeGenServiceImpl.class);

	private static final String DAO_SUFFIX = "DAO.java";
	
	private static final String MAPPER_SUFFIX = "Mapper.xml";
	
	private static final String SERVICE_SUFFIX = "Service.java";
	
	private static final String SERVICE_IMPL_SUFFIX = "ServiceImpl.java";
	
	@Resource(name = "routingDataSource")
	private AbstractRoutingDataSource routingDataSource = null;
	
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
		
		String createTableSQL = genCreateTableSQL(clazz);
		LOG.info("create table sql: {}", createTableSQL);
		Connection connection = null;
		Statement statement = null;
		try {
			connection = routingDataSource.getConnection();
			statement = connection.createStatement();
			statement.execute(createTableSQL);
			LOG.info("create table success!");
		} catch (SQLException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				if (null != statement) statement.close();
				if (null != connection && !connection.isClosed()) connection.close();
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	private Context genContext(String module, String entity, Class<?> clazz) {
		Context context = new Context();
		context.setVariable("module", module);
		context.setVariable("entity", entity);
		context.setVariable("table", clazz.getAnnotation(Table.class).name());
		List<String> attriList = new ArrayList<String>();
		List<String> columnList = new ArrayList<String>();
		Map<String, String> attriColumn = new HashMap<String, String>();
		Map<String, String> attriStringColumn = new HashMap<String, String>();
		Field[] fields = ReflectUtils.getFields(clazz);
		for (int i = 0, len = fields.length; i < len; i++) {
			Field field = fields[i];
			String fieldName = field.getName();
			if (Modifier.isStatic(field.getModifiers())) continue;
			if ("id".equalsIgnoreCase(fieldName)) continue;
			Column column = field.getAnnotation(Column.class);
			if (null == column) continue;
			String columnName = column.name();
			attriList.add("#{" + fieldName + "}");
			columnList.add(columnName);
			if (String.class.isAssignableFrom(field.getType())) {
				attriStringColumn.put(fieldName, columnName);
			} else {
				attriColumn.put(fieldName, columnName);
			}
		}
		context.setVariable("attriColumn", attriColumn);
		context.setVariable("attriStringColumn", attriStringColumn);
		context.setVariable("attri", String.join(",", attriList));
		context.setVariable("column", String.join(",", columnList));
		return context;
	}
	
	private String genCreateTableSQL(Class<?> clazz) {
		StringBuilder pk1 = new StringBuilder(50);
		StringBuilder pk2 = new StringBuilder(50);
		StringBuilder uk = new StringBuilder(100);
		StringBuilder cc = new StringBuilder(200);
		Field[] fields = ReflectUtils.getFields(clazz);
		for (int i = 0, len = fields.length; i < len; i++) {
			Field field = fields[i];
			if (Modifier.isStatic(field.getModifiers())) continue;
			Column column = field.getAnnotation(Column.class);
			if (null == column) continue;
			Class<?> fieldType = field.getType();
			if (null != field.getAnnotation(Id.class)) {
				pk1.append("`").append(column.name()).append("` BIGINT(20) NOT NULL AUTO_INCREMENT,");
				pk2.append("PRIMARY KEY (`").append(column.name()).append("`)");
				continue;
			}
			genColumn(cc, column, fieldType);
			if (column.unique()) {
				uk.append("UNIQUE KEY `").append(field.getName()).append("Unique` ");
				uk.append("(`").append(column.name()).append("`),");
			}
		}
		if (uk.length() > 0) {
			pk2.append(",");
			uk.deleteCharAt(uk.length() - 1);
		}
		StringBuilder sb = new StringBuilder(500);
		sb.append("CREATE TABLE `");
		String table = clazz.getAnnotation(Table.class).name();
		sb.append(table).append("` (").append("\n");
		sb.append(pk1.toString()).append("\n");
		sb.append(cc.toString());
		sb.append(pk2.toString()).append("\n");
		if (uk.length() > 0) sb.append(uk.toString()).append("\n");
		sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
		return sb.toString();
	}
	
	private void genColumn(StringBuilder sb, Column column, Class<?> fieldType) {
		sb.append("`").append(column.name()).append("` ");
		if (Short.class.isAssignableFrom(fieldType)) {
			sb.append("TINYINT(10)");
		} else if (Integer.class.isAssignableFrom(fieldType)) {
			sb.append("INT(20)");
		} else if (Long.class.isAssignableFrom(fieldType)) {
			sb.append("BIGINT(20)");
		} else if (Float.class.isAssignableFrom(fieldType)) {
			sb.append("FLOAT");
		} else if (Double.class.isAssignableFrom(fieldType)) {
			sb.append("DOUBLE");
		} else if (Boolean.class.isAssignableFrom(fieldType)) {
			sb.append("INT(1)");
		} else if (Date.class.isAssignableFrom(fieldType)) {
			sb.append("DATETIME");
		} else if (String.class.isAssignableFrom(fieldType)) {
			sb.append("VARCHAR(").append(column.length()).append(")");
		}
		sb.append(" ").append(column.nullable() ? "DEFAULT NULL" : "NOT NULL");
		sb.append(",").append("\n");
	}
	
}
