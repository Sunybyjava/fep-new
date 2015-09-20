package com.chooshine.fep.newtcpchannel;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.ArrayUtils;

public class HXByteBuffer implements Serializable {
	private static final long serialVersionUID = 1L;

	private byte[] _buffer = null;

	private int _length = 0;

	private int _capacity = 0;

	private int _scale = 64;

	public HXByteBuffer(int sz) {
		this._buffer = new byte[sz];
		this._capacity = sz;
		this._length = 0;
	}

	public HXByteBuffer(int sz, int scale) {
		this._buffer = new byte[sz];
		this._capacity = sz;
		this._length = 0;
		this._scale = scale;
	}

	public HXByteBuffer(byte[] data) {
		append(data);
	}

	public HXByteBuffer(byte[] data, boolean isRef) {
		if (isRef) {
			this._buffer = data;
			this._length = data.length;
		} else {
			append(data);
		}
	}

	public void append(byte[] data, int offset, int length) {
		if (length == 0)
			return;
		if (this._capacity <= length) {
			byte[] buffer1 = new byte[this._length + getScaleSize(length)];
			if (this._buffer != null)
				System.arraycopy(this._buffer, 0, buffer1, 0, this._length);
			this._buffer = buffer1;
		}

		System.arraycopy(data, offset, this._buffer, this._length, length);
		this._length += length;
		this._capacity = (this._buffer.length - this._length);
	}

	public void append(byte[] data) {
		if (data == null) {
			data = new byte[0];
		}
		append(data, 0, data.length);
	}

	public void clear() {
		this._length = 0;
		this._capacity = this._buffer.length;
	}

	public void append(String data) {
		append(data.getBytes());
	}

	public void append(byte b) {
		append(new byte[] { b });
	}

	public void append(int b) {
		append((byte) b);
	}

	public byte[] getBytes() {
		if (this._length == 0) {
			return new byte[0];
		}
		byte[] buffer = new byte[this._length];
		System.arraycopy(this._buffer, 0, buffer, 0, this._length);
		return buffer;
	}

	public int length() {
		return this._length;
	}

	public String substr(int offset, int length) {
		return new String(subbyte(offset, length));
	}

	public byte[] subbyte(int offset, int length) {
		if (offset + length > this._length) {
			throw new IllegalArgumentException(offset + "+" + length + ">"
					+ this._length);
		}

		byte[] buffer = new byte[length];
		System.arraycopy(this._buffer, offset, buffer, 0, length);
		return buffer;
	}

	public byte charAt(int offset) {
		return this._buffer[offset];
	}

	public int indexOf(int b, int offset) {
		if (offset > this._length) {
			throw new IllegalArgumentException(offset + ">" + this._length);
		}
		for (int i = offset; i < this._length; ++i) {
			if (this._buffer[i] == b)
				return i;
		}
		return -1;
	}

	public int indexOf(byte[] bb, int offset) {
		if ((bb == null) || (bb.length == 0) || (offset < 0)) {
			return -1;
		}
		while (true) {
			offset = ArrayUtils.indexOf(this._buffer, bb[0], offset);

			if (offset == -1) {
				return -1;
			}

			if (bb.length > this._length - offset) {
				return -1;
			}

			int i = 0;
			for (i = 0; i < bb.length; ++i) {
				if (this._buffer[(offset + i)] != bb[i]) {
					break;
				}
			}
			if (i == bb.length) {
				return offset;
			}

			++offset;
		}
	}

	public void repeat(byte b, int num) {
		if (num <= 0) {
			return;
		}
		byte[] nb = new byte[num];
		for (int i = 0; i < num; ++i) {
			nb[i] = b;
		}
		append(nb, 0, num);
	}

	public String toString() {
		if (this._buffer == null)
			return null;
		return new String(this._buffer, 0, this._length);
	}

	public String toString(String encoding) {
		try {
			if (this._buffer == null)
				return null;
			return new String(this._buffer, 0, this._length, encoding);
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	public byte[] remove(int offset, int length) {
		byte[] tmps = subbyte(offset, length);

		System.arraycopy(this._buffer, offset + length, this._buffer, offset,
				this._buffer.length - offset - length);

		return tmps;
	}

	private int getScaleSize(int len) {
		if (len / this._scale == 0) {
			return this._scale;
		}
		return ((len / this._scale + 1) * this._scale);
	}
}