package org.processmining.earthmoversstochasticconformancechecking.helperclasses;

public class PrefixProbabilityMarking {

	public static byte[] pack(int[] prefix, double probability, byte[] marking) {

		byte[] result = new byte[8 + prefix.length * 4 + marking.length];

		/**
		 * (1) the probability
		 */
		int probabilityStart = 0;
		long probabilityL = Double.doubleToRawLongBits(probability);
		result[probabilityStart + 0] = (byte) probabilityL;
		result[probabilityStart + 1] = (byte) (probabilityL >> 8);
		result[probabilityStart + 2] = (byte) (probabilityL >> 16);
		result[probabilityStart + 3] = (byte) (probabilityL >> 24);
		result[probabilityStart + 4] = (byte) (probabilityL >> 32);
		result[probabilityStart + 5] = (byte) (probabilityL >> 40);
		result[probabilityStart + 6] = (byte) (probabilityL >> 48);
		result[probabilityStart + 7] = (byte) (probabilityL >> 56);

		/**
		 * (2) the marking
		 */
		int markingStart = probabilityStart + 8;
		System.arraycopy(marking, 0, result, markingStart, marking.length);

		/**
		 * (3) the prefix
		 */
		int prefixStart = markingStart + marking.length;
		for (int p = 0; p < prefix.length; p++) {
			result[prefixStart + p * 4 + 0] = (byte) (prefix[p] >> 24);
			result[prefixStart + p * 4 + 1] = (byte) (prefix[p] >> 16);
			result[prefixStart + p * 4 + 2] = (byte) (prefix[p] >> 8);
			result[prefixStart + p * 4 + 3] = (byte) prefix[p];
		}

		return result;
	}

	public static double getProbability(byte[] prefixProbabilityMarking) {
		int probabilityStart = 0;

		long probabilityL = ((long) prefixProbabilityMarking[probabilityStart + 7] << 56)
				| ((long) prefixProbabilityMarking[probabilityStart + 6] & 0xff) << 48
				| ((long) prefixProbabilityMarking[probabilityStart + 5] & 0xff) << 40
				| ((long) prefixProbabilityMarking[probabilityStart + 4] & 0xff) << 32
				| ((long) prefixProbabilityMarking[probabilityStart + 3] & 0xff) << 24
				| ((long) prefixProbabilityMarking[probabilityStart + 2] & 0xff) << 16
				| ((long) prefixProbabilityMarking[probabilityStart + 1] & 0xff) << 8
				| ((long) prefixProbabilityMarking[probabilityStart + 0] & 0xff);

		return Double.longBitsToDouble(probabilityL);
	}

	public static byte[] getMarking(byte[] prefixProbabilityMarking, int markingLength) {
		int probabilityStart = 0;
		int markingStart = probabilityStart + 8;

		assert (markingStart < prefixProbabilityMarking.length);

		byte[] result = new byte[markingLength];
		System.arraycopy(prefixProbabilityMarking, markingStart, result, 0, markingLength);

		return result;
	}

	public static int[] getPrefix(byte[] prefixProbabilityMarking, int markingLength) {
		int probabilityStart = 0;
		int markingStart = probabilityStart + 8;
		int prefixStart = markingStart + markingLength;

		int prefixLength = (prefixProbabilityMarking.length - prefixStart) / 4;

		assert (prefixLength >= 0);

		int[] result = new int[prefixLength];

		for (int p = 0; p < prefixLength; p++) {
			result[p] = byteArray2int(prefixProbabilityMarking, prefixStart + p * 4);
		}

		return result;
	}

	public static int byteArray2int(byte[] bytes, int start) {
		return bytes[start + 0] << 24 | (bytes[start + 1] & 0xFF) << 16 | (bytes[start + 2] & 0xFF) << 8
				| (bytes[start + 3] & 0xFF);
	}
}
