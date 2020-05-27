# MySQL binary protocol research
## protocol
MySQL二进制协议是客户端连接MySQL服务时，进行的握手交互、命令交互、查询状态交互、进程信息交互等一系列请求和响应报文格式。研究MySQL二进制协议对开发MySQL驱动、数据库中间件意义重大，本文在前辈研究成果的基础上稍作修改，并提出后续改进建议。前辈的MySQL二进制协议为:[github地址](https://github.com/CallMeJiaGu/MySQL-Protocol)，研究MySQL二进制协议需要具备的工具为:
+ MySQL数据库服务
+ WireShark抓包工具
+ Idea或eclipse集成开发环境  

同时需要具体以下知识储备:
+ 网络各层协议格式和tcp交互报文，以便正确理解wireshark展示的交互报文
+ Java NIO部分, 缓冲区用于解析和生成MySQL协议报文
+ MySQL相关知识储备, 有问题是可以结合储备的知识进行理解
+ C语言、C++的数据类型, 以便正确解析无符号字段

## 测试和使用项目
本文提供的example类在example包中， 可以参考

## 改进和思考
本项目将原协议工具封装了Connection和初始化的ConnectionManage，并在其中提供了查询、插入等接口。
### 开源项目需要考虑一下几点
+ 代码一定要符合规范，让别人看的时候清晰易懂，后续真正加入开源社区不需要重新整理
+ 项目正常情况下使用日志框架打印日志，而不是使用控制台方法
+ 没有说明的情况下，需要提供example以便快速找到入口

### 本项目的思考
+ 使用IO阻塞模型，高并发情况下性能不佳，后续可替换为NIO
+ 使用NIO模型后，可参考netty设计将握手(或者一定设置)后的SocketChannel丢到和一个线程绑定的多路选择器中进行IO，整体上是线程组维护的连接池。
+ Select查询后的结果，可以和指定类进行绑定(ORM功能)，此功能需可扩展， 绑定方法可以使用fastjson或者protobuf进行
未完，待继续开发
