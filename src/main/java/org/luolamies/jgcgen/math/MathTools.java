package org.luolamies.jgcgen.math;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * A collection of static functions.
 *
 * @author Calle Laakkonen
 */
public class MathTools {

    public final static DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = DecimalFormatSymbols.getInstance();

    static {
        // Makes sure decimal separator is '.' despite locale
        DECIMAL_FORMAT_SYMBOLS.setDecimalSeparator('.');

        // Make sure minus sign is not a UTF-8 character
        DECIMAL_FORMAT_SYMBOLS.setMinusSign('-');
    }

    public final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.###", DECIMAL_FORMAT_SYMBOLS);

	/**
	 * An inclusive range from <var>from</var> to <var>to</var>
	 * @param from starting value
	 * @param to ending value
	 * @param step step size
	 * @return
	 */
	static public Iterable<Double> range(double from, double to, double step) {
		return new Range(from, to, step);
	}

	/**
	 * Is the given string a number?
	 * @param str
	 * @return true if string is a valid number
	 */
	static public boolean isNumber(String str) {
		try {
			Double.parseDouble(str);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Convert the string to a number
	 * @param str
	 * @return number
	 */
	static public double number(String str) {
		return Double.parseDouble(str);
	}

	static public String format(double value) {
	    return DECIMAL_FORMAT.format(value);
    }
}
