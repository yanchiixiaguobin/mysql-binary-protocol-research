package com.mysql.protocol.packet.connect;

import java.nio.ByteBuffer;

import com.mysql.protocol.MySQLMessage;
import com.mysql.protocol.packet.MySQLPacket;
import com.mysql.protocol.util.BufferUtil;


public class ShutdownPacket extends MySQLPacket {

	//default value
	public byte type = 0;

	@Override
	public int calcPacketSize() {
		return 2;
	}

	@Override
	protected String getPacketInfo() {
		return "MySQL Shutdown Packet";
	}

	@Override
	public void read(byte[] data) {
		MySQLMessage mm = new MySQLMessage(data);
		packetLength = mm.readUB3();
		packetID = mm.read();
		if (packetLength == 2)
			type = mm.read();
	}

	@Override
	public void write(ByteBuffer buffer) {
		int size = calcPacketSize();
		BufferUtil.writeUB3(buffer, size);
		buffer.put(packetID);
		buffer.put(COM_SHUTDOWN);
		buffer.put(type);
	}

}
