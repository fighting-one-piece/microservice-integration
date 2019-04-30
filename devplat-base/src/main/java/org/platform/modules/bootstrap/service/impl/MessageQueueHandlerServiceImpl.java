package org.platform.modules.bootstrap.service.impl;

import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.platform.modules.abstr.entity.RequestMessage;
import org.platform.modules.kafka.service.IKafkaService;
import org.platform.modules.system.service.IEmailService;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.web.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Order(3)
@Service("messageQueueHandlerService")
public class MessageQueueHandlerServiceImpl extends AbstractHandlerChainServiceImpl {

	private Logger LOG = LoggerFactory.getLogger(MessageQueueHandlerServiceImpl.class);
	
	private static final String TOPIC = "platform-accesslog";
	
	@Resource(name = "emailService")
	private IEmailService emailService = null;

	@Resource(name = "kafkaService")
	private IKafkaService kafkaService = null;
	
	@Value("${spring.profiles.active:development}")
	private String activeEnvironment = null;
	
	private ExecutorService threadPool = new ThreadPoolExecutor(2, 5, 10, TimeUnit.SECONDS, 
		new ArrayBlockingQueue<Runnable>(2000), Executors.defaultThreadFactory(), new DiscardPolicy());

	@Override
	public Object[] postHandle(HttpServletRequest request, Object result) throws BusinessException {
		String requestUrl = request.getServletPath();
		try {
			RequestMessage requestMessage = wrapperRequestMessage(request, requestUrl, result);
			threadPool.submit(new Runnable() {
				@Override
				public void run() {
					ListenableFuture<SendResult<Object, Object>> future = kafkaService.send(TOPIC, requestMessage);
					future.addCallback(
						success -> {
						}, 
						failure -> {
							emailService.send(activeEnvironment + "环境DevPlat项目Kafka服务异常", "请尽快处理Kafka服务异常", 
								new String[]{"592891306@qq.com", "976889989@qq.com", "1106439835@qq.com"});
							LOG.error("{} message {}-{}-{}-{}-{}", TOPIC, requestMessage.getIpAddress(), requestMessage.getUrl(), 
								requestMessage.getParams(), requestMessage.getAccount(), requestMessage.getTime());
							LOG.error(failure.getMessage(), failure);
						}
					);
				}
			});
		} catch (Exception e) {
			LOG.error("message queue url: {} result: {}", requestUrl, result);
			LOG.error(e.getMessage(), e);
		}
		return super.postHandle(request, result);
	}

	private RequestMessage wrapperRequestMessage(HttpServletRequest request, String requestUrl, Object result) {
		RequestMessage requestMessage = new RequestMessage();
		requestMessage.setUrl(requestUrl);
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
			requestMessage.getParams().put(entry.getKey(), entry.getValue()[0]);
		}
		Enumeration<String> attributeNames = request.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String attributeName = attributeNames.nextElement();
			if (!attributeName.startsWith("rda_"))
				continue;
			requestMessage.getAttributes().put(attributeName, request.getAttribute(attributeName));
		}
		requestMessage.setIpAddress(IPUtils.getIPAddress(request));
		requestMessage.setAccount(null);
		requestMessage.setTime(new Date());
		requestMessage.setReturnResult(result);
		return requestMessage;
	}

}
