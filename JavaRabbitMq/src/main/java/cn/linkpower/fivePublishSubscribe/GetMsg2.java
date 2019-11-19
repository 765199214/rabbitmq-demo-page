package cn.linkpower.fivePublishSubscribe;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;
import cn.linkpower.util.MqConnectUtil;

public class GetMsg2 {

	private static final String queue_name = "test_queue_name_sms";
	private static final String exchange_name = "test_exchange_fanout";

	public static void main(String[] args) throws IOException, TimeoutException {
		// 创建连接对象
		Connection mqConnection = MqConnectUtil.getMqConnection();
		// 创建通信管道
		final Channel channel = mqConnection.createChannel();
		// 声明队列
		channel.queueDeclare(queue_name, false, false, false, null);
		// 绑定消息交换机(将消息交换器和消息队列进行绑定)
		channel.queueBind(queue_name, exchange_name, "");

		// 获取消息
		// 公平分发---每次只分发一个消息
		channel.basicQos(1);

		// 4、信访室接受消息
		DefaultConsumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" get msg "+queue_name+" = " + message);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					System.out.println("get msg "+queue_name+" done");
					// 公平分发--- 消费完成后，需要做相关的回执信息
					channel.basicAck(envelope.getDeliveryTag(), false);
				}
			}
		};
		// 5、创建监听
		// 公平分发--- 同时需要关闭自动“应答”
		channel.basicConsume(queue_name, false, consumer);
	}
}
