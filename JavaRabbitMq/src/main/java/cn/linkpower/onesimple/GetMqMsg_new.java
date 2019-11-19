package cn.linkpower.onesimple;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import cn.linkpower.util.MqConnectUtil;

public class GetMqMsg_new {
	private static String simpleQueueName = "test_simple_queue";

	public static void main(String[] args) throws IOException, TimeoutException {
		// 获取连接
		Connection mqConnection = MqConnectUtil.getMqConnection();
		// 构建信道
		Channel createChannel = mqConnection.createChannel();
		
		//在发送操作中申明了消息队列，原则上无需在消费操作中也声明，但为了保险起见，还是需要声明以下
		createChannel.queueDeclare(simpleQueueName, false, false, false, null);

		DefaultConsumer consumer = new DefaultConsumer(createChannel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" get msg new = " + message );
			}
		};
		
		
		//监听队列
		createChannel.basicConsume(simpleQueueName, true, consumer);
	}
}
