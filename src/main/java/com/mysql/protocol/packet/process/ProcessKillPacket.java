package com.mysql.protocol.packet.process;

import java.nio.ByteBuffer;

import com.mysql.protocol.MysqlMessage;
import com.mysql.protocol.packet.MysqlPacket;
import com.mysql.protocol.util.BufferUtil;

public class ProcessKillPacket extends MysqlPacket {

	public byte flag = (byte) 0xfe;
	public int connectionId;

	@Override
	public void read(byte[] data) {
		MysqlMessage mm = new MysqlMessage(data);
		packetLength = mm.readUB3();
		packetId = mm.read();
		flag = mm.read();
		connectionId = mm.readInt();
	}

	@Override
	public void write(ByteBuffer buffer) {
		int size = calcPacketSize();
		BufferUtil.writeUB3(buffer, size);
		buffer.put(packetId);
		buffer.put(COM_PROCESS_KILL);
		BufferUtil.writeInt(buffer, connectionId);
	}

	@Override
	public int calcPacketSize() {
		return 5;
	}

	@Override
	protected String getPacketInfo() {
		return "MySQL Process Kill Packet";
	}

}
