package com.phy0312.childfun.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池管理工具类
 */
public class ThreadUtil {

	private static ExecutorService executorService;

	private static ExecutorService moreExecutorService;
	

	private static ExecutorService otherExecutorService;
	
	/**
	 * 该方法为单线程执行仅适用于应用程序图标刷新
	 * @param command
	 */
	public static void execute(Runnable command) {
		if (executorService == null)
			executorService = Executors.newFixedThreadPool(1);
		
		executorService.execute(command);
	}


	/**
	 * 非固定数量线程池
	 * @param command
	 */
	public static void executeMore(Runnable command) {
		if (moreExecutorService == null)
			moreExecutorService = Executors.newCachedThreadPool();
		
		moreExecutorService.execute(command);
	}
	
	/**
	 * 其它固定数量线程池
	 * @param command
	 */
	public static void executeOther(Runnable command) {
		if (otherExecutorService == null)
			otherExecutorService = Executors.newFixedThreadPool(1);
		
		otherExecutorService.execute(command);
	}
	
}
