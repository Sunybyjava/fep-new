package com.chooshine.fep.ConstAndTypeDefine;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HXThreadPool extends ThreadPoolExecutor {
	public HXThreadPool() {
		super(5, 50, 10, TimeUnit.SECONDS, new ArrayBlockingQueue(1000),new DiscardOldestPolicy());
	}
	
	private HXThreadPool(int min, int max, int queue) {
		super(min, max, 10, TimeUnit.SECONDS, new ArrayBlockingQueue(queue),new DiscardOldestPolicy());
	}
	
	private HXThreadPool(int min, int max){
		super(min, max, 10, TimeUnit.SECONDS, new ArrayBlockingQueue(1000),new DiscardOldestPolicy());
	}
	
	public static HXThreadPool createThreadPool() {
	    return new HXThreadPool();
	}

	public static HXThreadPool createThreadPool(int minThreads, int maxThreads) {
		return new HXThreadPool(minThreads, maxThreads);
	}

	public static HXThreadPool createThreadPool(int minThreads, int maxThreads, int queueSize) {
		return new HXThreadPool(minThreads, maxThreads, queueSize);
	}

}
