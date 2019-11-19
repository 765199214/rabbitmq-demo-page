package cn.linkpower.onesimple;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.rabbitmq.client.ShutdownSignalException;

import cn.linkpower.util.MqConnectUtil;

/**
 * 使用程序代码获取send端发送的消息(从mq中自动取得)
 * @author 76519
 *
 */
public class GetMqMsg_old {
	private static String simpleQueueName = "test_simple_queue";
	public static void main(String[] args) throws IOException, TimeoutException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		//获取连接
		Connection mqConnection = MqConnectUtil.getMqConnection();
		//构建通道
		Channel createChannel = mqConnection.createChannel();
		
		//定义一个队列的消费者
		QueueingConsumer queueingConsumer = new QueueingConsumer(createChannel);
		
		//监听
		createChannel.basicConsume(simpleQueueName, true,queueingConsumer);
		
		//获取数据
		while(true){
			
			Delivery nextDelivery = queueingConsumer.nextDelivery();
			String msg = new String(nextDelivery.getBody());
			System.out.println("get old msg == "+msg);
		}
	}
}
