package cn.linkpower.eightTx;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import cn.linkpower.util.MqConnectUtil;

public class Send {
	private static final String queue_name = "test_work_queue_tx";
	
	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		//1、建立连接
		Connection mqConnection = MqConnectUtil.getMqConnection();
		//2、建立信道(通道)
		Channel channel = mqConnection.createChannel();
		//3、声明队列
		channel.queueDeclare(queue_name, false, false, false, null);
		
		//公平分发---
		//为了开启公平分发操作，在消息消费者发送确认收到的指示后，消息队列才会给这个消费者继续发送下一条消息。
		//此处的 1 表示 限制发送给每个消费者每次最大的消息数。
		channel.basicQos(1);
		
		try {
			//开启事务
			channel.txSelect();
			String string = "hello xiangjiao ";
			System.out.println("send msg = "+string);
			//发送消息
			channel.basicPublish("", queue_name, null, string.getBytes());
			
			//模拟异常操作
			//int a = 10/0;
			//无异常  正常执行，则提交事务操作
			channel.txCommit();
		} catch (Exception e) {
			System.out.println("出现异常     回滚操作");
			channel.txRollback();
		}
		
		//5、使用完毕后，需要及时的关闭流应用
		channel.close();
		mqConnection.close();
	}
}
