package cn.linkpower.onesimple;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import cn.linkpower.util.MqConnectUtil;

/**
 * 简单消息队列 生产者
 * @author 76519
 *
 */
public class Send {
	private static String simpleQueueName = "test_simple_queue";
	public static void main(String[] args) throws Exception{
		//创建一个连接
		Connection conn = MqConnectUtil.getMqConnection();
		//构建一条通道 --- 从连接中获取一个通道
		Channel createChannel = conn.createChannel();
		//申明一个指定的队列
		createChannel.queueDeclare(simpleQueueName,
				false, false, false, null);
		
		//发送数据信息
		String msg = "hello xiangjiao bunana 22";
		//发送数据
		createChannel.basicPublish("", 
				simpleQueueName, null, msg.getBytes());
		
		System.out.println("---send msg = "+msg);
		
		//关闭流和连接
		createChannel.close();
		conn.close();
	}
}
