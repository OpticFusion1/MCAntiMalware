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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import com.github.mryurihi.tbnbt.TagType;
import com.github.mryurihi.tbnbt.adapter.AdapterRegistry;
import com.github.mryurihi.tbnbt.adapter.NBTParseException;
import com.github.mryurihi.tbnbt.adapter.TypeWrapper;
import com.github.mryurihi.tbnbt.tag.NBTTag;
import com.github.mryurihi.tbnbt.tag.NBTTagString;

/**
 * An input stream that reads NBT data.
 * 
 * @author MrYurihi Redstone
 */
public class NBTInputStream extends InputStream {
	
	private DataInputStream dis;
	
	public NBTInputStream(InputStream is, boolean compressed) throws IOException {
		if(compressed) {
			is = new GZIPInputStream(is);
		}
		dis = new DataInputStream(is);
	}
	
	public NBTInputStream(InputStream is) throws IOException {
		this(is, true);
	}
	
	/**
	 * Reads an NBT tag from this stream
	 * 
	 * @param named if the tag is named
	 * @return The NBTTag
	 * @throws IOException if there are any I/O exceptions while reading
	 */
	public NBTTag readTag(boolean named) throws IOException {
		byte type = dis.readByte();
		if(named) new NBTTagString("").readPayloadBytes(dis);
		return NBTTag.newTagByType(TagType.getTypeById(type), dis);
	}
	
	/**
	 * Reads a named NBT tag from this stream
	 * 
	 * @return The NBTTag
	 * @throws IOException if there are any I/O exceptions while reading
	 */
	public NBTTag readTag() throws IOException {
		return readTag(true);
	}
	
	/**
	 * Reads an NBT tag from this stream into an object
	 * 
	 * @param type the type of the object to read to
	 * @param registry the registry to use
	 * @param <T> the type to write into
	 * @return the object that has been written to
	 * @throws IOException if an I/O exception occurs while writing to the object
	 * @throws NBTParseException If an exception occurs while parsing NBT
	 */
	@SuppressWarnings("unchecked")
	public <T> T readToType(TypeWrapper<T> type, AdapterRegistry registry) throws IOException, NBTParseException {
		TagType id = TagType.getTypeById(dis.readByte());
		registry.fromString(dis);
		return (T) registry.getAdapterForObject(type).fromNBT(id, dis, type, registry);
	}
	
	/**
	 * Reads an NBT tag from this stream into an object. Will create a new reigstry
	 * object
	 * 
	 * @param type the type of the object to read to
	 * @param <T> the type to read from
	 * @return the object that has been written to
	 * @throws IOException if an I/O exception occurs while writing to the object
	 * @throws NBTParseException If an exception occurs while parsing NBT
	 */
	public <T> T readToType(TypeWrapper<T> type) throws IOException, NBTParseException {
		return readToType(type, new AdapterRegistry.Builder().create());
	}
	
	@Override
	public void close() throws IOException {
		dis.close();
	}
	
	@Override
	public int read() throws IOException {
		return dis.read();
	}
	
}
