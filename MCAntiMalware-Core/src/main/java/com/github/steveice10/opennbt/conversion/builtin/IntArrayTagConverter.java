package com.github.steveice10.opennbt.conversion.builtin;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.IntArrayTag;

/**
 * A converter that converts between IntArrayTag and int[].
 */
public class IntArrayTagConverter implements TagConverter<IntArrayTag, int[]> {
    @Override
    public int[] convert(IntArrayTag tag) {
        return tag.getValue();
    }

    @Override
    public IntArrayTag convert(String name, int[] value) {
        return new IntArrayTag(name, value);
    }
}
