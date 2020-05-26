package com.mysql.protocol.packet.database;

import java.nio.ByteBuffer;

import com.mysql.protocol.MysqlMessage;
import com.mysql.protocol.packet.MysqlPacket;
import com.mysql.protocol.util.BufferUtil;

public class InitDBPacket extends MysqlPacket {
	public byte flag;
	public byte[] schema;

	@Override
	public void read(byte[] data) {
		MysqlMessage mm = new MysqlMessage(data);
		packetLength = mm.readUB3();
		packetId = mm.read();
		flag = mm.read();
		this.schema = mm.readBytes();
	}

	@Override
	public void write(ByteBuffer buffer) {
		BufferUtil.writeUB3(buffer, calcPacketSize());
		buffer.put(packetId);
		buffer.put(COM_INIT_DB);
		buffer.put(schema);
	}

	@Override
	public int calcPacketSize() {
		int i = 1;
		i += schema.length;
		return i;
	}

	@Override
	protected String getPacketInfo() {
		return "MySQL Init DB Packet";
	}

}
