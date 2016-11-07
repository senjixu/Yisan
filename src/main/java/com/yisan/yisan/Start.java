package com.yisan.yisan;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class Start 
{
	private static ClassPathXmlApplicationContext context = null;
	public static void main( String[] args ){
		try{
			context = new ClassPathXmlApplicationContext(new String[] {"classpath:applicationContext.xml"});
			
			startUp();
		}catch(Throwable e){
			e.printStackTrace();
		}
	}
	
	public static void startUp(){
		context.start();
		System.out.println("初始化完成...");
	}
/*	
	public static void stop(){
		context.stop();
		System.out.println("closed...");
	}
	
	public static void destroy(){
		context.destroy();
		System.out.println("destroied...");
	}*/
}
