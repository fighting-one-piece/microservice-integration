package org.cisiondata.modules.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Job {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	
	public static final String P_THREAD_POOL_NUM = "P_THREAD_POOL_NUM";
	public static final String C_THREAD_POOL_NUM = "C_THREAD_POOL_NUM";
	public static final String USE_BLOCKING_QUEUE = "USE_BLOCKING_QUEUE";
	public static final String QUEUE_CAPACITY = "QUEUE_CAPACITY";
	public static final String USE_PRODUCER_MODEL = "USE_PRODUCER_MODEL";
	public static final String USE_CONSUMER_MODEL = "USE_CONSUMER_MODEL";
	public static final String CONSUME_DELAY_SECONDS = "CONSUME_DELAY_SECONDS";
	
	public static final int DEFAULT_P_THREAD_POOL_NUM = 5;
	public static final int DEFAULT_C_THREAD_POOL_NUM = 10;
	public static final int DEFAULT_QUEUE_CAPACITY = 10000;
	public static final int DEFAULT_CONSUME_DELAY_SECONDS = 10;
	
	private Map<String, Object> jobConf = null;

	private ExecutorService pExecutorService = null;
	private ExecutorService cExecutorService = null;
	
	private int pThreadPoolNum = DEFAULT_P_THREAD_POOL_NUM;
	private int cThreadPoolNum = DEFAULT_C_THREAD_POOL_NUM;
	
	private boolean useBlockingQueue = false;
	
	private BlockingQueue<String> queue = null;
	
	private boolean useProducerModel = true;
	private boolean useConsumerModel = true;
	
	private int consumeDelaySeconds = 10;
	
	private List<Future<?>> pFutures = null;
	private List<Future<?>> cFutures = null;
	
	/**
	 * 获取参数配置
	 * @return
	 */
	public Map<String, Object> getJobConf() {
		if (null == jobConf) jobConf = new HashMap<String, Object>();
		return jobConf;
	}
	
	/**
	 * 参数配置
	 * @param jobConf
	 */
	public void setJobConf(Map<String, Object> jobConf) {
		getJobConf().putAll(jobConf);
	}
	
	/**
	 * 参数配置
	 * @param key
	 * @param value
	 */
	public void setJobConf(String key, Object value) {
		getJobConf().put(key, value);
	}
	
	/**
	 * 初始化配置
	 */
	public void initializeJobConf() {}

	/**
	 * 分割总记录数,-1表示不需要分割数据
	 * @return
	 */
	public abstract int readRecordTotalNumber();
	
	/**
	 * 生产者实例
	 * @param params
	 * @return
	 */
	public abstract Task producer(Map<String, Object> params);

	/**
	 * 消费者实例
	 * @param params
	 * @return
	 */
	public abstract Task consumer(Map<String, Object> params);
	
	public void startup() {
		initializeJobConf();
		initializeJobParams();
		if (useProducerModel) startupProducers();
		if (useConsumerModel) startupConsumers();
	}
	
	public void shutdown() {
		if (useProducerModel) {
			for (int i = 0, len = pFutures.size(); i < len; i++) {
				Future<?> future = pFutures.get(i);
				while (!future.isDone()) {}
			}
			pExecutorService.shutdown();
		}
		if (useConsumerModel) {
			for (int i = 0, len = cFutures.size(); i < len; i++) {
				Future<?> future = cFutures.get(i);
				while (!future.isDone()) {}
			}
			cExecutorService.shutdown();
		}
	}
	
	/**
	 * 初始化参数配置
	 * @param jobConf
	 */
	private void initializeJobParams() {
		useProducerModel = (boolean) getJobConf().getOrDefault(USE_PRODUCER_MODEL, true);
		if (useProducerModel) {
			pThreadPoolNum = (int) getJobConf().getOrDefault(P_THREAD_POOL_NUM, DEFAULT_P_THREAD_POOL_NUM);
			pExecutorService = Executors.newFixedThreadPool(pThreadPoolNum);
		}
		useBlockingQueue = (boolean) getJobConf().getOrDefault(USE_BLOCKING_QUEUE, false);
		if (useBlockingQueue) {
			int queueCapaciy = (int) getJobConf().getOrDefault(QUEUE_CAPACITY, DEFAULT_QUEUE_CAPACITY);
			queue = new ArrayBlockingQueue<String>(queueCapaciy);
		}
		useConsumerModel = (boolean) getJobConf().getOrDefault(USE_CONSUMER_MODEL, true);
		if (useConsumerModel) {
			cThreadPoolNum = (int) getJobConf().getOrDefault(C_THREAD_POOL_NUM, DEFAULT_C_THREAD_POOL_NUM);
			cExecutorService = Executors.newFixedThreadPool(cThreadPoolNum);
			consumeDelaySeconds = (int) getJobConf().getOrDefault(
				CONSUME_DELAY_SECONDS, DEFAULT_CONSUME_DELAY_SECONDS);
		}
	}
	
	private void startupProducers() {
		try {
			pFutures = new ArrayList<Future<?>>();
			int recordTotalNumber = readRecordTotalNumber();
			if (recordTotalNumber == -1) {
				for (int i = 0; i < pThreadPoolNum; i++) {
					pFutures.add(pExecutorService.submit(producer(getJobConf())));
				}
			} else {
				int splitNumber = recordTotalNumber / pThreadPoolNum;
				int index = 1;
				for (int i = 0; i < pThreadPoolNum; i++) {
					Map<String, Object> params = new HashMap<String, Object>();
					params.putAll(getJobConf());
					if (useBlockingQueue) params.put("queue", queue);
					params.put("startIndex", index);
					index = (i == (pThreadPoolNum - 1)) ? recordTotalNumber + 1 : index + splitNumber;
					params.put("endIndex", index);
					pFutures.add(pExecutorService.submit(producer(params)));
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	private void startupConsumers() {
		try {
			Thread.sleep(consumeDelaySeconds * 1000);
			cFutures = new ArrayList<Future<?>>();
			for (int i = 0; i < cThreadPoolNum; i++) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.putAll(getJobConf());
				if (useBlockingQueue) params.put("queue", queue);
				cFutures.add(cExecutorService.submit(consumer(params)));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

}
