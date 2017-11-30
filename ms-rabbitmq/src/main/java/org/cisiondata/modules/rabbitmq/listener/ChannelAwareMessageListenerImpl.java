package org.cisiondata.modules.rabbitmq.listener;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.cisiondata.modules.rabbitmq.service.IConsumeService;
import org.cisiondata.utils.serde.SerializerUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.rabbitmq.client.Channel;

public class ChannelAwareMessageListenerImpl implements ChannelAwareMessageListener {
	
	@Autowired(required=true)
	private List<IConsumeService> consumerServiceList = null;

	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); //确认消息成功消费  
		MessageProperties properties = message.getMessageProperties();
		String routingKey = properties.getReceivedRoutingKey();
		System.out.println("listener receive routingKey: " + routingKey);
		if (StringUtils.isBlank(routingKey)) return;
		for (int i = 0, len = consumerServiceList.size(); i < len; i++) {
			consumerServiceList.get(i).handleMessage(routingKey, 
					SerializerUtils.read(message.getBody()));
		}
	}

}
