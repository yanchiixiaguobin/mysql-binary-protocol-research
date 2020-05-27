package com.mysql.protocol.packet.common;

import java.nio.ByteBuffer;

import com.mysql.protocol.MySQLMessage;
import com.mysql.protocol.packet.MySQLPacket;
import com.mysql.protocol.util.BufferUtil;

public class ErrorPacket extends MySQLPacket {
	public static final byte header = (byte) 0xFF;
	public int errno;
	public byte mark = (byte) '#';
	public byte[] sqlState = "HY000".getBytes();
	public byte[] message;

	@Override
	public void read(byte[] data) {
		MySQLMessage mm = new MySQLMessage(data);
		packetLength = mm.readUB3();
		packetID = mm.read();
		mm.read();
		errno = mm.readUB2();
		if (mm.hasRemaining() && (mm.read(mm.position()) == (byte) '#')) {
			mm.read();
			sqlState = mm.readBytes(5);
		}
		message = mm.readBytes();
	}

	@Override
	public void write(ByteBuffer buffer) {
		int size = calcPacketSize();
		BufferUtil.writeUB3(buffer, size);
		buffer.put(packetID);
		buffer.put(header);
		BufferUtil.writeUB2(buffer, errno);
		buffer.put(mark);
		buffer.put(sqlState);
		buffer.put(message);
	}

	@Override
	public int calcPacketSize() {
		int size = 9;// 1 + 2 + 1 + 5
		if (message != null) {
			size += message.length;
		}
		return size;
	}

	@Override
	protected String getPacketInfo() {
		return "MySQL Error Packet";
	}

}
