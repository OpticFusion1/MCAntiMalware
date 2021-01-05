package com.github.steveice10.opennbt.conversion.builtin;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.DoubleTag;

/**
 * A converter that converts between DoubleTag and double.
 */
public class DoubleTagConverter implements TagConverter<DoubleTag, Double> {
    @Override
    public Double convert(DoubleTag tag) {
        return tag.getValue();
    }

    @Override
    public DoubleTag convert(String name, Double value) {
        return new DoubleTag(name, value);
    }
}
