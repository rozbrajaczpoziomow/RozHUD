package futbol.rozbrajacz.rozhud.net

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
