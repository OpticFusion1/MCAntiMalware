/*
MIT License

Copyright (c) 2017 MrYurihi Redstone

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package com.github.mryurihi.tbnbt.adapter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.github.mryurihi.tbnbt.TagType;
import com.github.mryurihi.tbnbt.adapter.impl.ArrayAdapterFactory;
import com.github.mryurihi.tbnbt.adapter.impl.ByteArrayAdapter;
import com.github.mryurihi.tbnbt.adapter.impl.CollectionAdapterFactory;
import com.github.mryurihi.tbnbt.adapter.impl.IntegerArrayAdapter;
import com.github.mryurihi.tbnbt.adapter.impl.LongArrayAdapter;
import com.github.mryurihi.tbnbt.adapter.impl.MapAdapterFactory;
import com.github.mryurihi.tbnbt.adapter.impl.ObjectAdapter;
import com.github.mryurihi.tbnbt.adapter.impl.StringAdapter;
import com.github.mryurihi.tbnbt.adapter.impl.primitive.BooleanAdapter;
import com.github.mryurihi.tbnbt.adapter.impl.primitive.ByteAdapter;
import com.github.mryurihi.tbnbt.adapter.impl.primitive.DoubleAdapter;
import com.github.mryurihi.tbnbt.adapter.impl.primitive.FloatAdapter;
import com.github.mryurihi.tbnbt.adapter.impl.primitive.IntegerAdapter;
import com.github.mryurihi.tbnbt.adapter.impl.primitive.LongAdapter;
import com.github.mryurihi.tbnbt.adapter.impl.primitive.PrimitiveByteArrayAdapter;
import com.github.mryurihi.tbnbt.adapter.impl.primitive.PrimitiveIntArrayAdapter;
import com.github.mryurihi.tbnbt.adapter.impl.primitive.PrimitiveLongArrayAdapter;
import com.github.mryurihi.tbnbt.adapter.impl.primitive.ShortAdapter;

/**
 * The registry that holds all of the
 * {@link com.github.mryurihi.tbnbt.adapter.NBTAdapter}'s are held. To register
 * a custom adapter all you have do do is this <br>
 * <br>
 * 
 * <pre>
 * public class PointAdapter extends NBTAdapter&lt;Point&gt; {
 * 	... 
 * }
 * 
 * AdapterRegistry.registerAdapter(Point.class, PointAdapter.class);
 * </pre>
 * 
 * This class also comes with many convenience classes to read and write data
 * because it is not recommended to use any methods that don't get adapters
 * because they could change
 * 
 * @author MrYurihi Redstone
 *
 */
public class AdapterRegistry {
	
	/**
	 * Constructor for AdapterRegistry. Creates the registry along with all of the
	 * default values.
	 */
	private AdapterRegistry() {
		registry = new HashMap<>();
		factory = new HashMap<>();
		Class<?>[] rClass = new Class<?>[] {
			Byte.class,
			Short.class,
			Integer.class,
			Long.class,
			Float.class,
			Double.class,
			Byte[].class,
			byte[].class,
			String.class,
			Object.class,
			Integer[].class,
			int[].class,
			Long[].class,
			long[].class
		};
		NBTAdapter<?>[] rAdapter = new NBTAdapter<?>[] {
			new ByteAdapter(),
			new ShortAdapter(),
			new IntegerAdapter(),
			new LongAdapter(),
			new FloatAdapter(),
			new DoubleAdapter(),
			new ByteArrayAdapter(),
			new PrimitiveByteArrayAdapter(),
			new StringAdapter(),
			new ObjectAdapter(),
			new IntegerArrayAdapter(),
			new PrimitiveIntArrayAdapter(),
			new LongArrayAdapter(),
			new PrimitiveLongArrayAdapter()
		};
		for(int i = 0; i < rClass.length; i++)
			registry.put(rClass[i], rAdapter[i]);
		factory.put(Collection.class, new CollectionAdapterFactory());
		factory.put(Object[].class, new ArrayAdapterFactory());
		factory.put(Map.class, new MapAdapterFactory());
	}
	
	private Map<Class<?>, NBTAdapter<?>> registry;
	private Map<Class<?>, NBTAdapterFactory> factory;
	
	/**
	 * Gets the adapter that reads and writes data for a specific type
	 * 
	 * @param objectTypeWrapper the type associated with the adapter
	 * @return The adapter
	 */
	public NBTAdapter<?> getAdapterForObject(TypeWrapper<?> objectTypeWrapper) {
		return getAdapterForObject(objectTypeWrapper, objectTypeWrapper);
	}
	
	private NBTAdapter<?> getAdapterForObject(TypeWrapper<?> objectTypeWrapper, TypeWrapper<?> topTypeWrapper) {
		Class<?> objectType = objectTypeWrapper.getClassType();
		if(objectType.isPrimitive()) {
			if(objectType.equals(byte.class)) return new ByteAdapter();
			if(objectType.equals(short.class)) return new ShortAdapter();
			if(objectType.equals(int.class)) return new IntegerAdapter();
			if(objectType.equals(long.class)) return new LongAdapter();
			if(objectType.equals(float.class)) return new FloatAdapter();
			if(objectType.equals(double.class)) return new DoubleAdapter();
			if(objectType.equals(boolean.class)) return new BooleanAdapter();
		}
		if(registry.containsKey(objectType)) return registry.get(objectType);
		else if(factory.containsKey(objectType)) return factory.get(objectType).create(this, topTypeWrapper);
		else if(objectType.getInterfaces().length != 0) {
			for(Type t: objectType.getGenericInterfaces()) {
				NBTAdapter<?> adapter = getAdapterForObject(TypeWrapper.of(t), topTypeWrapper);
				if(adapter != null) return adapter;
			}
		}
		Class<?> superClass = objectType.getSuperclass();
		if(superClass == null) return null;
		NBTAdapter<?> out = getAdapterForObject(TypeWrapper.of(superClass), topTypeWrapper);
		return out;
	}
	
	/**
	 * Gets the adapter for a byte
	 * 
	 * @return the byte adapter
	 */
	public NBTAdapter<?> getByteAdapter() {
		return registry.get(Byte.class);
	}
	
	/**
	 * Convenience method. Writes data to a Byte object
	 * 
	 * @param payload the stream to read from
	 * @return the Byte of data
	 * @throws NBTParseException if there was a parse exception
	 */
	public Byte fromByte(DataInputStream payload) throws NBTParseException {
		return (Byte) registry.get(Byte.class).fromNBT(TagType.BYTE, payload, new TypeWrapper<Byte>() {}, this);
	}
	
	/**
	 * Convenience method. Reads data from a byte
	 * 
	 * @param out the stream to write to
	 * @param object the byte to read from
	 * @throws NBTParseException if there was a parse exception
	 */
	public void writeByte(DataOutputStream out, byte object) throws NBTParseException {
		registry.get(Byte.class).toNBT(out, object, new TypeWrapper<Byte>() {}, this);
	}
	
	/**
	 * Gets the adapter for a short
	 * 
	 * @return the short adapter
	 */
	public NBTAdapter<?> getShortAdapter() {
		return registry.get(Short.class);
	}
	
	/**
	 * Convenience method. Writes data to a Short object
	 * 
	 * @param payload the stream to read from
	 * @return the Short of data
	 * @throws NBTParseException if there was a parse exception
	 */
	public Short fromShort(DataInputStream payload) throws NBTParseException {
		return (Short) registry.get(Short.class).fromNBT(TagType.SHORT, payload, new TypeWrapper<Short>() {}, this);
	}
	
	/**
	 * Convenience method. Reads data from a short
	 * 
	 * @param out the stream to write to
	 * @param object the short to read from
	 * @throws NBTParseException if there was a parse exception
	 */
	public void writeShort(DataOutputStream out, short object) throws NBTParseException {
		registry.get(Short.class).toNBT(out, object, new TypeWrapper<Short>() {}, this);
	}
	
	/**
	 * Gets the adapter for an integer
	 * 
	 * @return the integer adapter
	 */
	public NBTAdapter<?> getIntAdapter() {
		return registry.get(Integer.class);
	}
	
	/**
	 * Convenience method. Writes data to an Integer object
	 * 
	 * @param payload the stream to read from
	 * @return the Integer of data
	 * @throws NBTParseException if there was a parse exception
	 */
	public Integer fromInt(DataInputStream payload) throws NBTParseException {
		return (Integer) registry.get(Integer.class).fromNBT(TagType.INT, payload, new TypeWrapper<Integer>() {}, this);
	}
	
	/**
	 * Convenience method. Reads data from an int
	 * 
	 * @param out the stream to write to
	 * @param object the int to read from
	 * @throws NBTParseException if there was a parse exception
	 */
	public void writeInt(DataOutputStream out, int object) throws NBTParseException {
		registry.get(Integer.class).toNBT(out, object, new TypeWrapper<Integer>() {}, this);
	}
	
	/**
	 * Gets the adapter for a long
	 * 
	 * @return the long adapter
	 */
	public NBTAdapter<?> getLongAdapter() {
		return registry.get(Long.class);
	}
	
	/**
	 * Convenience method. Writes data to a Long object
	 * 
	 * @param payload the stream to read from
	 * @return the Long of data
	 * @throws NBTParseException if there was a parse exception
	 */
	public Long fromLong(DataInputStream payload) throws NBTParseException {
		return (Long) registry.get(Long.class).fromNBT(TagType.LONG, payload, new TypeWrapper<Long>() {}, this);
	}
	
	/**
	 * Convenience method. Reads data from a long
	 * 
	 * @param out the stream to write to
	 * @param object the long to read from
	 * @throws NBTParseException if there was a parse exception
	 */
	public void writeLong(DataOutputStream out, long object) throws NBTParseException {
		registry.get(Long.class).toNBT(out, object, new TypeWrapper<Long>() {}, this);
	}
	
	/**
	 * Gets the adapter for a float
	 * 
	 * @return the float adapter
	 */
	public NBTAdapter<?> getFloatAdapter() {
		return registry.get(Float.class);
	}
	
	/**
	 * Convenience method. Writes data to a Float object
	 * 
	 * @param payload the stream to read from
	 * @return the Float of data
	 * @throws NBTParseException if there was a parse exception
	 */
	public Float fromFloat(DataInputStream payload) throws NBTParseException {
		return (Float) registry.get(Float.class).fromNBT(TagType.FLOAT, payload, new TypeWrapper<Float>() {}, this);
	}
	
	/**
	 * Convenience method. Reads data from a float
	 * 
	 * @param out the stream to write to
	 * @param object the float to read from
	 * @throws NBTParseException if there was a parse exception
	 */
	public void writeFloat(DataOutputStream out, float object) throws NBTParseException {
		registry.get(Float.class).toNBT(out, object, new TypeWrapper<Float>() {}, this);
	}
	
	/**
	 * Gets the adapter for a double
	 * 
	 * @return the double adapter
	 */
	public NBTAdapter<?> getDoubleAdapter() {
		return registry.get(Double.class);
	}
	
	/**
	 * Convenience method. Writes data to a Double object
	 * 
	 * @param payload the stream to read from
	 * @return the Double of data
	 * @throws NBTParseException if there was a parse exception
	 */
	public Double fromDouble(DataInputStream payload) throws NBTParseException {
		return (Double) registry.get(Double.class).fromNBT(TagType.DOUBLE, payload, new TypeWrapper<Double>() {}, this);
	}
	
	/**
	 * Convenience method. Reads data from a double
	 * 
	 * @param out the stream to write to
	 * @param object the double to read from
	 * @throws NBTParseException if there was a parse exception
	 */
	public void writeDouble(DataOutputStream out, double object) throws NBTParseException {
		registry.get(Double.class).toNBT(out, object, new TypeWrapper<Double>() {}, this);
	}
	
	/**
	 * Gets the adapter for a byte array
	 * 
	 * @return the byte array adapter
	 */
	public NBTAdapter<?> getByteArrayAdapter() {
		return registry.get(Byte[].class);
	}
	
	/**
	 * Convenience method. Writes data to a Byte[] object
	 * 
	 * @param payload the stream to read from
	 * @return the Byte[] of data
	 * @throws NBTParseException if there was a parse exception
	 */
	public Byte[] fromByteArray(DataInputStream payload) throws NBTParseException {
		return (Byte[]) registry.get(Byte[].class)
			.fromNBT(TagType.BYTE_ARRAY, payload, new TypeWrapper<Byte[]>() {}, this);
	}
	
	/**
	 * Convenience method. Reads data from a Byte[]
	 * 
	 * @param out the stream to write to
	 * @param object the Byte[] to read from
	 * @throws NBTParseException if there was a parse exception
	 */
	public void writeByteArray(DataOutputStream out, Byte[] object) throws NBTParseException {
		registry.get(Byte[].class).toNBT(out, object, new TypeWrapper<Byte[]>() {}, this);
	}
	
	/**
	 * Gets the adapter for a string
	 * 
	 * @return the string adapter
	 */
	public NBTAdapter<?> getStringAdapter() {
		return registry.get(String.class);
	}
	
	/**
	 * Convenience method. Writes data to a String object
	 * 
	 * @param payload the stream to write to
	 * @return the String of data
	 * @throws NBTParseException if there was a parse exception
	 */
	public String fromString(DataInputStream payload) throws NBTParseException {
		return (String) registry.get(String.class).fromNBT(TagType.STRING, payload, new TypeWrapper<String>() {}, this);
	}
	
	/**
	 * Convenience method. Reads data from a String
	 * 
	 * @param out the stream to write to
	 * @param object the String to read from
	 * @throws NBTParseException if there was a parse exception
	 */
	public void writeString(DataOutputStream out, String object) throws NBTParseException {
		registry.get(String.class).toNBT(out, object, new TypeWrapper<String>() {}, this);
	}
	
	/**
	 * Gets the adapter for an integer array
	 * 
	 * @return the int array adapter
	 */
	public NBTAdapter<?> getIntArrayAdapter() {
		return registry.get(Long[].class);
	}
	
	/**
	 * Convenience method. Writes data to an Integer[] object
	 * 
	 * @param payload the stream to write to
	 * @return the Integer[] of data
	 * @throws NBTParseException if there was a parse exception
	 */
	public Integer[] fromIntArray(DataInputStream payload) throws NBTParseException {
		return (Integer[]) registry.get(Integer[].class)
			.fromNBT(TagType.INT_ARRAY, payload, new TypeWrapper<Integer[]>() {}, this);
	}
	
	/**
	 * Convenience method. Reads data from an Integer[]
	 * 
	 * @param out the stream to write to
	 * @param object the Integer[] to read from
	 * @throws NBTParseException if there was a parse exception
	 */
	public void writeIntArray(DataOutputStream out, Integer[] object) throws NBTParseException {
		registry.get(Integer[].class).toNBT(out, object, new TypeWrapper<Integer[]>() {}, this);
	}
	
	/**
	 * Gets the adapter for an long array
	 * 
	 * @return the long array adapter
	 */
	public NBTAdapter<?> getLongArrayAdapter() {
		return registry.get(Long[].class);
	}
	
	/**
	 * Convenience method. Writes data to an Long[] object
	 * 
	 * @param payload the stream to write to
	 * @return the Long[] of data
	 * @throws NBTParseException if there was a parse exception
	 */
	public Long[] fromLongArray(DataInputStream payload) throws NBTParseException {
		return (Long[]) registry.get(Long[].class)
			.fromNBT(TagType.LONG_ARRAY, payload, new TypeWrapper<Long[]>() {}, this);
	}
	
	/**
	 * Convenience method. Reads data from an Long[]
	 * 
	 * @param out the stream to write to
	 * @param object the Long[] to read from
	 * @throws NBTParseException if there was a parse exception
	 */
	public void writeLongArray(DataOutputStream out, Long[] object) throws NBTParseException {
		registry.get(Long[].class).toNBT(out, object, new TypeWrapper<Long[]>() {}, this);
	}
	
	/**
	 * A Builder for {@link AdapterRegistry}
	 * 
	 * @author MrYurihi Redstone
	 *
	 */
	public static class Builder {
		
		AdapterRegistry reg;
		
		/**
		 * Creates a new {@link AdapterRegistry} Builder with all of the default
		 * adapters
		 */
		public Builder() {
			reg = new AdapterRegistry();
		}
		
		/**
		 * Add an {@link NBTAdapter} to the registry
		 * 
		 * @param type the type the adapter works on
		 * @param adapter the adapter
		 * @param <B> the type the adapter works on
		 * @return this
		 */
		public <B> Builder addAdapter(Class<B> type, NBTAdapter<B> adapter) {
			reg.registry.put(type, adapter);
			return this;
		}
		
		/**
		 * Add an {@link NBTAdapterFactory} to the registry
		 * 
		 * @param type the type the adapter factory works on
		 * @param factory the adapter factory
		 * @return this
		 */
		public Builder addFactory(Class<?> type, NBTAdapterFactory factory) {
			reg.factory.put(type, factory);
			return this;
		}
		
		/**
		 * Creates an {@link AdapterRegistry} for use
		 * 
		 * @return the registry
		 */
		public AdapterRegistry create() {
			return reg;
		}
	}
}
