package com.mysql.protocol.packet.process;

import java.nio.ByteBuffer;

import com.mysql.protocol.MySQLMessage;
import com.mysql.protocol.packet.MySQLPacket;
import com.mysql.protocol.util.BufferUtil;

public class ProcessInfoPacket extends MySQLPacket {

	public byte payload;

	@Override
	public int calcPacketSize() {
		return 1;
	}

	@Override
	protected String getPacketInfo() {
		return "MySQL Process Info Packet";
	}

	@Override
	public void read(byte[] data) {
		MySQLMessage mm = new MySQLMessage(data);
		packetLength = mm.readUB3();
		packetID = mm.read();
		payload = mm.read();
	}

	@Override
	public void write(ByteBuffer buffer) {
		int size = calcPacketSize();
		BufferUtil.writeUB3(buffer, size);
		buffer.put(packetID);
		buffer.put(COM_PROCESS_INFO);
	}

}
