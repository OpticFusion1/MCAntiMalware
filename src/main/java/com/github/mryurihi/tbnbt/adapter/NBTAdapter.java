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

import com.github.mryurihi.tbnbt.TagType;

public abstract class NBTAdapter<T> {
	
	/**
	 * Get an object from NBT data
	 * 
	 * @param id the id for input tag
	 * @param payload the stream to be read from
	 * @param type the type of object to be written into
	 * @param registry the adapter registry that contains the adapters and factories
	 *            available
	 * @return an object with the type of the parameter {@code type}
	 * @throws NBTParseException there is an exception parsing
	 */
	public abstract T fromNBT(
		TagType id,
		DataInputStream payload,
		TypeWrapper<?> type,
		AdapterRegistry registry
	) throws NBTParseException;
	
	/**
	 * Write NBT data from an object
	 * 
	 * @param out the stream to be written to
	 * @param object the object to read from
	 * @param type the type of the object to be read from
	 * @param registry the adapter registry that contains the adapters and factories
	 *            available
	 * @throws NBTParseException of there is an exception parsing
	 */
	public abstract void toNBT(
		DataOutputStream out,
		Object object,
		TypeWrapper<?> type,
		AdapterRegistry registry
	) throws NBTParseException;
	
	/**
	 * Gets the type of tag this adapter writes.
	 * 
	 * @return the tag type
	 */
	public abstract TagType getId();
}
