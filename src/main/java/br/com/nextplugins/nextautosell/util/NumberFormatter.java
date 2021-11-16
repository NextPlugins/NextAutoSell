package br.com.nextplugins.nextautosell.util;

import java.text.DecimalFormat;

public final class NumberFormatter {

    private static final DecimalFormat decimalFormat = new DecimalFormat();

    public static String format(double number) {
        return decimalFormat.format(number);
    }

}
