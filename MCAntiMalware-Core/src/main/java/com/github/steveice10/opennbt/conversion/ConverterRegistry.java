package com.github.steveice10.opennbt.conversion;

import com.github.steveice10.opennbt.conversion.builtin.*;
import com.github.steveice10.opennbt.conversion.builtin.custom.DoubleArrayTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.custom.FloatArrayTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.custom.ShortArrayTagConverter;
import com.github.steveice10.opennbt.tag.builtin.ByteArrayTag;
import com.github.steveice10.opennbt.tag.builtin.ByteTag;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.DoubleTag;
import com.github.steveice10.opennbt.tag.builtin.FloatTag;
import com.github.steveice10.opennbt.tag.builtin.IntArrayTag;
import com.github.steveice10.opennbt.tag.builtin.IntTag;
import com.github.steveice10.opennbt.tag.builtin.ListTag;
import com.github.steveice10.opennbt.tag.builtin.LongTag;
import com.github.steveice10.opennbt.tag.builtin.ShortTag;
import com.github.steveice10.opennbt.tag.builtin.StringTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.steveice10.opennbt.tag.builtin.custom.DoubleArrayTag;
import com.github.steveice10.opennbt.tag.builtin.custom.FloatArrayTag;
import com.github.steveice10.opennbt.tag.builtin.LongArrayTag;
import com.github.steveice10.opennbt.tag.builtin.custom.ShortArrayTag;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A registry mapping tags and value types to converters.
 */
public class ConverterRegistry {
    private static final Map<Class<? extends Tag>, TagConverter<? extends Tag, ?>> tagToConverter = new HashMap<Class<? extends Tag>, TagConverter<? extends Tag, ?>>();
    private static final Map<Class<?>, TagConverter<? extends Tag, ?>> typeToConverter = new HashMap<Class<?>, TagConverter<? extends Tag, ?>>();

    static {
        register(ByteTag.class, Byte.class, new ByteTagConverter());
        register(ShortTag.class, Short.class, new ShortTagConverter());
        register(IntTag.class, Integer.class, new IntTagConverter());
        register(LongTag.class, Long.class, new LongTagConverter());
        register(FloatTag.class, Float.class, new FloatTagConverter());
        register(DoubleTag.class, Double.class, new DoubleTagConverter());
        register(ByteArrayTag.class, byte[].class, new ByteArrayTagConverter());
        register(StringTag.class, String.class, new StringTagConverter());
        register(ListTag.class, List.class, new ListTagConverter());
        register(CompoundTag.class, Map.class, new CompoundTagConverter());
        register(IntArrayTag.class, int[].class, new IntArrayTagConverter());
        register(LongArrayTag.class, long[].class, new LongArrayTagConverter());

        register(DoubleArrayTag.class, double[].class, new DoubleArrayTagConverter());
        register(FloatArrayTag.class, float[].class, new FloatArrayTagConverter());
        register(ShortArrayTag.class, short[].class, new ShortArrayTagConverter());
    }

    /**
     * Registers a converter.
     *
     * @param <T>       Tag type to convert from.
     * @param <V>       Value type to convert to.
     * @param tag       Tag type class to register the converter to.
     * @param type      Value type class to register the converter to.
     * @param converter Converter to register.
     * @throws ConverterRegisterException If an error occurs while registering the converter.
     */
    public static <T extends Tag, V> void register(Class<T> tag, Class<V> type, TagConverter<T, V> converter) throws ConverterRegisterException {
        if(tagToConverter.containsKey(tag)) {
            throw new ConverterRegisterException("Type conversion to tag " + tag.getName() + " is already registered.");
        }

        if(typeToConverter.containsKey(type)) {
            throw new ConverterRegisterException("Tag conversion to type " + type.getName() + " is already registered.");
        }

        tagToConverter.put(tag, converter);
        typeToConverter.put(type, converter);
    }

    /**
     * Unregisters a converter.
     *
     * @param <T>  Tag type to unregister.
     * @param <V>  Value type to unregister.
     * @param tag  Tag type class to unregister.
     * @param type Value type class to unregister.
     */
    public static <T extends Tag, V> void unregister(Class<T> tag, Class<V> type) {
        tagToConverter.remove(tag);
        typeToConverter.remove(type);
    }

    /**
     * Converts the given tag to a value.
     *
     * @param <T> Tag type to convert from.
     * @param <V> Value type to convert to.
     * @param tag Tag to convert.
     * @return The converted value.
     * @throws ConversionException If a suitable converter could not be found.
     */
    public static <T extends Tag, V> V convertToValue(T tag) throws ConversionException {
        if(tag == null || tag.getValue() == null) {
            return null;
        }

        if(!tagToConverter.containsKey(tag.getClass())) {
            throw new ConversionException("Tag type " + tag.getClass().getName() + " has no converter.");
        }

        TagConverter<T, ?> converter = (TagConverter<T, ?>) tagToConverter.get(tag.getClass());
        return (V) converter.convert(tag);
    }

    /**
     * Converts the given value to a tag.
     *
     * @param <V>   Value type to convert from.
     * @param <T>   Tag type to convert to.
     * @param name  Name of the resulting tag.
     * @param value Value to convert.
     * @return The converted tag.
     * @throws ConversionException If a suitable converter could not be found.
     */
    public static <V, T extends Tag> T convertToTag(String name, V value) throws ConversionException {
        if(value == null) {
            return null;
        }

        TagConverter<T, V> converter = (TagConverter<T, V>) typeToConverter.get(value.getClass());
        if(converter == null) {
            for(Class<?> clazz : getAllClasses(value.getClass())) {
                if(typeToConverter.containsKey(clazz)) {
                    try {
                        converter = (TagConverter<T, V>) typeToConverter.get(clazz);
                        break;
                    } catch(ClassCastException e) {
                    }
                }
            }
        }

        if(converter == null) {
            throw new ConversionException("Value type " + value.getClass().getName() + " has no converter.");
        }

        return converter.convert(name, value);
    }

    private static Set<Class<?>> getAllClasses(Class<?> clazz) {
        Set<Class<?>> ret = new LinkedHashSet<Class<?>>();
        Class<?> c = clazz;
        while(c != null) {
            ret.add(c);
            ret.addAll(getAllSuperInterfaces(c));
            c = c.getSuperclass();
        }

        // Make sure Serializable is at the end to avoid mix-ups.
        if(ret.contains(Serializable.class)) {
            ret.remove(Serializable.class);
            ret.add(Serializable.class);
        }

        return ret;
    }

    private static Set<Class<?>> getAllSuperInterfaces(Class<?> clazz) {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        for(Class<?> c : clazz.getInterfaces()) {
            ret.add(c);
            ret.addAll(getAllSuperInterfaces(c));
        }

        return ret;
    }
}
