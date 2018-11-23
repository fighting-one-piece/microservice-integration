package org.platform.modules.bootstrap.config.ds;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class DataSourceContextHolder {

	private static final ThreadLocal<String> context = new ThreadLocal<String>();
	
	private static List<String> allDataSources = new ArrayList<String>();
	private static List<String> masterDataSources = new ArrayList<String>();
	private static List<String> slaveDataSources = new ArrayList<String>();
	
	private static Random random = new Random();

    public static void setDataSource(String value) {
    	if (null == value || "".equals(value)) value = DataSource.MASTER;
    	if ("slave".equals(value)) value = obtainSlaveDataSource();
        context.set(value);
    }

    public static String getDataSource() {
        return null == context.get() ? DataSource.MASTER : context.get();
    }

    public static void clearDataSource() {
        context.remove();
    }
    
    public static void addDataSource(String dataSource){
    	if (null == dataSource || "".equals(dataSource)) return;
    	if (dataSource.startsWith("master")) {
    		masterDataSources.add(dataSource);
    	} else if (dataSource.startsWith("slave")) {
    		slaveDataSources.add(dataSource);
    	}
    	allDataSources.add(dataSource);
    }
    
    public static void addDataSource(Set<Object> dataSources){
    	if (null == dataSources || dataSources.isEmpty()) return;
    	for (Object dataSource : dataSources) {
    		addDataSource((String) dataSource);
    	}
    }
    
    public static boolean containDataSource(String dataSource){
        return allDataSources.contains(dataSource);
    }
    
    public static void removeDataSource(String dataSource){
        allDataSources.remove(dataSource);
        masterDataSources.remove(dataSource);
        slaveDataSources.remove(dataSource);
    }
    
    private static String obtainSlaveDataSource() {
    	if (slaveDataSources.isEmpty()) return DataSource.MASTER;
    	int len = slaveDataSources.size();
    	return slaveDataSources.get(len == 1 ? 0 : random.nextInt(len));
    }
    
}
