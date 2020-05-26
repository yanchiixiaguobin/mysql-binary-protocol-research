package com.mysql.protocol;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import com.mysql.protocol.packet.common.OKPacket;
import com.mysql.protocol.packet.connect.HandshakePacket;
import com.mysql.protocol.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connection {

    private static final Logger LOG = LoggerFactory.getLogger(Connection.class);

    private Socket socket;

    private InputStream inputStream;

    private OutputStream outputStream;

    public Connection() {
        socket = new Socket();
        OKPacket okPacket = null;

        //三次握手建立连接
        ConnectionUtil.threeHands(socket, ConnectionManager.getHost(), ConnectionManager.getPort());

        //处理handshake
        try {
            this.inputStream = socket.getInputStream();
        } catch(Exception e) {
            LOG.debug("get socket inputstream failed", e);
        }

        HandshakePacket handshakePacket = ConnectionUtil.processHandShake(this.inputStream);

        //发送Auth Packet

        try {
            this.outputStream = socket.getOutputStream();
        } catch (Exception e) {
            LOG.debug("get socket outputstream failed", e);
        }

        ConnectionUtil.sendAuthPacket(handshakePacket, ConnectionManager.getUser(), ConnectionManager.getPasswd(), ConnectionManager.getDb(), outputStream);
        //解析OK packet包
        okPacket = ConnectionUtil.processOKPacket(inputStream);
        if(okPacket.header!=0x00) {
            LOG.debug("服务端对认证包验证后，返回的OK包中标志位不为0x00");
        }

        //发送query set names utf8的命令
        ConnectionUtil.sendQueryPacket(outputStream,"SET NAMES utf8");
        okPacket = ConnectionUtil.processOKPacket(inputStream);
        if(okPacket.header!=0x00) {
            LOG.debug("服务端对认证包验证后，返回的OK包中标志位不为0x00");
        }

        //发送SET autocommit=0的命令
        ConnectionUtil.sendQueryPacket(outputStream,"SET autocommit=0");
        //解析OK packet包
        okPacket = ConnectionUtil.processOKPacket(inputStream);
        if(okPacket.header!=0x00) {
            LOG.debug("服务端对认证包验证后，返回的OK包中标志位不为0x00");
        }

    }

    public void Query(String SQL) {
        //发送 query packet包
        ConnectionUtil.sendQueryPacket(this.outputStream, SQL);
        //解析result
        ConnectionUtil.processResult(this.inputStream);
    }

    // 解析为这种结构
    public List<Map<String, String>> execQueryRaw(String SQL) {
        //发送 query packet包
        ConnectionUtil.sendQueryPacket(this.outputStream, SQL);
        return ConnectionUtil.processResult2(this.inputStream);
    }

    public List<Map<String, Object>> execQuery(String SQL) {
        //发送 query packet包
        ConnectionUtil.sendQueryPacket(this.outputStream, SQL);
        return ConnectionUtil.processResult3(this.inputStream);
    }




}