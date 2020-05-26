package com.jiagu.mysql.protocol;

import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import com.jiagu.mysql.protocol.util.HexUtil;
import org.junit.Test;

/**
 * 
 * <pre><b>test init db packet.</b></pre>
 * @author 
 * <pre>seaboat</pre>
 * <pre><b>email: </b>849586227@qq.com</pre>
 * <pre><b>blog: </b>http://blog.csdn.net/wangyangzhizhou</pre>
 * @version 1.0
 */
public class InitDBPacketTest {
	@Test
	public void produce() {
		byte[] table = { 't', 'e', 's', 't' };
		InitDBPacket initDB = new InitDBPacket();
		initDB.packetId = 2;
		initDB.schema = table;
		initDB.flag = MysqlPacket.COM_INIT_DB;
		ByteBuffer buffer = ByteBuffer.allocate(256);
		initDB.write(buffer);
		buffer.flip();
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes, 0, bytes.length);
		String result = HexUtil.Bytes2HexString(bytes);
		System.out.println(result);
		assertTrue(Integer.valueOf(result.substring(0, 2), 16) == result
				.length() / 2 - 4);
		byte[] test = { 5, 0, 0, 2, 2, 116, 101, 115, 116 };
		InitDBPacket initDB2 = new InitDBPacket();
		initDB2.read(test);
		System.out.println(new String(initDB2.schema));
		assertTrue(new String(initDB2.schema).equals("test"));
	}

}
