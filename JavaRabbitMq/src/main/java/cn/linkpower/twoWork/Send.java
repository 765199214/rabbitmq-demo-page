package cn.linkpower.twoWork;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import cn.linkpower.util.MqConnectUtil;

public class Send {
	private static final String queue_name = "test_work_queue";
	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		//1、建立连接
		Connection mqConnection = MqConnectUtil.getMqConnection();
		//2、建立信道(通道)
		Channel channel = mqConnection.createChannel();
		//3、声明队列
		channel.queueDeclare(queue_name, false, false, false, null);
		
		//4、发送100条消息
		for (int i = 0; i < 50; i++) {
			String string = "hello xiangjiao "+i;
			System.out.println("send msg = "+string);
			//发送消息
			channel.basicPublish("", queue_name, null, string.getBytes());
			//消息发送慢一点
			Thread.sleep(i*20);
		}
		//5、使用完毕后，需要及时的关闭流应用
		channel.close();
		mqConnection.close();
	}
}
