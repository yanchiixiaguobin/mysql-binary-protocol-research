package com.mysql.protocol;

import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import com.mysql.protocol.packet.connect.QuitPacket;
import org.junit.Test;

import com.mysql.protocol.util.HexUtil;

/**
 * 
 * <pre><b>test quit packet.</b></pre>
 * @author 
 * <pre>seaboat</pre>
 * <pre><b>email: </b>849586227@qq.com</pre>
 * <pre><b>blog: </b>http://blog.csdn.net/wangyangzhizhou</pre>
 * @version 1.0
 */
public class QuitPacketTest {
	@Test
	public void produce() {
		QuitPacket quit = new QuitPacket();
		quit.payload = 1;
		quit.packetID = 0;
		ByteBuffer buffer = ByteBuffer.allocate(256);
		quit.write(buffer);
		buffer.flip();
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes, 0, bytes.length);
		String result = HexUtil.Bytes2HexString(bytes);
		System.out.println(result);
		assertTrue(Integer.valueOf(result.substring(0, 2), 16) == result
				.length() / 2 - 4);

		QuitPacket quit2 = new QuitPacket();
		quit2.read(bytes);
		assertTrue(result.equals("0100000001"));
	}

}
