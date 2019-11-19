package cn.linkpower.nineConfirmTx.two;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import cn.linkpower.util.MqConnectUtil;

/**
 * 普通模式
 * 
 * @author 76519
 *
 */
public class Send {
	// 之前设置的事务机制的对了 此处做测试能否和confirm机制共存
	// private static final String queue_name = "test_work_queue_tx";
	private static final String queue_name = "test_queue_confirm2";

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		// 1、建立连接
		Connection mqConnection = MqConnectUtil.getMqConnection();
		// 2、建立信道(通道)
		Channel channel = mqConnection.createChannel();
		// 3、声明队列(开启持久化队列)
		channel.queueDeclare(queue_name, true, false, false, null);
		// 开启confirm
		channel.confirmSelect();
		channel.basicQos(1);

		String string = "hello xiangjiao confirm";
		System.out.println("send msg = " + string);
		// 消息持久化
		Builder builder = new Builder();
		builder.deliveryMode(2);
		BasicProperties properties = builder.build();
		// 发送消息
		for (int i = 0; i < 10; i++) {
			channel.basicPublish("", queue_name, properties, string.getBytes());
		}
		//confirm 普通方式  判断消息发送成功还是失败
		if(!channel.waitForConfirms()){
			System.out.println("send msg failed");
		}else{
			System.out.println("send msg success");
		}

		channel.close();
		mqConnection.close();
	}
}
