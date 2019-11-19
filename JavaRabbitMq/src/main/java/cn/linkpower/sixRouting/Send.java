package cn.linkpower.sixRouting;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import cn.linkpower.util.MqConnectUtil;

public class Send {
	//ctrl + shift +x 小写转大写
	private static final String EXCHANGE_NAME = "test_exchange_direct";
	public static void main(String[] args) throws IOException, TimeoutException {
		Connection mqConnection = MqConnectUtil.getMqConnection();
		Channel channel = mqConnection.createChannel();
		//消息生产者是发送消息至消息转发器中
		channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
		//生产消息
		String msg = "hello world";
		//实名转发（String routingKey）
		channel.basicPublish(EXCHANGE_NAME, "xiangjiao1", null, msg.getBytes());
		System.out.println("发送消息  msg = "+ msg);
		//关闭流
		channel.close();
		mqConnection.close();
	}
}
