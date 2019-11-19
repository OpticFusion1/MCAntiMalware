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
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import com.github.mryurihi.tbnbt.TagType;
import com.github.mryurihi.tbnbt.adapter.AdapterRegistry;
import com.github.mryurihi.tbnbt.adapter.NBTAdapter;
import com.github.mryurihi.tbnbt.adapter.NBTAdapterFactory;
import com.github.mryurihi.tbnbt.adapter.NBTParseException;
import com.github.mryurihi.tbnbt.adapter.TypeWrapper;

public class CollectionAdapterFactory implements NBTAdapterFactory {
	
	@SuppressWarnings({
		"unchecked", "rawtypes"
	})
	@Override
	public <T> NBTAdapter<T> create(AdapterRegistry registry, TypeWrapper<T> type) {
		Class<?> itemClass = (Class<?>) ((ParameterizedType) type.getType()).getActualTypeArguments()[0];
		return (NBTAdapter<T>) new Adapter(registry.getAdapterForObject(TypeWrapper.of(itemClass)), itemClass);
	}
	
	private static class Adapter<E> extends NBTAdapter<Collection<E>> {
		
		private NBTAdapter<E> itemAdapter;
		private Class<E> itemClass;
		
		public Adapter(NBTAdapter<E> itemAdapter, Class<E> itemClass) {
			this.itemAdapter = itemAdapter;
			this.itemClass = itemClass;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Collection<E> fromNBT(
			TagType id,
			DataInputStream payload,
			TypeWrapper<?> type,
			AdapterRegistry registry
		) throws NBTParseException {
			try {
				Constructor<?> constr = type.getClassType().getDeclaredConstructor();
				constr.setAccessible(true);
				Collection<E> out = (Collection<E>) constr.newInstance();
				TagType itemId = TagType.getTypeById(registry.fromByte(payload));
				int length = payload.readInt();
				for(int i = 0; i < length; i++) {
					out.add((E) itemAdapter.fromNBT(itemId, payload, TypeWrapper.of(itemClass), registry));
				}
				return out;
			} catch(Exception e) {
				throw new NBTParseException(e);
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void toNBT(
			DataOutputStream out,
			Object object,
			TypeWrapper<?> type,
			AdapterRegistry registry
		) throws NBTParseException {
			try {
				Collection<E> col = (Collection<E>) object;
				registry.writeByte(out, (byte) itemAdapter.getId().getId());
				registry.writeInt(out, col.size());
				for(E e: col) {
					itemAdapter.toNBT(out, e, TypeWrapper.of(e.getClass()), registry);
				}
			} catch(Exception e) {
				throw new NBTParseException(e);
			}
		}
		
		@Override
		public TagType getId() {
			return TagType.LIST;
		}
	}
}
