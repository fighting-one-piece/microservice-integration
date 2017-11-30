package org.cisiondata.modules.rabbitmq.listener;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.cisiondata.modules.rabbitmq.service.IConsumeService;
import org.cisiondata.utils.serde.SerializerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.rabbitmq.client.Channel;

public class ChannelAwareMessageListenerImpl implements ChannelAwareMessageListener {
	
	private final Logger LOG = LoggerFactory.getLogger(ChannelAwareMessageListenerImpl.class);
	
	@Autowired(required=true)
	private List<IConsumeService> consumeServiceList = null;

	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		try {
			channel.basicQos(100);
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			MessageProperties properties = message.getMessageProperties();
			String exchange = properties.getReceivedExchange();
			String routingKey = properties.getReceivedRoutingKey();
			LOG.info("--receive confirm-- exchange: {} routingKey: {} message: {} ", exchange, routingKey, SerializerUtils.read(message.getBody()));
			if (StringUtils.isBlank(exchange) || StringUtils.isBlank(routingKey)) return;
			for (int i = 0, len = consumeServiceList.size(); i < len; i++) {
				consumeServiceList.get(i).handleMessage(exchange, routingKey, 
						SerializerUtils.read(message.getBody()));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
		}
	}

}
