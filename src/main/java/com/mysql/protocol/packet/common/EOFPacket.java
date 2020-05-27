package com.mysql.protocol.packet.common;

import java.nio.ByteBuffer;

import com.mysql.protocol.MySQLMessage;
import com.mysql.protocol.packet.MySQLPacket;
import com.mysql.protocol.util.BufferUtil;

public class EOFPacket extends MySQLPacket {

	public byte header = (byte) 0xFE;
	public int warningCount;
	public int status = 2;

	@Override
	public void read(byte[] data) {
		MySQLMessage mm = new MySQLMessage(data);
		packetLength = mm.readUB3();
		packetID = mm.read();
		header = mm.read();
		warningCount = mm.readUB2();
		status = mm.readUB2();
	}

	@Override
	public void write(ByteBuffer buffer) {
		int size = calcPacketSize();
		BufferUtil.writeUB3(buffer, size);
		buffer.put(packetID);
		buffer.put(header);
		BufferUtil.writeUB2(buffer, warningCount);
		BufferUtil.writeUB2(buffer, status);
	}

	@Override
	public int calcPacketSize() {
		return 5;
	}

	@Override
	protected String getPacketInfo() {
		return "MySQL EOF Packet";
	}

}
