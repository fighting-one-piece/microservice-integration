package org.cisiondata.modules;

import javax.annotation.Resource;

import org.cisiondata.modules.bootstrap.BootstrapApplication;
import org.cisiondata.modules.bootstrap.config.RabbitmqConfiguration;
import org.cisiondata.modules.rabbitmq.service.IRabbitmqV1Service;
import org.cisiondata.utils.serde.SerializerUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootstrapApplication.class)
@WebAppConfiguration 
public class RabbitmqTest {

	@Autowired
	private RabbitTemplate rabbitTemplate  = null;
	
	@Resource(name = "rabbitmqService")
	private IRabbitmqV1Service rabbitmqService = null;
	
	@Test
	public void testProducer() {
		rabbitTemplate.setExchange(RabbitmqConfiguration.DEFAULT_EXCHANGE);
		rabbitTemplate.convertAndSend(RabbitmqConfiguration.DEFAULT_ROUTINGKEY, SerializerUtils.write("hello"));
		rabbitTemplate.convertAndSend(RabbitmqConfiguration.DEFAULT_EXCHANGE, 
				RabbitmqConfiguration.DEFAULT_ROUTINGKEY, SerializerUtils.write("this is a test message"));
	}
	
	@Test
	public void testMQProducer() {
		rabbitmqService.sendMessage("this is a default message!");
		rabbitmqService.sendMessage(RabbitmqConfiguration.DEFAULT_ROUTINGKEY, "this is a default1 message");
		rabbitmqService.sendMessage(RabbitmqConfiguration.DEFAULT_EXCHANGE, 
				RabbitmqConfiguration.DEFAULT_ROUTINGKEY, "this is a default2 message");
	}
	
	@Test
	public void testCustomProducer() {
		DirectExchange exchange = new DirectExchange("c-exchange");
		rabbitmqService.declareExchange(exchange);
		Queue queue = new Queue("c-queue", true);
		rabbitmqService.declareQueue(queue);
		Binding binding = BindingBuilder.bind(queue).to(exchange).with("c-routingkey");
		rabbitmqService.declareBinding(binding);
		rabbitmqService.sendMessage("c-exchange", "c-routingkey", "this is a custom message!");
	}
	
	@Test
	public void testCustomProducer1() {
		rabbitmqService.sendMessage("c-exchange", "c-routingkey", "this is a custom message!");
	}
	
	@Test
	public void testCustomProducer02() {
		TopicExchange exchange = new TopicExchange("t-exchange");
		rabbitmqService.declareExchange(exchange);
		Queue queue = new Queue("t-queue", true);
		rabbitmqService.declareQueue(queue);
		Binding binding = BindingBuilder.bind(queue).to(exchange).with("t-routingkey");
		rabbitmqService.declareBinding(binding);
		rabbitmqService.sendMessage("t-exchange", "t-routingkey", "this is a topic message!");
	}
	
	@Test
	public void testTopicProducer() {
		TopicExchange exchange = new TopicExchange("t-exchange");
		rabbitmqService.declareExchange(exchange);
		Queue queue = new Queue("t-queue", true);
		rabbitmqService.declareQueue(queue);
		Binding binding = BindingBuilder.bind(queue).to(exchange).with("t-routingkey");
		rabbitmqService.declareBinding(binding);
		rabbitmqService.sendMessage("t-exchange", "t-routingkey", "this is a custom message!");
	}
	
}
