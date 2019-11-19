package cn.linkpower.sevenTopics;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import cn.linkpower.util.MqConnectUtil;

public class Send {
	private static final String exchange_name = "test_exchange_topics";

	public static void main(String[] args) throws IOException, TimeoutException {
		Connection mqConnection = MqConnectUtil.getMqConnection();
		Channel channel = mqConnection.createChannel();
		// 申明消息转发器的类型
		channel.exchangeDeclare(exchange_name, BuiltinExchangeType.TOPIC);
		// 消息
		String msg = "xiangjiao  bunana";
		// 推送消息至消息转发器
		String routingKey = "goods.delete";
		channel.basicPublish(exchange_name, routingKey, null, msg.getBytes());
		System.out.println("send  msg = "+msg +" routingKey = "+routingKey);
		channel.close();
		mqConnection.close();
	}
}
