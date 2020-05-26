package com.mysql.protocol.packet;

import java.nio.ByteBuffer;

import com.mysql.protocol.MysqlMessage;
import com.mysql.protocol.packet.MysqlPacket;
import com.mysql.protocol.util.BufferUtil;

public class QueryPacket extends MysqlPacket {
	public byte flag;
	public byte[] message;

	public void read(byte[] data) {
		MysqlMessage mm = new MysqlMessage(data);
		packetLength = mm.readUB3();
		packetId = mm.read();
		flag = mm.read();
		message = mm.readBytes();
	}

	public void write(ByteBuffer buffer) {
		int size = calcPacketSize();//前面的一个flag+messgag的字节长度
		BufferUtil.writeUB3(buffer, size);//存放的长度的字节数为3个字节，所以调用了wirteUB3
		buffer.put(packetId);
		buffer.put(COM_QUERY);
		buffer.put(message);
	}

	@Override
	public int calcPacketSize() {
		int size = 1;
		if (message != null) {
			size += message.length;
		}
		return size;
	}

	@Override
	protected String getPacketInfo() {
		return "MySQL Query Packet";
	}

}
