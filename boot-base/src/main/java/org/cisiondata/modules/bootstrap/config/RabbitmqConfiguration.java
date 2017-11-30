package org.cisiondata.modules.bootstrap.config;

import org.cisiondata.modules.rabbitmq.listener.ChannelAwareMessageListenerImpl;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties.Cache.Channel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ConditionalOnClass({ RabbitTemplate.class, Channel.class })
@EnableConfigurationProperties(RabbitProperties.class)
public class RabbitmqConfiguration {
	
	public static final String DEFAULT_QUEUE = "default-queue"; 
	public static final String DEFAULT_EXCHANGE   = "default-exchange";  
    public static final String DEFAULT_ROUTINGKEY = "default-routingKey"; 
	
    public static final String TOPIC_QUEUE = "topic-queue"; 
    public static final String TOPIC_EXCHANGE   = "topic-exchange";  
    public static final String TOPIC_ROUTINGKEY = "topic.*";
    
//	@Bean
//	@ConfigurationProperties(prefix = "spring.rabbitmq")
//	public RabbitProperties rabbitProperties() {
//		return new RabbitProperties();
//	}
    
    @Bean
    public SimpleMessageConverter serializerMessageConverter() {
    	return new SimpleMessageConverter();
    }
	
	@Bean
	@ConfigurationProperties(prefix = "spring.rabbitmq")
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setPublisherConfirms(true); 
		return connectionFactory;
	}
	
	@Bean(name = "rabbitTemplate")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)  //必须是prototype类型  
    public RabbitTemplate rabbitTemplate() {  
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
		rabbitTemplate.setMessageConverter(serializerMessageConverter());
        return rabbitTemplate;  
    } 
	
	@Bean(name = "dAmqpTemplate")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)  //必须是prototype类型  
    public RabbitTemplate dAmqpTemplate() {  
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
		rabbitTemplate.setExchange(DEFAULT_EXCHANGE);
		rabbitTemplate.setMessageConverter(serializerMessageConverter());
        return rabbitTemplate;  
    }
	
	@Bean(name = "tAmqpTemplate")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)  //必须是prototype类型  
    public RabbitTemplate tAmqpTemplate() {  
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
		rabbitTemplate.setExchange(TOPIC_EXCHANGE);
		rabbitTemplate.setMessageConverter(serializerMessageConverter());
        return rabbitTemplate;  
    }
	
	@Bean
	public AmqpAdmin amqpAdmin() {  
        return new RabbitAdmin(connectionFactory());  
    } 
	
	/**   
    * FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念  
    * HeadersExchange ：通过添加属性key-value匹配  
    * DirectExchange: 按照routingkey分发到指定队列  
    * TopicExchange: 多关键字匹配  
    */  
    @Bean  
    public DirectExchange defaultExchange() {  
    	return new DirectExchange(DEFAULT_EXCHANGE);  
    } 
    
    @Bean
    public Queue defaultQueue() {
    	return new Queue(DEFAULT_QUEUE, true);
    }
    
    @Bean
    public Binding defaultBinding() {
    	return BindingBuilder.bind(defaultQueue()).to(defaultExchange()).with(DEFAULT_ROUTINGKEY);
    }
    
    @Bean  
    public TopicExchange topicExchange() {  
    	return new TopicExchange(TOPIC_EXCHANGE);  
    } 
    
    @Bean
    public Queue topicQueue() {
    	return new Queue(TOPIC_QUEUE, true);
    }
    
    @Bean
    public Binding topicBinding() {
    	return BindingBuilder.bind(topicQueue()).to(topicExchange()).with(TOPIC_ROUTINGKEY);
    }
    
    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer() {
    	SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
    	container.setQueues(defaultQueue());  
        container.setExposeListenerChannel(true);  
        container.setConcurrentConsumers(2);  
        container.setMaxConcurrentConsumers(6);  
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL); //设置确认模式手工确认  
        container.setMessageConverter(serializerMessageConverter());
        container.setMessageListener(channelAwareMessageListener());  
    	return container;
    }
    
    @Bean
    public ChannelAwareMessageListener channelAwareMessageListener() {
    	return new ChannelAwareMessageListenerImpl();
    }

}
