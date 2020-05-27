package com.mysql.protocol;

import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import com.mysql.protocol.packet.common.OKPacket;
import com.mysql.protocol.util.HexUtil;
import org.junit.Test;

/**
 * 
 * <pre><b>test ok packet.</b></pre>
 * @author 
 * <pre>seaboat</pre>
 * <pre><b>email: </b>849586227@qq.com</pre>
 * <pre><b>blog: </b>http://blog.csdn.net/wangyangzhizhou</pre>
 * @version 1.0
 */
public class OKPacketTest {
	@Test
	public void produce() {
		OKPacket ok = new OKPacket();
		ok.packetID = 2;
		ok.affectedRows = 0;
		ok.insertId = 0;
		ok.serverStatus = 2;
		ok.warningCount = 0;
		ByteBuffer buffer = ByteBuffer.allocate(256);
		ok.write(buffer);
		buffer.flip();
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes, 0, bytes.length);
		String result = HexUtil.Bytes2HexString(bytes);
		System.out.println(result);
		assertTrue(Integer.valueOf(result.substring(0, 2), 16) == result
				.length() / 2 - 4);

		OKPacket ok2 = new OKPacket();
		ok2.read(bytes);
		//auth ok
		assertTrue(result.equals("0700000200000002000000"));
	}

}
