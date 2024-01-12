package org.processmining.earthmoversstochasticconformancechecking.datastructures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

/**
 * Adapted implementation of the HashBackedPriorityQueue by bvdongen to support generics.
 * Besides, this implementation is less memory efficient than the original implementation but 
 * therefore is easier to maintain and relies only on java-native libraries.
 * 
 * It is IMPORTANT that {@link Object#equals(Object)} and {@link Object#hashCode()} are implemented.
 * Moreover, it is possible that the comparator has a stronger assumption on what is equal.
 * -> Idea: Hash code and equals consider the state (e.g. replay state)
 * -> Comparator: More sophisticated ordering based on state evaluation values
 * @author brockhoff
 *
 * @param <T>
 */
public class HashBackedPriorityQueue<T> implements java.util.Queue<T> {

	protected final Map<T, Integer> locationMap;

	protected static final int NEV = -1;

	/**
	 * Priority queue represented as a balanced binary heap: the two children of
	 * queue[n] are queue[2*n+1] and queue[2*(n+1)]. The priority queue is ordered
	 * by the record's natural ordering: For each node n in the heap and each
	 * descendant d of n, n <= d. The element with the best value is in queue[0],
	 * assuming the queue is nonempty.
	 */
	protected ArrayList<T> queue;

	private final Comparator<T> itemComparator;

	public HashBackedPriorityQueue(int initialCapacity, Comparator<T> itemComparator) {
		locationMap = new HashMapLinearProbing<>(initialCapacity);
		this.queue = new ArrayList<>(initialCapacity);
		this.itemComparator = itemComparator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.tue.astar.util.FastLookupPriorityQueue#isEmpty()
	 */
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public boolean contains(Object object) {
		return locationMap.containsKey(object);
	}

	@Override
	public T peek() {
		if (queue.size() == 0)
			return null;
		return queue.get(0);
	}

	protected T peek(int location) {
		return queue.get(location);
	}

	public int size() {
		return queue.size();
	}

	@Override
	public T poll() {
		if (queue.isEmpty())
			return null;
		// Top of the heap
		T result = queue.get(0);
		// Remove from location map
		locationMap.remove(result);
		
		// Remove last element
		T x = queue.remove(queue.size() - 1);

		// If there is more than a single element, i.e., result != x
		if(queue.size() != 0) {
			// Percolate x down the heap
			siftDown(0, x);
		}

		return result;
	}


	public String toString() {
		return queue.toString();
	}

	/**
	 * Inserts the specified element into this priority queue.
	 * Does not allow to add two objects with the same hash code.
	 * 
	 * 
	 * @return {@code true} (as specified by {@link Collection#add})
	 */
	public boolean add(T object) {
		//		assert checkInv();
		// check if overwrite is necessary, i.e. only add if the object does not
		// exist yet,
		// or exists, but with higher costs.
		Integer location = locationMap.get(object);
		if (location == null) {
			// new element, add to queue and return
			offer(object);
			//			assert checkInv();
			return true;
		}
		else {
			return false;
		}

	}
	
	public boolean addOrUpdate(T object) {
		Integer location = locationMap.get(object);
		if (location == null) {
			// new element, add to queue and return
			offer(object);
			//			assert checkInv();
			return true;
		}
		// if the object which exists at location has updated score
		if (location > 0 && isBetter(object, peek((location - 1) >>> 1))) {
			// and the new score is better, then sift the marking up
			// update to better, if newE better then peek(location)
			siftUp(location, object);
			//			assert checkInv();
			return true;
		} else if ((location << 1) + 1 < queue.size() && isBetter(peek((location << 1) + 1), object)) {
			siftDown(location, object);
			//			assert checkInv();
			return true;
		} else if ((location << 1) + 2 < queue.size() && isBetter(peek((location << 1) + 2), object)) {
			siftDown(location, object);
			//			assert checkInv();
			return true;
		}
		else {
			queue.set(location, object);
			return true;
		}
	}
	
	public boolean addOrUpdateIfBetter(T object) {
		Integer location = locationMap.get(object);
		if (location == null) {
			// new element, add to queue and return
			offer(object);
			//			assert checkInv();
			return true;
		}
		if (location > 0 && isBetter(object, queue.get(location))) {
			// if the object which exists at location has updated score
			if (location > 0 && isBetter(object, peek((location - 1) >>> 1))) {
				// and the new score is better, then sift the marking up
				// update to better, if newE better then peek(location)
				siftUp(location, object);
				//			assert checkInv();
				return true;
			} else if ((location << 1) + 1 < queue.size() && isBetter(peek((location << 1) + 1), object)) {
				siftDown(location, object);
				//			assert checkInv();
				return true;
			} else if ((location << 1) + 2 < queue.size() && isBetter(peek((location << 1) + 2), object)) {
				siftDown(location, object);
				//			assert checkInv();
				return true;
			}
			else {
				queue.set(location, object);
				return true;
			}
		}
		else {
			return false;
		}
	}

	/**
	 * First order sorting is based on F score alone.
	 */
	protected boolean isBetter(T object1, T object2) {
		return this.itemComparator.compare(object1, object2) < 0;
	}

	/**
	 * Inserts the specified element into this priority queue.
	 * 
	 * @return {@code true} (as specified by {@link Queue#offer})
	 */
	public boolean offer(T object) {
		int s = queue.size();
		if (s == 0) {
			queue.add(object);
			locationMap.put(object, 0);
		} else {
			queue.add(object);
			siftUp(s, object);
		}
		return true;
	}

	/**
	 * Inserts item x at position k, maintaining heap invariant by promoting x up
	 * the tree until it is greater than or equal to its parent, or is the root.
	 * 
	 * @param fromPosition
	 * @param marking
	 *            the item to insert
	 */
	protected void siftUp(int fromPosition, T object) {
		while (fromPosition > 0) {
			int parent = (fromPosition - 1) >>> 1;
			T existing = queue.get(parent);
			if (!isBetter(object, existing)) {
				break;
			}
			queue.set(fromPosition, existing);
			locationMap.replace(existing, fromPosition);
			fromPosition = parent;
		}
		queue.set(fromPosition, object);
		locationMap.put(object, fromPosition);
	}

	/**
	 * Inserts item x at position k, maintaining heap invariant by demoting x down
	 * the tree repeatedly until it is less than or equal to its children or is a
	 * leaf.
	 * 
	 * @param positionToFill
	 *            the position to fill
	 * @param marking
	 *            the item to insert
	 */
	protected void siftDown(int positionToFill, T object) {
		int half = queue.size() >>> 1;
		while (positionToFill < half) {
			int posChild = (positionToFill << 1) + 1;
			T c = queue.get(posChild);
			int right = posChild + 1;
			if (right < queue.size() && isBetter(queue.get(right), c)) {
				c = queue.get(right);
				posChild = right;
			}

			if (!isBetter(c, object))
				break;

			queue.set(positionToFill, c);
			// assert locationMap.get(c.getState()) == child;
			// i.e. child + k -child == k,
			// hence we use adjustValue instead of put here.
			locationMap.replace(c, positionToFill);
			positionToFill = posChild;
		}
		queue.set(positionToFill, object);
		locationMap.put(object, positionToFill);
	}

	protected boolean checkInv(int loc) {
		T n = queue.get(loc);
		T c1 = null;
		T c2 = null;
		if (2 * loc + 1 < queue.size())
			c1 = queue.get(2 * loc + 1);

		if (2 * (loc + 1) < queue.size())
			c2 = queue.get(2 * (loc + 1));

		if (c1 != null) {
			if (isBetter(c1, n)) {
//				System.err.println("Child " + c1 + "(" + ") is better than parent " + n + "("
//						+ ")");
				return false;
			}
		}
		if (c2 != null) {
			if (isBetter(c2, n)) {
//				System.err.println("Child " + c2 + "(" +  ") is better than parent " + n + "("
//						+ ")");
				return false;
			}
		}
		return (c1 == null ? true : checkInv(2 * loc + 1)) && (c2 == null ? true : checkInv(2 * (loc + 1)));

	}

	public Iterator<T> iterator() {
		// Remove method is problematic
		throw new RuntimeException("Iterator not supported");
	}

	public Object[] toArray() {
		return queue.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return queue.toArray(a);
	}

	public boolean remove(Object o) {
		throw new RuntimeException("Remove not supported");
	}

	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean addAll(Collection<? extends T> c) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public T remove() {
		// TODO Auto-generated method stub
		return null;
	}

	public T element() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<T, Integer> getLocationMap() {
		return locationMap;
	}
	
	public Comparator<T> getItemComparator() {
		return itemComparator;
	}
	
	public T get(Object o) {
		if (locationMap.containsKey(o)) {
			return queue.get(locationMap.get(o));
		}
		else {
			return null;
		}
	}
	
}