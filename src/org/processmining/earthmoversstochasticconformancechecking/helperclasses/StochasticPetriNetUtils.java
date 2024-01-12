package org.processmining.earthmoversstochasticconformancechecking.helperclasses;

import java.util.BitSet;

import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;

import com.util.Arrays;

import gnu.trove.map.TLongByteMap;
import gnu.trove.map.hash.TLongByteHashMap;

public class StochasticPetriNetUtils {

	public static class PetriNetCache {
		private static final byte NOTSET = (byte) 0;
		private static final byte TRUE = (byte) 1;
		private static final byte FALSE = (byte) 2;

		private TLongByteMap neighbourCache = new TLongByteHashMap(10, 0.5f, Long.MIN_VALUE, NOTSET);
		private TLongByteMap dependentCache = new TLongByteHashMap(10, 0.5f, Long.MIN_VALUE, NOTSET);
	}

	/**
	 * 
	 * @param net
	 * @param transitionMap
	 * @param transitionIndexA
	 * @param transitionIndexB
	 * @return whether transition B consumes any token produced by transition A
	 */
	public static boolean isDependent(StochasticLabelledPetriNet net, int transitionIndexA, int transitionIndexB,
			PetriNetCache cache) {
		long key = (((long) transitionIndexA) << 32) | (transitionIndexB & 0xffffffffL);
		byte result = cache.dependentCache.get(key);
		if (result != cache.dependentCache.getNoEntryValue()) {
			return result == cache.TRUE;
		}

		int[] placesA = net.getOutputPlaces(transitionIndexA);
		int[] placesB = net.getInputPlaces(transitionIndexB);

		for (int place : placesA) {
			if (Arrays.contains(placesB, place)) {
				cache.dependentCache.put(key, cache.TRUE);
				return true;
			}
		}

		cache.dependentCache.put(key, cache.FALSE);
		return false;
	}

	/**
	 * 
	 * @param net
	 * @param transitionMap
	 * @param transitionIndex
	 * @param neighbourCache
	 * @return all transitions that share an input place with this transition
	 *         and are not the transition itself
	 */
	public static BitSet getNeighbours(StochasticLabelledPetriNet net, int transitionIndex, BitSet enabledTransitions,
			PetriNetCache neighbourCache) {
		BitSet result = new BitSet();
		for (int transitionIndexB = enabledTransitions.nextSetBit(
				0); transitionIndexB >= 0; transitionIndexB = enabledTransitions.nextSetBit(transitionIndexB + 1)) {
			if (transitionIndex != transitionIndexB
					&& isNeighbour(net, transitionIndex, transitionIndexB, neighbourCache)) {
				result.set(transitionIndexB);
			}
		}
		return result;
	}

	/**
	 * 
	 * @param net
	 * @param transitionMap
	 * @param transitionIndexA
	 * @param transitionIndexB
	 * @return whether transitionA and transitionB compete for any token
	 */
	public static boolean isNeighbour(StochasticLabelledPetriNet net, int transitionIndexA, int transitionIndexB,
			PetriNetCache cache) {
		long key = (((long) transitionIndexA) << 32) | (transitionIndexB & 0xffffffffL);
		byte result = cache.neighbourCache.get(key);
		if (result != cache.neighbourCache.getNoEntryValue()) {
			return result == cache.TRUE;
		}

		int[] placesA = net.getInputPlaces(transitionIndexA);
		int[] placesB = net.getInputPlaces(transitionIndexB);

		for (int place : placesA) {
			if (Arrays.contains(placesB, place)) {
				cache.neighbourCache.put(key, cache.TRUE);
				return true;
			}
		}

		cache.neighbourCache.put(key, cache.FALSE);
		return false;
	}

	/**
	 * 
	 * @param net
	 * @param transitionMap
	 * @param transitionIndex
	 * @param neighbourCache
	 * @return all transitions that do not share an input place with this
	 *         transition and are not the transition itself
	 */
	public static BitSet getNonNeighbours(StochasticLabelledPetriNet net, int transitionIndex,
			BitSet enabledTransitions, PetriNetCache neighbourCache) {
		BitSet result = new BitSet();
		for (int transitionIndexB = enabledTransitions.nextSetBit(
				0); transitionIndexB >= 0; transitionIndexB = enabledTransitions.nextSetBit(transitionIndexB + 1)) {
			if (transitionIndex != transitionIndexB
					&& !isNeighbour(net, transitionIndex, transitionIndexB, neighbourCache)) {
				result.set(transitionIndexB);
			}
		}
		return result;
	}

}
