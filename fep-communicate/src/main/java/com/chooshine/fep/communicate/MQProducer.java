package com.chooshine.fep.communicate;

import com.chooshine.rabbitmq.client.EventTemplate;
import com.chooshine.rabbitmq.client.config.EventControlConfig;
import com.chooshine.rabbitmq.client.exception.SendRefuseException;
import com.chooshine.rabbitmq.client.impl.SimpleEventController;

public class MQProducer {	
	private SimpleEventController controller;
	
	private EventTemplate eventTemplate;
	
	public MQProducer(){
		EventControlConfig config = new EventControlConfig(CommunicationServerConstants.defaultMQHost,CommunicationServerConstants.MQPort,
				CommunicationServerConstants.MQUserName, CommunicationServerConstants.MQUserPass, CommunicationServerConstants.MQVirtulHost,
                5000, 0, 0);
		controller = SimpleEventController.getInstance(config);
		eventTemplate = controller.getEopEventTemplate();
		
	}
	public void sendMessage(String Msg) throws SendRefuseException{
		eventTemplate.send(CommunicationServerConstants.defaultMQHost, CommunicationServerConstants.defaultMQExchange, Msg);
	}
}
