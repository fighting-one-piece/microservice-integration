package org.platform.modules.bootstrap.service.impl;

import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.platform.modules.abstr.entity.RequestMessage;
import org.platform.modules.kafka.service.IKafkaService;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.web.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Order(3)
@Service("messageQueueHandlerService")
public class MessageQueueHandlerServiceImpl extends AbstractHandlerChainServiceImpl {

	private Logger LOG = LoggerFactory.getLogger(MessageQueueHandlerServiceImpl.class);
	
	private static final String TOPIC = "platform-accesslog";

	@Resource(name = "kafkaService")
	private IKafkaService kafkaService = null;
	
	private ExecutorService threadPool = new ThreadPoolExecutor(2, 5, 10, TimeUnit.SECONDS, 
			new LinkedBlockingDeque<Runnable>(50), Executors.defaultThreadFactory(), new DiscardPolicy());

	@Override
	public Object[] postHandle(HttpServletRequest request, Object result) throws BusinessException {
		String requestUrl = request.getServletPath();
		try {
			RequestMessage requestMessage = wrapperRequestMessage(request, requestUrl, result);
			threadPool.submit(new Runnable() {
				@Override
				public void run() {
					kafkaService.send(TOPIC, requestMessage);
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
