package com.github.steveice10.opennbt.conversion.builtin;

import com.github.steveice10.opennbt.conversion.ConverterRegistry;
import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.ListTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter that converts between CompoundTag and Map.
 */
public class ListTagConverter implements TagConverter<ListTag, List> {
    @Override
    public List convert(ListTag tag) {
        List<Object> ret = new ArrayList<Object>();
        List<? extends Tag> tags = tag.getValue();
        for(Tag t : tags) {
            ret.add(ConverterRegistry.convertToValue(t));
        }

        return ret;
    }

    @Override
    public ListTag convert(String name, List value) {
        List<Tag> tags = new ArrayList<Tag>();
        for(Object o : value) {
            tags.add(ConverterRegistry.convertToTag("", o));
        }

        return new ListTag(name, tags);
    }
}
