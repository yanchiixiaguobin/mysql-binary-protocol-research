package com.mysql.protocol.packet.process;

import java.nio.ByteBuffer;

import com.mysql.protocol.MySQLMessage;
import com.mysql.protocol.packet.MySQLPacket;
import com.mysql.protocol.util.BufferUtil;

public class ProcessKillPacket extends MySQLPacket {

	public byte flag = (byte) 0xfe;
	public int connectionId;

	@Override
	public void read(byte[] data) {
		MySQLMessage mm = new MySQLMessage(data);
		packetLength = mm.readUB3();
		packetID = mm.read();
		flag = mm.read();
		connectionId = mm.readInt();
	}

	@Override
	public void write(ByteBuffer buffer) {
		int size = calcPacketSize();
		BufferUtil.writeUB3(buffer, size);
		buffer.put(packetID);
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
