package com.mysql.protocol.packet.table;

import java.nio.ByteBuffer;

import com.mysql.protocol.MysqlMessage;
import com.mysql.protocol.packet.MysqlPacket;
import com.mysql.protocol.util.BufferUtil;

public class FieldListPacket extends MysqlPacket {
	public byte flag;
	public byte[] table;
	public byte[] fieldWildcard;

	@Override
	public void read(byte[] data) {
		MysqlMessage mm = new MysqlMessage(data);
		packetLength = mm.readUB3();
		packetId = mm.read();
		flag = mm.read();
		table = mm.readBytesWithNull();
		fieldWildcard = mm.readBytes();
	}

	@Override
	public void write(ByteBuffer buffer) {
		BufferUtil.writeUB3(buffer, calcPacketSize());
		buffer.put(packetId);
		buffer.put(COM_FIELD_LIST);
		BufferUtil.writeWithNull(buffer, table);
		buffer.put(fieldWildcard);
	}

	@Override
	public int calcPacketSize() {
		int i = 1;
		i += table.length + 1;
		i += fieldWildcard.length;
		return i;
	}

	@Override
	protected String getPacketInfo() {
		return "MySQL Field List Packet";
	}

}
