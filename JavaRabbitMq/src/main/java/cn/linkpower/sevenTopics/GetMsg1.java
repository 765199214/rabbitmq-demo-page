package cn.linkpower.sevenTopics;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

import cn.linkpower.util.MqConnectUtil;

public class GetMsg1 {
	private static final String QUEUE_NAME = "test_queue_topic_email";
	private static final String EXCHANGE_NAME = "test_exchange_topics";
	public static void main(String[] args) throws IOException, TimeoutException {
		Connection mqConnection = MqConnectUtil.getMqConnection();
		final Channel channel = mqConnection.createChannel();
		//申明队列
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		//绑定队列至指定的交换机（并写明指定的routingkey）
		channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "goods.add");
		channel.basicQos(1);
		//采取新接口方式   接受消息
		DefaultConsumer consumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				System.out.println(" goods.add  get msg = "+new String(body,"UTF-8"));
				//做应答操作
				channel.basicAck(envelope.getDeliveryTag(), false);
			}
		};
		//开启监听
		channel.basicConsume(QUEUE_NAME, false,consumer);
	}
}
