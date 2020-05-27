package com.mysql.protocol;

import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import com.mysql.protocol.packet.MySQLPacket;
import com.mysql.protocol.packet.table.FieldListPacket;
import com.mysql.protocol.util.HexUtil;
import org.junit.Test;

/**
 * 
 * <pre><b>test field list packet.</b></pre>
 * @author 
 * <pre>seaboat</pre>
 * <pre><b>email: </b>849586227@qq.com</pre>
 * <pre><b>blog: </b>http://blog.csdn.net/wangyangzhizhou</pre>
 * @version 1.0
 */
public class FieldListPacketTest {
	@Test
	public void produce() {
		byte[] table = { 't', 'e', 's', 't' };
		byte[] fieldWildcard = { 'w', 'h', 'e', 'r', 'e' };
		FieldListPacket fieldListPacket = new FieldListPacket();
		fieldListPacket.packetID = 2;
		fieldListPacket.table = table;
		fieldListPacket.fieldWildcard = fieldWildcard;
		fieldListPacket.flag = MySQLPacket.COM_FIELD_LIST;
		ByteBuffer buffer = ByteBuffer.allocate(256);
		fieldListPacket.write(buffer);
		buffer.flip();
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes, 0, bytes.length);
		String result = HexUtil.Bytes2HexString(bytes);
		System.out.println(result);
		assertTrue(Integer.valueOf(result.substring(0, 2), 16) == result
				.length() / 2 - 4);
		byte[] test = { 11, 0, 0, 2, 4, 116, 101, 115, 116, 0, 119, 104, 101,
				114, 101 };
		FieldListPacket fieldListPacket2 = new FieldListPacket();
		fieldListPacket2.read(test);
		System.out.println(new String(fieldListPacket2.table));
		System.out.println(new String(fieldListPacket2.fieldWildcard));
		assertTrue(new String(fieldListPacket2.table).equals("test"));
		assertTrue(new String(fieldListPacket2.fieldWildcard).equals("where"));
	}

}
