package cn.linkpower.fivePublishSubscribe;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import cn.linkpower.util.MqConnectUtil;

public class Send {
	
	private static final String exchange_name = "test_exchange_fanout";
	
	public static void main(String[] args) throws IOException, TimeoutException {
		//创建连接对象
		Connection mqConnection = MqConnectUtil.getMqConnection();
		//创建通信管道
		Channel channel = mqConnection.createChannel();
		//申明交换机(分发类型)
		channel.exchangeDeclare(exchange_name, BuiltinExchangeType.FANOUT);
		//发送消息
		String msg = "hello world";
		//匿名转发（String routingKey）
		channel.basicPublish(exchange_name, "", null, msg.getBytes());
		System.out.println("发送消息  msg = "+ msg);
		//关闭流
		channel.close();
		mqConnection.close();
	}
}
