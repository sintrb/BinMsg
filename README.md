BinMsg
======

Java的二进制消息序列化（反序列化）类库


例子：
如实现Modbus的03功能数据帧的解析、反解析，
请求：
```
| 1byte地址 | 1byte功能码(0x03) | 2byte起始地址(大端) | 2byte寄存器数量(大端) |
```
响应：
```
| 1byte地址 | 1byte功能码(0x03) | 1byte字节数2*N | 2*Nbyte寄存器值(大端) |
```

消息定义和注解如下：
请求：
```java
@MsgBody
public class MB03Req {
	@MsgField(offset = 0, type = FieldType.BYTE)
	public byte addr;
	@MsgField(offset = 1, type = FieldType.BYTE)
	public byte func = 0x03; // 请求码
	@MsgField(offset = 2, type = FieldType.BIG16)
	public short start;
	@MsgField(offset = 4, type = FieldType.BIG16)
	public short count;
}
```

响应：
```java
@MsgBody
public class MB03Res {
	@MsgField(offset = 0, type = FieldType.BYTE)
	public byte addr;
	@MsgField(offset = 1, type = FieldType.BYTE)
	public byte func;
	@MsgField(offset = 2, type = FieldType.BYTE)
	public int size;
	@MsgField(offset = 3, type = FieldType.BIG16S, sizedepend = "size", sizeunit = 2)
	public short[] values;
}
```

使用：
```
// 请求
MB03Req req = new MB03Req();
req.addr = 0x01;
req.func = 0x03;
req.start = 10;
req.count = 2;
byte[] dats = BinMsg.msgToBin(req);
// 接下来根据dats计算CRC16
printByteArray(dats); // 打印二进制主机

// 响应
// 返回两个寄存器值0x1234 0x5678，末尾两个CRC16值忽略
byte[] rebts = new byte[] { 0x01, 0x03, 4, 0x12, 0x34, 0x56, 0x78, 0, 0 };
MB03Res res = BinMsg.binToMsg(rebts, MB03Res.class);
for (short v : res.values) {
	System.out.print(String.format("%04x ", v));
}
System.out.println();
```

输出结果：
```
01 03 00 0a 00 02 
1234 5678 

```

### 说明：
@MsgBody 将消息对象进行注解，参数type为消息字段布局方式：
	LayoutType.Offset 表示偏移方式，在二进制化的时候将字段值放置到字段注解的offset位置；反二进制化的时候从offset位置取出值。
	LayoutType.Order  表示排序方式，在二进制化之前先对所有的字段安装注解的offset值进行排序（值小的放在前），然后按顺序对值进行二进制化；反二进制化的时候也是先排序，然后按顺序从二进制数据中获取值。



