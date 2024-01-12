package org.processmining.earthmoversstochasticconformancechecking.helperclasses;

import java.util.ArrayList;
import java.util.List;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class Transition2LabelMap {

	private final int[] transition2labelIndex;
	private final List<String> labelIndex2label;

	public Transition2LabelMap(EfficientStochasticPetriNetSemantics semantics, int numberOfTransitions) {
		TObjectIntMap<String> sMap = new TObjectIntHashMap<>(10, 0.5f, -1);

		transition2labelIndex = new int[numberOfTransitions];
		labelIndex2label = new ArrayList<>();
		for (int transition = 0; transition < numberOfTransitions; transition++) {
			if (semantics.isInvisible(transition)) {

			} else {
				String label = semantics.getLabel(transition);

				if (sMap.containsKey(label)) {
					int labelIndex = sMap.get(label);
					transition2labelIndex[transition] = labelIndex;
				} else {
					int labelIndex = sMap.size();
					sMap.put(label, labelIndex);
					labelIndex2label.add(label);
					transition2labelIndex[transition] = labelIndex;
				}
			}
		}
	}

	/**
	 * Thread safe.
	 * 
	 * @param label
	 * @return
	 */
	public int transition2labelIndex(int transition) {
		return transition2labelIndex[transition];
	}

	/**
	 * Thread safe.
	 * 
	 * @param labelIndex
	 * @return
	 */
	public String labelIndex2label(int labelIndex) {
		return labelIndex2label.get(labelIndex);
	}
}
