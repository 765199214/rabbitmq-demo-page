package cn.linkpower.twoWork;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

import cn.linkpower.util.MqConnectUtil;

public class GetMsg2 {
	private final static String queue_name="test_work_queue";
	public static void main(String[] args) throws IOException, TimeoutException {
		//1、建立连接
		Connection mqConnection = MqConnectUtil.getMqConnection();
		//2、获取信道
		Channel channel = mqConnection.createChannel();
		//3、声明队列
		channel.queueDeclare(queue_name, false, false, false, null);
		//4、信访室接受消息
		DefaultConsumer consumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" get msg new2 = " + message );
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally {
					System.out.println("get msg new2 done");
				}
			}
		};
		//5、创建监听
		channel.basicConsume(queue_name,true, consumer);
	}
}
