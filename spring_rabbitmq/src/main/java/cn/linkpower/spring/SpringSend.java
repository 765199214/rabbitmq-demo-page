package cn.linkpower.spring;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 消息生产者
 * @author 765199214
 *
 */
public class SpringSend {
	public static void main(String[] args) throws InterruptedException {
		//加载配置文件
		AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:context.xml");
		//RabbitMQ模板
		RabbitTemplate template = ctx.getBean(RabbitTemplate.class);
		//发送消息
		template.convertAndSend("Hello, world!");
		Thread.sleep(1000);// 休眠1秒
		ctx.destroy(); //容器销毁
	}
}
