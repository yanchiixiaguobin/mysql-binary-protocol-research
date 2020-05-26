package com.mysql.protocol.packet;

import java.nio.ByteBuffer;

import com.mysql.protocol.MysqlMessage;
import com.mysql.protocol.packet.MysqlPacket;
import com.mysql.protocol.util.BufferUtil;

public class StatisticsPacket extends MysqlPacket {

	public byte payload;
	
	@Override
	public int calcPacketSize() {
		return 1;
	}

	@Override
	protected String getPacketInfo() {
		return "MySQL Statistics Packet";
	}

	@Override
	public void read(byte[] data) {
		MysqlMessage mm = new MysqlMessage(data);
		packetLength = mm.readUB3();
		packetId = mm.read();
		payload = mm.read();
	}

	@Override
	public void write(ByteBuffer buffer) {
		int size = calcPacketSize();
		BufferUtil.writeUB3(buffer, size);
		buffer.put(packetId);
		buffer.put(COM_STATISTICS);
	}

}
