package org.platform.modules.datatransfer.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.platform.modules.abstr.entity.QueryEntity;
import org.platform.modules.abstr.web.WebResult;
import org.platform.modules.datatransfer.service.IConfigService;
import org.platform.modules.datatransfer.service.IListenService;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.http.HttpUtils;
import org.platform.utils.json.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component("transferListenService")
public class TransferListenServiceImpl implements IListenService, InitializingBean {

	private Logger LOG = LoggerFactory.getLogger(TransferListenServiceImpl.class);
	
	private static String PREFIX = "http://localhost:17000";
	
	//磁盘状态
	private static Map<String, Boolean> diskStatusCache = new LinkedHashMap<String, Boolean>();
		
	//磁盘定义
	private static final String[] disks = new String[] { "C:", "D:", "E:", "F:", "G:", "H:", "I:", "J:", "K:"};
	
	private volatile String currentDisk = null;
	
	private Gson gson = GsonUtils.builder();
	
	@Resource(name = "configService")
	private IConfigService configService = null;
	
	private ExecutorService threadPool = new ThreadPoolExecutor(2, 5, 10, TimeUnit.SECONDS, 
			new LinkedBlockingDeque<Runnable>(50), Executors.defaultThreadFactory(), new CallerRunsPolicy());
	
	@Override
	public void afterPropertiesSet() throws Exception {
		initDiskStatus();
		new Thread(new Runnable() {
			@Override
			public void run() {
				startupListen();
			}
		}).start();
	}
	
	@Override
	public boolean startupListen() throws BusinessException {
		try {
			String path = configService.readSystemConfigValue("transfer.file.o.path");
			LOG.info("transfer startup listen path: {}", path);
			checkDiskStatus(path);
		} catch(Exception e) {
			LOG.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	@Override
	public boolean shutdownListen() throws BusinessException {
		String path = configService.readSystemConfigValue("transfer.file.o.path");
		LOG.info("transfer shutdown listen path: {}", path);
		return true;
	}
	
	@Override
	public String getListenDisk() throws BusinessException {
		return this.currentDisk;
	}
	
	//初始化磁盘状态，存在true，否则false
	private void initDiskStatus() {
		File directory = null;
		for (int i = 0, len = disks.length; i < len; i++) {
			String disk = disks[i];
			directory = new File(disk + File.separator);
			diskStatusCache.put(disk, directory.exists());
			LOG.info("检测磁盘挂载: {} {}", disk, directory.exists());
		}
	}
	
	// 死循环检测每个磁盘状态
	private void checkDiskStatus(String listenDirectory) {
		File directory = null;
		for (;;) {
			for (int i = 0, len = disks.length; i < len; i++) {
				String disk = disks[i];
				directory = new File(disk + File.separator);
				boolean isExist = directory.exists();
				// 如果磁盘现在存在,以前不存在,则表示刚插上U盘
				if (isExist && !diskStatusCache.get(disk)) {
					LOG.info("检测到磁盘挂载: {}", disk);
					currentDisk = disk;
					String targetDirectoryPath = directory + listenDirectory;
					File targetDirectory = new File(targetDirectoryPath);
					if (targetDirectory.exists()) {
						File[] targetFiles = targetDirectory.listFiles();
						List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
						for (int j =0, jLen = targetFiles.length; j < jLen; j++) {
							File targetFile = targetFiles[j];
							tasks.add(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									try {
										parse(targetFile);
										targetFile.delete();
									} catch (Exception e) {
										LOG.error(e.getMessage(), e);
										return false;
									}
									return true;
								}
							});
						}
						int taskNumber = 0;
						try {
							 List<Future<Boolean>> futures = threadPool.invokeAll(tasks);
							 for (int k = 0, kLen = futures.size(); k < kLen; k++) {
								 if (futures.get(k).get()) taskNumber++;
							 }
						} catch (Exception e) {
							LOG.error(e.getMessage(), e);
						}
						LOG.info("本次扫描数: {} 成功处理数: {}", targetFiles.length, taskNumber);
					}
				}
				if (!isExist && diskStatusCache.get(disk)) {
					LOG.info("检测到磁盘卸载: {}", disk);
					currentDisk = null;
				}
				// 每次状态改变时，更新保存的状态
				if (isExist != diskStatusCache.get(disk)) {
					diskStatusCache.put(disk, isExist);
				}
			}
			try {
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	private void parse(File file) throws BusinessException {
		if (StringUtils.isBlank(currentDisk)) return;
		String outputDirectoryPath = currentDisk + File.separator + configService.readSystemConfigValue("transfer.file.i.path");
		File outputDirectory = new File(outputDirectoryPath);
		if (!outputDirectory.exists()) outputDirectory.mkdirs();
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(file));
			Object queryEntityObj = ois.readObject();
			if (null == queryEntityObj) return;
			QueryEntity queryEntity = (QueryEntity) queryEntityObj;
			queryEntity.setTime(new Date());
			LOG.info(PREFIX + queryEntity.getUrl());
			String result = HttpUtils.sendGet(PREFIX + queryEntity.getUrl());
			WebResult webResult = gson.fromJson(result, WebResult.class);
			Object data = webResult.getData();
			if (null != data) result = String.valueOf(data);
			LOG.info(result);
			queryEntity.setResult(result.substring(1));
			queryEntity.setFlag("1".equals(result.substring(0, 1)) ? 1 : 2);
			File outputFile = new File(outputDirectoryPath + File.separator + file.getName());
			oos = new ObjectOutputStream(new FileOutputStream(outputFile));
			oos.writeObject(queryEntity);
			oos.flush();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				if (null != ois) ois.close();
				if (null != oos) oos.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

}
