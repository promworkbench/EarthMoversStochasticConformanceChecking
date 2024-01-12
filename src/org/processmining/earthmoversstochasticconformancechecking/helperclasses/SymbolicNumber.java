package org.processmining.earthmoversstochasticconformancechecking.helperclasses;

import java.math.BigInteger;

import com.google.common.math.BigIntegerMath;

/**
 * if it doesn't run locally, ask Wolfram
 * 
 * @author sander
 *
 */
public class SymbolicNumber {

	private static final BigInteger factorialThreshold = BigInteger.valueOf(50000);

	private static enum Type {
		number, string, NaN, factorial, multiplyFactorial
	}

	private final Type type;
	private final BigInteger value;
	private final BigInteger multiplicationFactor;
	private final String string;

	public SymbolicNumber(BigInteger value) {
		assert value != null;
		this.type = Type.number;
		this.value = value;
		this.string = null;
		this.multiplicationFactor = null;
	}

	public static SymbolicNumber NaN(String string) {
		return new SymbolicNumber(string, Type.NaN);
	}

	private SymbolicNumber(String string, Type type) {
		assert string != null;
		assert type == Type.string || type == Type.NaN;
		this.value = null;
		this.multiplicationFactor = null;
		this.string = string;
		this.type = type;
	}

	private SymbolicNumber(BigInteger value, BigInteger multiplicationFactor, Type type) {
		assert type != Type.multiplyFactorial || multiplicationFactor != null;
		assert type != Type.factorial || multiplicationFactor == null;
		assert value != null;
		this.type = type;
		this.value = value;
		this.string = null;
		this.multiplicationFactor = multiplicationFactor;
	}

	public boolean isNumber() {
		return type == Type.number;
	}

	private boolean isNaN() {
		return type == Type.NaN;
	}

	public String toString() {
		switch (type) {
			case factorial :
				return "(" + value.toString() + "!)";
			case multiplyFactorial :
				return "(" + multiplicationFactor.toString() + "*" + value.toString() + "!)";
			case number :
				return value.toString();
			case string :
				return string;
			case NaN :
				return "NaN " + string;
			default :
				return null;
		}
	}

	public SymbolicNumber multiply(SymbolicNumber with) {
		if (isNaN()) {
			return this;
		}
		if (with.isNaN()) {
			return with;
		}
		if (isNumber() && value.equals(BigInteger.ONE)) {
			return with;
		}
		if (with.isNumber() && with.value.equals(BigInteger.ONE)) {
			return this;
		}
		if (isNumber() && with.isNumber()) {
			return new SymbolicNumber(value.multiply(with.value));
		}

		//if this is a number and the with is a factorial, then there is a special case for division
		if (type == Type.number && with.type == Type.factorial) {
			return new SymbolicNumber(with.value, value, Type.multiplyFactorial);
		}

		return new SymbolicNumber("(" + toString() + "*" + with.toString() + ")", Type.number);
	}

	public SymbolicNumber add(SymbolicNumber n) {
		if (isNaN()) {
			return this;
		}
		if (n.isNaN()) {
			return n;
		}
		if (isNumber() && value.equals(BigInteger.ZERO)) {
			return n;
		}
		if (n.isNumber() && n.value.equals(BigInteger.ZERO)) {
			return this;
		}

		if (isNumber() && n.isNumber()) {
			return new SymbolicNumber(value.add(n.value));
		}
		return new SymbolicNumber("(" + toString() + "+" + n.toString() + ")", Type.string);
	}

	public SymbolicNumber factorial() {
		if (isNaN()) {
			return this;
		}
		//if the factorial is small engouh, compute it directly
		if (isNumber() && value.compareTo(factorialThreshold) <= 0) {
			return new SymbolicNumber(BigIntegerMath.factorial(value.intValue()));
		}
		if (isNumber()) {
			//this number is too big to compute, but we might use while dividing
			return new SymbolicNumber(value, null, Type.factorial);
		}
		return new SymbolicNumber("(" + toString() + "!)", Type.string);
	}

	public SymbolicNumber divide(SymbolicNumber by) {
		if (isNaN()) {
			return this;
		}
		if (by.isNaN()) {
			return by;
		}
		if (isNumber() && by.isNumber()) {
			return new SymbolicNumber(value.divide(by.value));
		}

		//divide by 1
		if (by.isNumber() && by.value.equals(BigInteger.ONE)) {
			return this;
		}

		//try to get the division of factorials by simplification
		if (type == Type.factorial && by.type == Type.factorial) {
			BigInteger difference = value.subtract(by.value);
			if (difference.compareTo(factorialThreshold) <= 0 && difference.compareTo(BigInteger.ZERO) >= 0) {

				//we are larger than by, so we can obtain the division by expanding the top factorial
				int diff = difference.intValue();
				BigInteger result = BigInteger.ONE;
				BigInteger factor = value;
				for (int n = 1; n <= diff; n++) {
					result = result.multiply(factor);
					factor = factor.subtract(BigInteger.ONE);
				}

				return new SymbolicNumber(result);
			}
		}

		//try to get the special case of a!/(b*c!) => expand(a to c) / b
		if (type == Type.factorial && by.type == Type.multiplyFactorial) {
			BigInteger difference = value.subtract(by.value);
			if (difference.compareTo(factorialThreshold) <= 0 && difference.compareTo(BigInteger.ZERO) >= 0) {
				//we are larger than by, so we can obtain the division by expanding the top factorial
				int diff = difference.intValue();
				BigInteger result = BigInteger.ONE;
				BigInteger factor = value;
				for (int n = 1; n <= diff; n++) {
					result = result.multiply(factor);
					factor = factor.subtract(BigInteger.ONE);
				}

				return new SymbolicNumber(result.divide(by.multiplicationFactor));
			}
		}

		return new SymbolicNumber("(" + toString() + "/" + by.toString() + ")", Type.string);
	}

	/**
	 * 
	 * @return the value of the number if it fits, or Long.MIN_VALUE if it was
	 *         not computed.
	 */
	public long longValue() {
		if (isNumber()) {
			long i = value.longValue();
			if (value.equals(BigInteger.valueOf(i))) {
				return i;
			}
		}
		return Long.MIN_VALUE;
	}

	/**
	 * 
	 * @return the value of the number if it has been computed; otherwise null.
	 */
	public BigInteger bigIntegerValue() {
		return value;
	}

}
