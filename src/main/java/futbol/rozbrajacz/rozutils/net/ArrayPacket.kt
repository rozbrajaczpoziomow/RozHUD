package futbol.rozbrajacz.rozutils.net
/*
	Copyright (c) rozbrajaczpoziomow

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU Affero General Public License version 3
	as published by the Free Software Foundation.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Affero General Public License for more details.

	You should have received a copy of the GNU Affero General Public License
	along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.simpleimpl.IMessage

class ArrayPacket : IMessage {
	var arr = arrayOf<String>()
	var nanos = 0L

	override fun fromBytes(buf: ByteBuf) {
		arr = Array(buf.readInt()) {
			buf.readCharSequence(buf.readInt(), Charsets.UTF_8).toString()
		}
		nanos = buf.readLong()
	}

	override fun toBytes(buf: ByteBuf) {
		buf.writeInt(arr.size)
		for(str in arr) {
			buf.writeInt(str.length)
			buf.writeCharSequence(str, Charsets.UTF_8)
		}
		buf.writeLong(System.nanoTime())
	}

	constructor() // required by FML

	constructor(arr: Array<String>) {
		this.arr = arr
		nanos = System.nanoTime()
	}
}
