package cn.linkpower.fourChijiu;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

import cn.linkpower.util.MqConnectUtil;
/**
 * 消息消费者要实现 “公平分发” 的操作，需要关闭自动应答操作；<br>
 * 同时，在处理完消息后，需要向消息队列做“消费完成”的应答！<br>
 * @author 76519
 *
 */
public class GetMsg2 {
	private final static String queue_name="test_work_queue";
	public static void main(String[] args) throws IOException, TimeoutException {
		//1、建立连接
		Connection mqConnection = MqConnectUtil.getMqConnection();
		//2、获取信道
		final Channel channel = mqConnection.createChannel();
		//3、声明队列
		boolean durable = true;
		channel.queueDeclare(queue_name, durable, false, false, null);
		
		//公平分发---设置每次接受处理的消息数
		channel.basicQos(1);
		
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
					//公平分发----成功处理完成消息后，需要向 消息队列  进行应答操作
					channel.basicAck(envelope.getDeliveryTag(),false);
				}
			}
		};
		//5、创建监听
		//channel.basicConsume(queue_name,true, consumer);
		//公平分发----取消自动应答，更改为手动应答(对应finally中的处理完成后的操作)
		channel.basicConsume(queue_name, false,consumer);
	}
}
