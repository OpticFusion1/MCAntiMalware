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
package com.github.mryurihi.tbnbt.stream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import com.github.mryurihi.tbnbt.adapter.AdapterRegistry;
import com.github.mryurihi.tbnbt.adapter.NBTAdapter;
import com.github.mryurihi.tbnbt.adapter.NBTParseException;
import com.github.mryurihi.tbnbt.adapter.TypeWrapper;
import com.github.mryurihi.tbnbt.tag.NBTTag;
import com.github.mryurihi.tbnbt.tag.NBTTagString;

/**
 * An output stream that writes NBT data
 * 
 * @author MrYurihi Redstone
 */
public class NBTOutputStream extends OutputStream {
	
	private DataOutputStream dos;
	
	public NBTOutputStream(OutputStream out, boolean compressed) throws IOException {
		if(compressed) {
			out = new GZIPOutputStream(out);
		}
		dos = new DataOutputStream(out);
	}
	
	public NBTOutputStream(OutputStream out) throws IOException {
		this(out, true);
	}
	
	/**
	 * Writes an NBTTag to the stream.
	 * 
	 * @param tag the tag to write
	 * @param name the name of the tag
	 * @throws IOException if there are any I/O exceptions while writing data
	 */
	public void writeTag(NBTTag tag, String name) throws IOException {
		dos.writeByte(tag.getTagType().getId());
		new NBTTagString("").writePayloadBytes(dos);
		tag.writePayloadBytes(dos);
	}
	
	/**
	 * Writes an object to the stream
	 * 
	 * @param type the type of the object
	 * @param obj the object to be written
	 * @param registry the registry to use
	 * @param name the name of the tag to write
	 * @param <T> the type to write from
	 * @throws NBTParseException if there is an exception while parsing the data
	 * @throws IOException if there are any I/O exceptions when writing
	 */
	public <T> void writeFromObject(
		TypeWrapper<T> type,
		Object obj,
		String name,
		AdapterRegistry registry
	) throws NBTParseException, IOException {
		NBTAdapter<?> adapter = registry.getAdapterForObject(type);
		dos.writeByte(adapter.getId().getId());
		registry.writeString(dos, name);
		adapter.toNBT(dos, obj, type, registry);
		dos.close();
	}
	
	/**
	 * Writes an object to the stream. Will use a new registry
	 * 
	 * @param type the type of the object
	 * @param obj the object to be written
	 * @param name the name of the tag to write
	 * @param <T> the type to write from
	 * @throws NBTParseException if there is an exception while parsing the data
	 * @throws IOException if there are any I/O exceptions when writing
	 */
	public <
		T> void writeFromObject(TypeWrapper<T> type, Object obj, String name) throws NBTParseException, IOException {
		writeFromObject(type, obj, name, new AdapterRegistry.Builder().create());
	}
	
	/**
	 * Writes an object to the stream with a name of "". Will use a new registry
	 * 
	 * @param type the type of the object
	 * @param obj the object to be written
	 * @param <T> the type to write from
	 * @throws NBTParseException if there is an exception while parsing the data
	 * @throws IOException if there are any I/O exceptions when writing
	 */
	public <T> void writeFromObject(TypeWrapper<T> type, Object obj) throws NBTParseException, IOException {
		writeFromObject(type, obj, "");
	}
	
	@Override
	public void close() throws IOException {
		dos.close();
	}
	
	@Override
	public void write(int b) throws IOException {
		dos.write(b);
	}
}
