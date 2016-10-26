package com.yisan.yisan;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class Start 
{
	private static final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath:applicationContext.xml"});
	public static void main( String[] args ){
		startUp();
	}
	
	public static void startUp(){
		context.start();
		System.out.println("初始化完成...");
	}
	
	public static void stop(){
		context.stop();
		System.out.println("closed...");
	}
	
	public static void destroy(){
		context.destroy();
		System.out.println("destroied...");
	}
}
