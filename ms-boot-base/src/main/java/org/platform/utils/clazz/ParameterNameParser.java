package org.platform.utils.clazz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;

import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public class ParameterNameParser {

	public Map<String, List<String>> parse(String clazz) throws NotFoundException, ClassNotFoundException {
		Map<String, List<String>> paramNames = new HashMap<String, List<String>>();
		ClassPool classPool = ClassPool.getDefault();
		classPool.appendClassPath(new ClassClassPath(ParameterNameParser.class));
		CtClass ctClass = classPool.get(clazz);
		for (CtMethod method : ctClass.getDeclaredMethods()) {
			CodeAttribute codeAttribute = (CodeAttribute) method.getMethodInfo().getAttribute("Code");
			if (codeAttribute == null || javassist.Modifier.isAbstract(method.getModifiers()) || javassist.Modifier.isStatic(method.getModifiers())) {
				continue;
			}
			LocalVariableAttribute localVariableAttribute = (LocalVariableAttribute) codeAttribute.getAttribute("LocalVariableTable");
			List<String> names = new ArrayList<String>();
			for (int i = 0; i < localVariableAttribute.tableLength(); i++) {
				String name = localVariableAttribute.getConstPool().getUtf8Info(localVariableAttribute.nameIndex(i));
				if (name.equals("this")) {
					for (int j = i + 1; j <= i + method.getParameterTypes().length; j++) {
						String paramName = localVariableAttribute.getConstPool().getUtf8Info(localVariableAttribute.nameIndex(j));
						names.add(paramName);
					}
				} else {
					continue;
				}
			}

			Object[][] arr = method.getParameterAnnotations();
			for (int i = 0; i < arr.length; i++) {
				for (int j = 0; j < arr[i].length; j++) {
					if (RequestParam.class.isAssignableFrom(arr[i][j].getClass())) {
						RequestParam requestParam = (RequestParam) arr[i][j];
						if (StringUtils.isNotBlank(requestParam.value())) {
							names.set(i, requestParam.value());
						}
					}
					if (PathVariable.class.isAssignableFrom(arr[i][j].getClass())) {
						PathVariable pathVariable = (PathVariable) arr[i][j];
						if (StringUtils.isNotBlank(pathVariable.value())) {
							names.set(i, pathVariable.value());
						}
					}
					if (DateTimeFormat.class.isAssignableFrom(arr[i][j].getClass())) {
						DateTimeFormat dateTimeFormat = (DateTimeFormat) arr[i][j];
						names.set(i, names.get(i) + "\b" + dateTimeFormat.pattern());
					}
				}
			}
			StringBuilder sb=new StringBuilder();
			sb.append(method.getName()).append("(");
			for(CtClass type:method.getParameterTypes()){
				sb.append(type.getName()).append(",");
			}
			if(method.getParameterTypes().length>0)
				sb.delete(sb.length()-1, sb.length());
			sb.append(")");
			paramNames.put(sb.toString(), names);
		}
		return paramNames;
	}
	

}
