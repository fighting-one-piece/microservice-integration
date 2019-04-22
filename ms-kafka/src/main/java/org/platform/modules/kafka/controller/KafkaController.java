package org.platform.modules.kafka.controller;

import javax.annotation.Resource;

import org.platform.modules.kafka.service.IKafkaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class KafkaController {
	
	private Logger LOG = LoggerFactory.getLogger(KafkaController.class);

	@Resource(name = "kafkaService")
	private IKafkaService kafkaService = null;
	
	@RequestMapping(value = "/kafka/send/d1/message", method = RequestMethod.POST)
	public Object send(String topic, Object data) {
		try {
			kafkaService.send(topic, data);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "failure";
		}
		return "success";
	}
	
	@RequestMapping(value = "/kafka/send/ds/message", method = RequestMethod.POST)
	public Object send(String topic, Object... datas) {
		try {
			kafkaService.send(topic, datas);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return "failure";
		}
		return "success";
	}
	
}
