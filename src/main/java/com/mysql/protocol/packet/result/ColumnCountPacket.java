package com.mysql.protocol.packet.result;

import java.nio.ByteBuffer;

import com.mysql.protocol.MySQLMessage;
import com.mysql.protocol.packet.MySQLPacket;
import com.mysql.protocol.util.BufferUtil;

public class ColumnCountPacket extends MySQLPacket {

	public int columnCount;

	public void read(byte[] data) {
		MySQLMessage mm = new MySQLMessage(data);
		this.packetLength = mm.readUB3();
		this.packetID = mm.read();
		this.columnCount = (int) mm.readLength();
	}

	@Override
	public void write(ByteBuffer buffer) {
		int size = calcPacketSize();
		BufferUtil.writeUB3(buffer, size);
		buffer.put(packetID);
		BufferUtil.writeLength(buffer, columnCount);
	}

	@Override
	public int calcPacketSize() {
		int size = BufferUtil.getLength(columnCount);
		return size;
	}

	@Override
	protected String getPacketInfo() {
		return "MySQL Column Count Packet";
	}

}
