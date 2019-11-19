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

/**
 * A wrapper for exceptions passed by Adapters
 * 
 * @author MrYurihi Redstone
 */
public class NBTParseException extends Exception {
	private static final long serialVersionUID = 1564827538291444240L;
	
	public NBTParseException() {
		super();
	}
	
	public NBTParseException(String message) {
		super(message);
	}
	
	public NBTParseException(Throwable cause) {
		super(cause instanceof NBTParseException? cause.getCause(): cause);
	}
	
	public NBTParseException(String message, Throwable cause) {
		super(message, cause instanceof NBTParseException? cause.getCause(): cause);
	}
	
	protected NBTParseException(String message, Throwable cause, boolean enableSuppresion, boolean writableStackTrace) {
		super(
			message,
			cause instanceof NBTParseException? cause.getCause(): cause,
			enableSuppresion,
			writableStackTrace
		);
	}
}
