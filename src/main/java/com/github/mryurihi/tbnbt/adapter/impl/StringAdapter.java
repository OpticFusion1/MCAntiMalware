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
package com.github.mryurihi.tbnbt.adapter.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import com.github.mryurihi.tbnbt.TagType;
import com.github.mryurihi.tbnbt.adapter.AdapterRegistry;
import com.github.mryurihi.tbnbt.adapter.NBTAdapter;
import com.github.mryurihi.tbnbt.adapter.NBTParseException;
import com.github.mryurihi.tbnbt.adapter.TypeWrapper;

public class StringAdapter extends NBTAdapter<String> {
	
	@Override
	public String fromNBT(
		TagType id,
		DataInputStream payload,
		TypeWrapper<?> type,
		AdapterRegistry registry
	) throws NBTParseException {
		if(
			!id.equals(TagType.STRING)
		) throw new NBTParseException(String.format("id %s does not match required id 8", id.getId()));
		short length = (short) registry.fromShort(payload);
		byte[] out = new byte[length];
		for(int i = 0; i < length; i++) {
			out[i] = (byte) registry.fromByte(payload);
		}
		return new String(out);
	}
	
	@Override
	public void toNBT(
		DataOutputStream out,
		Object object,
		TypeWrapper<?> type,
		AdapterRegistry registry
	) throws NBTParseException {
		String string = (String) object;
		try {
			registry.getShortAdapter().toNBT(out, (short) string.length(), null, registry);
			for(byte b: string.getBytes())
				out.writeByte(b);
		} catch(Exception e) {
			throw new NBTParseException(e);
		}
	}
	
	@Override
	public TagType getId() {
		return TagType.STRING;
	}
}
