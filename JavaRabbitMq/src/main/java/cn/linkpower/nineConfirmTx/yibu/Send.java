package cn.linkpower.nineConfirmTx.yibu;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;

import cn.linkpower.util.MqConnectUtil;

/**
 * 普通模式
 * 
 * @author 76519
 *
 */
public class Send {
	private static final String queue_name = "test_queue_confirm_yibu";

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

		// 创建一个有序的集合 保存每一次的tag
		final SortedSet<Long> confirmSortedSet = Collections.synchronizedSortedSet(new TreeSet<Long>());

		// 设置监听事件 异步监听每条消息的成功与失败
		channel.addConfirmListener(new ConfirmListener() {
			//成功时回调(异步的  此时表示没有问题的回调)
			//每回调一次handleAck方法，unconfirm集合删掉相应的一条（multiple=false）或多条（multiple=true）记录。
			public void handleAck(long deliveryTag, boolean multiple) throws IOException {
				if(multiple){
					//当发送多条消息时，他返回可能多条消息的接受情况，也可能返回单条消息的情况
					System.out.println("handleAck --- multiple == true");
					//这里为什么会 +1？
					//SortedSet<Long> java.util.SortedSet.headSet(Long toElement)
					//返回的是当前toElement之前一个的所有视图(不包含 toElement )，要想移除指定的 toElement 再集合中的数据，则需要+1
					confirmSortedSet.headSet(deliveryTag+1).clear();
				}else{
					//单条消息
					System.out.println("handleAck --- multiple == false");
					confirmSortedSet.remove(deliveryTag);
				}
			}
			//失败时回调
			public void handleNack(long deliveryTag, boolean multiple) throws IOException {
				if(multiple){
					System.out.println("handleNack --- multiple == true");
					confirmSortedSet.headSet(deliveryTag+1).clear();
				}else{
					//单条消息
					System.out.println("handleNack --- multiple == false");
					confirmSortedSet.remove(deliveryTag);
				}
			}
		});
		
		String msg = "hello xiangjiao confirm yibu";
		System.out.println("send msg = " + msg);
		
		// 消息持久化
		Builder builder = new Builder();
		builder.deliveryMode(2);
		BasicProperties properties = builder.build();
		
//		//发送多条消息
//		for (int i = 0; i < 100; i++) {
//			//获取下一条消息的tag编号
//			long nextPublishSeqNo = channel.getNextPublishSeqNo();
//			channel.basicPublish("", queue_name, properties, msg.getBytes());
//			//向集合中保存对应的编号信息
//			confirmSortedSet.add(nextPublishSeqNo);
//		}
//		
//		channel.close();
//		mqConnection.close();
		
		while (true) {
			//获取下一条消息的tag编号
			long nextPublishSeqNo = channel.getNextPublishSeqNo();
			channel.basicPublish("", queue_name, properties, msg.getBytes());
			//向集合中保存对应的编号信息
			confirmSortedSet.add(nextPublishSeqNo);
			
		}
	}
}
