package org.platform.modules.kyfw.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

import org.platform.utils.date.DateFormatter;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.http.HttpUtils;
import org.platform.utils.json.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MTrainNumberUtils {
	
	private static Logger LOG = LoggerFactory.getLogger(MTrainNumberUtils.class);
	
	private static long expiredTime = 0L;
	
	private static ReentrantLock lock = new ReentrantLock();
	
	private static volatile boolean isInitialFinish = false;
	
	private static Map<String, Map<String, Object>> ti = null;
	
	private static List<Map.Entry<String, Integer>> tc = null;
	
	private static List<String> stations = new ArrayList<String>();
	
	private static Set<String> trainNumberList = new HashSet<String>();
	
	static {
		
	}
	
	/**
	 * 获取车次信息
	 * @return
	 */
	public static Map<String, Object> obtainTrainNumberInfo() {
		try {
			//判断车次池是否过期,如果过期则初始化
			if (checkTrainNumberPoolExpiredTime()) {
				trainNumberList = new HashSet<String>();
				initializeTrainNumberPool();
				isInitialFinish = true;
			}
			while (!isInitialFinish){}
			String trainNumber = obtainTrainNumber();
			return ti.get(trainNumber);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static List<Map<String, Object>> obtainTrainNumberInfos(String startPlace, String endPlace) {
		return MTrainNumberService.obtainTrainNumberInfos(startPlace, endPlace);
	}
	
	/**
	 * 获取车次列表
	 * @return
	 */
	public static Set<String> obtainTrainNumberList() {
		return trainNumberList;
	}
	
	/**
	 * 清空车次池
	 */
	public static void cleanupTrainNumberPool() {
		tc = new ArrayList<Map.Entry<String, Integer>>();
	}
	
	/**
	 * 判断车次池是否过期
	 * @return
	 */
	private static boolean checkTrainNumberPoolExpiredTime() {
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
	
	/**
	 * 初始化车次池
	 */
	private static void initializeTrainNumberPool() {
		CountDownLatch countDownLatch = new CountDownLatch(1);
		if (lock.tryLock()) {
			try {
				doInitializeTrainNumberPool(countDownLatch);
			} finally {
				lock.unlock();
			}
		} else {
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	private static int stationsBatchNumber = 0;
	
	private static ExecutorService executorService = Executors.newCachedThreadPool();
	
	private static void doInitializeTrainNumberPool(CountDownLatch countDownLatch) {
		ti = new HashMap<String, Map<String, Object>>();
		Map<String, Integer> tm = new HashMap<String, Integer>();
		List<Callable<List<Map<String, Object>>>> trainNumberTasks = new ArrayList<Callable<List<Map<String, Object>>>>();
		if (stations.size() == 0) {
			//stations = trainStationService.readTrainStations();
			if (stations.size() == 0){
				throw new BusinessException("暂时无法获取有效站点!");
			}
		}
		if (stationsBatchNumber == (stations.size() - 1)) {
			stations.clear();
			stationsBatchNumber = 0;
		}
		String[] stationsBatch = stations.get(stationsBatchNumber++).split("\\$");
		for (int i = 0, len = stationsBatch.length; i < len; i++) {
			String stationsTxt = stationsBatch[i];
			trainNumberTasks.add(new Callable<List<Map<String, Object>>>() {
				@Override
				public List<Map<String, Object>> call() throws Exception {
					String[] seStation = stationsTxt.split("-");
					return MTrainNumberService.obtainTrainNumberInfos(seStation[0], seStation[1]);
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
			String trainNumber = ((String) trainNumberInfo.get("trainNumber")).replace("\"", "");
			String key = trainNumber + ":" + (String) trainNumberInfo.get("type");
			ti.put(key, trainNumberInfo);
			tm.put(key, (int) trainNumberInfo.get("count"));
			trainNumberList.add(trainNumber);
		}
		tc = new ArrayList<Map.Entry<String, Integer>>(tm.entrySet());
		Collections.sort(tc, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return -o1.getValue().compareTo(o2.getValue());
			}
		});
		countDownLatch.countDown();
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
		return trainNumber.getKey();
	}
	
}

class MTrainNumberService {
	
	public static Logger LOG = LoggerFactory.getLogger(TrainNumberService.class);
	
	private static String TRAIN_NUMBER_URL = "http://touch.train.qunar.com/api/train/trains2s?startStation=%s&endStation=%s&date=%s&searchType=stasta&bd_source=baidupz&wakeup=1";
	
	private static SimpleDateFormat DSDF = DateFormatter.DATE.get();
	private static SimpleDateFormat TSDF = DateFormatter.TIME.get();
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> obtainTrainNumberInfos(String startPlace, String endPlace) {
		List<Map<String, Object>> trainNumberInfos = new ArrayList<Map<String, Object>>();
		try {
			String url = String.format(TRAIN_NUMBER_URL, startPlace, endPlace, DSDF.format(new Date()), System.currentTimeMillis());
			String response = HttpUtils.sendGet(url);
			Map<String, Object> result = GsonUtils.fromJsonToMap(response);
			Map<String, Object> dataMap = (Map<String, Object>) result.get("dataMap");
			Map<String, Object> directTrainInfo = (Map<String, Object>) dataMap.get("directTrainInfo");
			System.err.println("directTrainInfo: " + directTrainInfo);
			List<Map<String, Object>> trains = (List<Map<String, Object>>) directTrainInfo.get("trains");
			Calendar calendar = Calendar.getInstance();
			String currentDate = DSDF.format(calendar.getTime());
			calendar.add(Calendar.HOUR_OF_DAY, 1);
			Date upperTime = calendar.getTime();
			calendar.add(Calendar.HOUR_OF_DAY, 2);
			Date lowerTime = calendar.getTime();
			for (int i = 0, len = trains.size(); i < len; i++) {
				Map<String, Object> train = trains.get(i);
				Object dTime = train.get("dTime").toString().replace("\"", "");
				System.err.println("dtime: " + currentDate + " " + dTime + ":00");
				Date tDptTime = TSDF.parse(currentDate + " " + dTime + ":00");
				if (tDptTime.before(upperTime) || tDptTime.after(lowerTime)) continue;
				List<Map<String, Object>> ticketInfos = (List<Map<String, Object>>) train.get("ticketInfos"); 
				for (int j = 0, jLen = ticketInfos.size(); j < jLen; j++) {
					Map<String, Object> ticketInfo = ticketInfos.get(j);
					int count = Integer.parseInt(String.valueOf(ticketInfo.get("count")));
					if (count <= 0) continue;
					Map<String, Object> trainNumberInfo = new HashMap<String, Object>();
					trainNumberInfo.put("trainNumber", train.get("trainNumber").toString());
					trainNumberInfo.put("dStation", train.get("dStation").toString());
					trainNumberInfo.put("aStation", train.get("aStation").toString());
					trainNumberInfo.put("dTimeStr", train.get("dTimeStr").toString());
					trainNumberInfo.put("aTimeStr", train.get("aTimeStr").toString());
					trainNumberInfo.put("date", train.get("date").toString().replace("\"", ""));
					trainNumberInfo.put("distance", train.get("distance").toString());
					trainNumberInfo.put("timeInMinute", train.get("timeInMinute").toString());
					trainNumberInfo.put("price", ticketInfo.get("price").toString());
					trainNumberInfo.put("type", ticketInfo.get("type").toString());
					trainNumberInfo.put("ticketId",  ticketInfo.get("ticketId").toString());
					trainNumberInfo.put("count", count);
					trainNumberInfo.put("dTime", dTime);
					trainNumberInfos.add(trainNumberInfo);
				}
			}
		} catch (NumberFormatException nfe) {
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return trainNumberInfos;
	}
	
}
