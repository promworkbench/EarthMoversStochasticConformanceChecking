package org.processmining.earthmoversstochasticconformancechecking.helperclasses;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class QueueCombination {

	private static final int globalQueueTargetSize = 20;
	private static final int useGlobalEach = 10;

	protected final ConcurrentLinkedQueue<byte[]> globalQueue;
	protected final AtomicInteger globalQueueSize;
	protected final PriorityQueue<byte[]> localQueue;

	public QueueCombination(ConcurrentLinkedQueue<byte[]> globalQueue, AtomicInteger globalQueueSize) {
		this.globalQueue = globalQueue;
		this.globalQueueSize = globalQueueSize;
		this.localQueue = new PriorityQueue<>(10, new Comparator<byte[]>() {
			public int compare(byte[] o1, byte[] o2) {
				double a = PrefixProbabilityMarking.getProbability(o1);
				double b = PrefixProbabilityMarking.getProbability(o2);
				if (a < b) {
					return 1;
				} else if (a == b) {
					return 0;
				} else {
					return -1;
				}
			}
		});
	}

	public void add(byte[] prefixProbabilityMarking) {
		int globalSize = globalQueueSize.get();
		if (globalSize <= globalQueueTargetSize) {
			globalQueue.add(prefixProbabilityMarking);
			globalQueueSize.incrementAndGet();
		} else {
			//add to local queue
			localQueue.add(prefixProbabilityMarking);
		}
	}

	public byte[] poll() {
		if (localQueue.size() % useGlobalEach != 0) {
			byte[] local = localQueue.poll();
			if (local != null) {
				return local;
			}
		}
		byte[] global = globalQueue.poll();
		if (global != null) {
			globalQueueSize.decrementAndGet();
			return global;
		} else {
			return localQueue.poll();
		}
	}
}
