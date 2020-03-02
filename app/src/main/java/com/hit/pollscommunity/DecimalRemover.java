package com.hit.pollscommunity;


import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class DecimalRemover extends ValueFormatter {

    private DecimalFormat mFormat;

    public DecimalRemover() {
        mFormat = new DecimalFormat("#");
    }

    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value);
    }
}