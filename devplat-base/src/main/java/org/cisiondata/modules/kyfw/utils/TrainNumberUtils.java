package org.cisiondata.modules.kyfw.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

import org.cisiondata.utils.file.DefaultLineHandler;
import org.cisiondata.utils.file.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrainNumberUtils {
	
	private static Logger LOG = LoggerFactory.getLogger(TrainNumberUtils.class);
	
	private static long expiredTime = 0L;
	
	private static ReentrantLock lock = new ReentrantLock();
	
	private static volatile boolean isInitialFinish = false;
	
	private static Map<String, Map<String, Object>> ti = null;
	
	private static List<Map.Entry<String, Integer>> tc = null;
	
	private static List<String> stations = new ArrayList<String>();
	
	static {
		stations.addAll(FileUtils.readFromClasspath(
			"stations/stations.txt", new DefaultLineHandler()));
	}
	
	public static Map<String, Object> obtainTrainNumberInfo() {
		try {
			checkTrainNumberPool();
			while (!isInitialFinish){}
			String trainNumber = obtainTrainNumber();
			return ti.get(trainNumber);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	private static void checkTrainNumberPool() {
		if (!trainNumberPoolExpired()) return;
		if (lock.tryLock()) {
			try {
				System.err.println(Thread.currentThread().getName() + " lock success!");
				initializeTrainNumberPool();
				isInitialFinish = true;
			} finally {
				lock.unlock();
				System.err.println(Thread.currentThread().getName() + " unlock success!");
			}
		} else {
			System.err.println(Thread.currentThread().getName() + " lock failure!");
		}
	}
	
	private static boolean trainNumberPoolExpired() {
		long currentTime = System.currentTimeMillis();
		if (currentTime >= expiredTime) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.HOUR, 3);
			calendar.add(Calendar.MINUTE, 30);
			expiredTime = calendar.getTimeInMillis();
			return true;
		}
		return false;
	}
	
	private static int stationsBatchNumber = 0;
	
	private static ExecutorService executorService = Executors.newCachedThreadPool();
	
	private static void initializeTrainNumberPool() {
		ti = new HashMap<String, Map<String, Object>>();
		Map<String, Integer> tm = new HashMap<String, Integer>();
		List<Callable<List<Map<String, Object>>>> trainNumberTasks = new ArrayList<Callable<List<Map<String, Object>>>>();
		if (stationsBatchNumber == (stations.size() - 1)) stationsBatchNumber = 0;
		String[] stationsBatch = stations.get(stationsBatchNumber++).split("\\$");
		for (int i = 0, len = stationsBatch.length; i < len; i++) {
			String stationsTxt = stationsBatch[i];
			trainNumberTasks.add(new Callable<List<Map<String, Object>>>() {
				@Override
				public List<Map<String, Object>> call() throws Exception {
					String[] seStation = stationsTxt.split("-");
					System.err.println(seStation[0] + " - " + seStation[1]);
					return TrainNumberService.obtainTrainNumberInfos(seStation[0], seStation[1]);
				}
			});
		}
		List<Map<String, Object>> trainNumberInfos = new ArrayList<Map<String, Object>>();
		try {
			List<Future<List<Map<String, Object>>>> fs = executorService.invokeAll(trainNumberTasks);
			for (int i = 0, len = fs.size(); i < len; i++) {
				trainNumberInfos.addAll(fs.get(i).get());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		for (int i = 0, len = trainNumberInfos.size(); i < len; i++) {
			Map<String, Object> trainNumberInfo = trainNumberInfos.get(i);
			String key = (String) trainNumberInfo.get("trainNo") + (String) trainNumberInfo.get("seatType");
			ti.put(key, trainNumberInfo);
			tm.put(key, (int) trainNumberInfo.get("seatCount"));
		}
		tc = new ArrayList<Map.Entry<String, Integer>>(tm.entrySet());
		Collections.sort(tc, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return -o1.getValue().compareTo(o2.getValue());
			}
		});
	}
	
	private static Map.Entry<String, Integer> trainNumber = null;
	
	private static synchronized String obtainTrainNumber() {
		if (null == tc || tc.size() == 0) initializeTrainNumberPool();
		if (null == trainNumber) trainNumber = tc.remove(0);
		int count = trainNumber.getValue();
		if (count <= 0) {
			trainNumber = tc.remove(0);
			count = trainNumber.getValue();
		}
		trainNumber.setValue(--count);
		/**
		System.err.println(Thread.currentThread().getName() + " - " + trainNumber.getKey() + " -only- " + count);
		*/
		return trainNumber.getKey();
	}
	
	public static void main(String[] args) {
		ExecutorService pool = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 1000; i++) {
			pool.submit(new Runnable() {
				@Override
				public void run() {
					Map<String, Object> tni = TrainNumberUtils.obtainTrainNumberInfo();
					System.err.println(Thread.currentThread().getName() + " : " + tni);
				}
			});
		}
	}
}
