package cn.linkpower.fourChijiu;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import cn.linkpower.util.MqConnectUtil;

/**
 * 公平分发--谁做的快谁就多做！<br>
 * 只有在消息消费者成功消费消息，发送消费成功的指令给队列后，消息队列才会继续向该消费者发送下一条消息指令。<br>
 * @author 76519
 *
 */
public class Send {
	private static final String queue_name = "test_work_queue";
	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		//1、建立连接
		Connection mqConnection = MqConnectUtil.getMqConnection();
		//2、建立信道(通道)
		Channel channel = mqConnection.createChannel();
		//3、声明队列(开启持久化)
		boolean durable = true;
		channel.queueDeclare(queue_name, durable, false, false, null);
		
		//公平分发---
		//为了开启公平分发操作，在消息消费者发送确认收到的指示后，消息队列才会给这个消费者继续发送下一条消息。
		//此处的 1 表示 限制发送给每个消费者每次最大的消息数。
		channel.basicQos(1);
		
		//4、发送消息
		for (int i = 0; i < 10; i++) {
			String string = "hello xiangjiao "+i;
			System.out.println("send msg = "+string);
			//发送消息
			//channel.basicPublish("", queue_name, null, string.getBytes());
			//消息持久化测试
			Builder builder = new Builder();
			builder.deliveryMode(2);
			BasicProperties properties = builder.build();
			channel.basicPublish("", queue_name, properties, string.getBytes());
			//消息发送慢一点
			Thread.sleep(i*5);
		}
		//5、使用完毕后，需要及时的关闭流应用
		channel.close();
		mqConnection.close();
	}
}
