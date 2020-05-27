package com.mysql.protocol.util;

import com.mysql.protocol.constant.Capabilities;
import com.mysql.protocol.constant.ColumnType;
import com.mysql.protocol.packet.QueryPacket;
import com.mysql.protocol.packet.common.OKPacket;
import com.mysql.protocol.packet.connect.AuthPacket;
import com.mysql.protocol.packet.common.EOFPacket;
import com.mysql.protocol.packet.connect.HandshakePacket;
import com.mysql.protocol.packet.result.ColumnCountPacket;
import com.mysql.protocol.packet.result.ColumnDefinitionPacket;
import com.mysql.protocol.packet.result.ResultsetRowPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionUtil.class);

    public static void threeHands(Socket socket, String host, int port) {
        try {
            socket.connect(new InetSocketAddress(host, port));
        } catch (Exception e) {
            LOG.debug("connect failed:", e);
        }
    }


    public static HandshakePacket processHandShake(InputStream inputStream) {
        try {
            byte[] bytesTemp = new byte[1024 * 1024 * 16];//临时存放输入流的字节，一个数据最多含有2^24-1个字节
            int len = 0;
            //接受handshake包
            len = inputStream.read(bytesTemp);
            byte[] bytes = new byte[len];
            System.arraycopy(bytesTemp, 0, bytes, 0, len);
            //System.out.println("handshake包的十六进制:"+Bytes2HexString(bytes));

            //解析handshake包
            HandshakePacket handshakePacket = new HandshakePacket();
            handshakePacket.read(bytes);
            return handshakePacket;
        } catch(Exception e) {
            LOG.debug("handshake parse failed", e);
            return null;
        }
    }

    public static void sendAuthPacket(HandshakePacket handshakePacket, String user, String password, String dataBase, OutputStream outputStream) {
        //发送authPacket包
        try {
            byte[] authPacket = produceAuthPacket(handshakePacket.seed, handshakePacket.restOfScrambleBuff, user, password, dataBase);
            outputStream.write(authPacket);
            outputStream.flush();
        } catch (Exception e) {
            LOG.debug("outputstream write or flush failed", e);
        }
    }

    public static byte[] produceAuthPacket(byte[] rand1,byte[] rand2, String user , String password,String database) {

        byte[] seed = new byte[rand1.length + rand2.length];
        System.arraycopy(rand1, 0, seed, 0, rand1.length);
        System.arraycopy(rand2, 0, seed, rand1.length, rand2.length);

        AuthPacket auth = new AuthPacket();
        auth.packetID = 1;
        auth.clientFlags = getClientCapabilities();
        auth.maxPacketSize = 1024 * 1024 * 1024;
        auth.user = user;
        try {
            auth.password = SecurityUtil
                    .scramble411(password.getBytes(), seed);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        auth.database = database;

        ByteBuffer buffer = ByteBuffer.allocate(256);
        auth.write(buffer);
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes, 0, bytes.length);
        return bytes;
    }

    public static int getClientCapabilities() {
        int flag = 0;
        flag |= Capabilities.CLIENT_LONG_PASSWORD;
        flag |= Capabilities.CLIENT_FOUND_ROWS;
        flag |= Capabilities.CLIENT_LONG_FLAG;
        flag |= Capabilities.CLIENT_CONNECT_WITH_DB;
        flag |= Capabilities.CLIENT_ODBC;
        flag |= Capabilities.CLIENT_IGNORE_SPACE;
        flag |= Capabilities.CLIENT_PROTOCOL_41;
        flag |= Capabilities.CLIENT_INTERACTIVE;
        flag |= Capabilities.CLIENT_IGNORE_SIGPIPE;
        flag |= Capabilities.CLIENT_TRANSACTIONS;
        flag |= Capabilities.CLIENT_SECURE_CONNECTION;
        return flag;
    }


    public static OKPacket processOKPacket(InputStream inputStreams) {
        try {
            byte[] bytesTemp = new byte[1024 * 16];//临时存放输入流的字节，一个数据最多含有2^24-1个字节
            int len = inputStreams.read(bytesTemp);
            byte[] bytes = new byte[len];
            System.arraycopy(bytesTemp, 0, bytes, 0, len);
            //System.out.println("OK 包的十六进制:"+Bytes2HexString(bytes));
            OKPacket okPacket = new OKPacket();
            okPacket.read(bytes);
            return okPacket;
        } catch (Exception e) {
            LOG.debug("processOKPacket failed", e);
            return null;
        }
    }

    public static void sendQueryPacket(OutputStream outputStream,String queryStr) {
        try {
            byte[] queryPacket = produceQueryPacket(queryStr);
            outputStream.write(queryPacket);
            outputStream.flush();
        } catch (Exception e) {
            LOG.debug("sendQueryPacket failed", e);
        }
    }

    public static byte[] produceQueryPacket(String queryStr) {
        QueryPacket query = new QueryPacket();
        query.flag = 3;//查询的标记，3为query
        query.message = queryStr.getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(256);
        query.write(buffer);//将包的大小、packedId(包的序列号)，flag,message，写到Buffer中
        buffer.flip();//写模式转为读模式
        byte[] bytes = new byte[buffer.remaining()];//buffer.remaining表示buffer中可读的为多少
        buffer.get(bytes, 0, bytes.length);//最后将请求的参数的二进制放在了bytes里
        return bytes;
    }

    public static void processResult(InputStream inputStreams) {
        try {
            byte[] bytesTemp = new byte[1024 * 16];
            int len = inputStreams.read(bytesTemp);
            byte[] bytes = new byte[len];
            System.arraycopy(bytesTemp, 0, bytes, 0, len);

            //解析columnCountPacket
            ColumnCountPacket columnCountPacket = processColumnCountPacket(bytes);
            //解析columnDef包
            ColumnDefinitionPacket[] columnDefinitionPackets = parseColumnDefinition(columnCountPacket.columnCount, bytes);
            //获取EOF包
            EOFPacket eofPacket = processEOFPacket(bytes);
            //解析resultSetRow 包
            ArrayList<ResultsetRowPacket> resultSetRowPackets = getResultSetRows(columnCountPacket.columnCount, bytes);
            for (ResultsetRowPacket r : resultSetRowPackets) {
                for (int i = 0; i < columnCountPacket.columnCount; i++)
                    System.out.print(new String(r.columnValues.get(i), "utf-8") + ",");
                System.out.println();
            }
        } catch (Exception e) {
            LOG.debug("processResult failed", e);
        }
    }

    public static  ColumnCountPacket processColumnCountPacket(byte[] bytes){
        ColumnCountPacket columnCountPacket = new ColumnCountPacket();
        columnCountPacket.read(bytes);
        ByteUtil.bytesCut(columnCountPacket,bytes);
        return columnCountPacket;
    }

    public static ColumnDefinitionPacket[] parseColumnDefinition(int columnCount,byte[] bytes){
        ColumnDefinitionPacket[] result = new ColumnDefinitionPacket[columnCount];
        for(int i=0; i<columnCount ;i++){
            ColumnDefinitionPacket columnDefPacket = new ColumnDefinitionPacket();
            columnDefPacket.read(bytes);
            result[i] = columnDefPacket;
            int deleteCount = columnDefPacket.packetLength+4;
            ByteUtil.bytesCut(columnDefPacket,bytes);
            //System.arraycopy(bytes, deleteCount, bytes, 0, bytes.length-deleteCount);
            //System.out.println("解析了columnDef之后的值"+Bytes2HexString(bytes));
        }
        return result;
    }

    public static EOFPacket processEOFPacket(byte[] bytes){
        EOFPacket eofPacket = new EOFPacket();
        eofPacket.read(bytes);
        //System.arraycopy(bytes, eofPacket.packetLength+4, bytes, 0, bytes.length- eofPacket.packetLength-4);
        ByteUtil.bytesCut(eofPacket,bytes);
        //System.out.println("截取掉eof之后的十六进制："+Bytes2HexString(bytes));
        return eofPacket;
    }

    public static ArrayList<ResultsetRowPacket> getResultSetRows(int columnCount ,byte[] bytes){
        EOFPacket eofPacket = new EOFPacket();
        ArrayList<ResultsetRowPacket> result = new ArrayList<ResultsetRowPacket>();
        while(true) {
            eofPacket.read(bytes);
            if ((eofPacket.header &0xff) == 0xfe)//读到了一个EOF包，表示结束
                break;
            ResultsetRowPacket resultSetRowPacket = new ResultsetRowPacket(columnCount);
            resultSetRowPacket.read(bytes);
            System.arraycopy(bytes, resultSetRowPacket.packetLength+4, bytes, 0, bytes.length- resultSetRowPacket.packetLength-4);
            result.add(resultSetRowPacket);
        }
        return result;
    }

    public static List<Map<String, String>> processResult2ListMapString(InputStream inputStreams) {
        try {
            byte[] bytesTemp = new byte[1024 * 16];
            int len = inputStreams.read(bytesTemp);
            byte[] bytes = new byte[len];
            System.arraycopy(bytesTemp, 0, bytes, 0, len);

            //解析columnCountPacket
            ColumnCountPacket columnCountPacket = processColumnCountPacket(bytes);
            //解析columnDef包
            ColumnDefinitionPacket[] columnDefinitionPackets = parseColumnDefinition(columnCountPacket.columnCount, bytes);
            //获取EOF包
            EOFPacket eofPacket = processEOFPacket(bytes);
            List<Map<String, String>> result = new ArrayList<Map<String, String>>();
            //解析resultSetRow 包
            ArrayList<ResultsetRowPacket> resultSetRowPackets = getResultSetRows(columnCountPacket.columnCount, bytes);
            for (ResultsetRowPacket r : resultSetRowPackets) {
                Map<String, String> map = new HashMap();
                for (int i = 0; i < columnCountPacket.columnCount; i++) {
                    map.put(new String(columnDefinitionPackets[i].name, "utf-8")
                            , new String(r.columnValues.get(i), "utf-8"));
                }
                result.add(map);
            }
            return result;
        } catch (Exception e) {
            LOG.debug("processResult2ListMapString failed", e);
            return null;
        }
    }

    public static List<Map<String, Object>> processResult2ListMapObject(InputStream inputStreams) {
        try {
            byte[] bytesTemp = new byte[1024 * 16];
            int len = inputStreams.read(bytesTemp);
            byte[] bytes = new byte[len];
            System.arraycopy(bytesTemp, 0, bytes, 0, len);

            //解析columnCountPacket
            ColumnCountPacket columnCountPacket = processColumnCountPacket(bytes);
            //解析columnDef包
            ColumnDefinitionPacket[] columnDefinitionPackets = parseColumnDefinition(columnCountPacket.columnCount, bytes);
            //获取EOF包
            EOFPacket eofPacket = processEOFPacket(bytes);
            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            //解析resultSetRow 包
            ArrayList<ResultsetRowPacket> resultSetRowPackets = getResultSetRows(columnCountPacket.columnCount, bytes);
            for (ResultsetRowPacket r : resultSetRowPackets) {
                Map<String, Object> map = new HashMap();
                for (int i = 0; i < columnCountPacket.columnCount; i++) {

                    map.put(new String(columnDefinitionPackets[i].name, "utf-8")
                            , getType(r.columnValues.get(i),columnDefinitionPackets[i].type));
                }
                result.add(map);
            }
            return result;
        } catch (Exception e) {
            LOG.debug("processResult22ListMapObject failed", e);
            return null;
        }
    }

    public static Object getType(byte[] charData, int type) {

        switch(type) {

            case ColumnType.FIELD_TYPE_LONGLONG:
                // mysql返回的数据并不是8字节的长整形、或无符号长整形,而是ascii码字符，所以这么处理
                String longValue = new String(charData);
                return Long.parseLong(longValue);
            case ColumnType.FIELD_TYPE_VAR_STRING:
                return new String(charData);

        }

        return null;
    }

}
