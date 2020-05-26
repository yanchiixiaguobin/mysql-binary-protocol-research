package com.mysql.protocol.util;

import com.mysql.protocol.packet.MysqlPacket;

public class ByteUtil {

    /**
     *
     * @param mysqlPacket 利用多态使用mysqlPacket获得packetLength
     * @param bytes 接受到的字节内容
     */
    public static void bytesCut(MysqlPacket mysqlPacket ,byte[] bytes){
        System.arraycopy(bytes, mysqlPacket.packetLength+4, bytes, 0, bytes.length-mysqlPacket.packetLength-4);
    }

}
