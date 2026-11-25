package com.webelement.esgadminportal.common;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class IndianAmountSerializer extends JsonSerializer<Number> {
	@Override
    public void serialize(Number value, JsonGenerator gen, SerializerProvider p) throws IOException {
        if (value == null) { gen.writeNull(); return; }
        
        gen.writeString(format(new BigDecimal(value.toString())));
    }
	
	
	private String format(BigDecimal amount) {
        // use setScale(2, ...) instead if you want paise
        BigDecimal rounded = amount.setScale(0, RoundingMode.HALF_UP);
        boolean negative = rounded.signum() < 0;
        String digits = rounded.abs().toPlainString();

        if (digits.length() <= 3) {
            return (negative ? "-" : "") + digits;
        }

        String lastThree = digits.substring(digits.length() - 3);
        String rest = digits.substring(0, digits.length() - 3);

        StringBuilder sb = new StringBuilder();
        // group the remaining digits in pairs, from the right
        int firstGroup = rest.length() % 2;
        if (firstGroup > 0) {
            sb.append(rest, 0, firstGroup);
        }
        for (int i = firstGroup; i < rest.length(); i += 2) {
            if (sb.length() > 0) sb.append(',');
            sb.append(rest, i, i + 2);
        }
        sb.append(',').append(lastThree);

        return (negative ? "-" : "") + sb;
    }
}
