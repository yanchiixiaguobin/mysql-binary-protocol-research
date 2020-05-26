package com.mysql.protocol.packet.connect;

import java.nio.ByteBuffer;

import com.mysql.protocol.MysqlMessage;
import com.mysql.protocol.packet.MysqlPacket;
import com.mysql.protocol.util.BufferUtil;


public class ShutdownPacket extends MysqlPacket {

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
		MysqlMessage mm = new MysqlMessage(data);
		packetLength = mm.readUB3();
		packetId = mm.read();
		if (packetLength == 2)
			type = mm.read();
	}

	@Override
	public void write(ByteBuffer buffer) {
		int size = calcPacketSize();
		BufferUtil.writeUB3(buffer, size);
		buffer.put(packetId);
		buffer.put(COM_SHUTDOWN);
		buffer.put(type);
	}

}
