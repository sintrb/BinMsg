package com.sin.java.comm.binmsg;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.sin.java.comm.binmsg.MsgField.FieldType;
import com.sin.java.comm.binmsg.stream.SeekableBuffedOutputStream;
import com.sin.java.comm.binmsg.stream.SeekableOutputStream;

/**
 * Java的二进制消息序列化（反序列化）类库 <br/>
 * 
 * @author RobinTang
 * @date 2014-12-26
 * @see https://github.com/sintrb/BinMsg/
 */
final public class BinMsg {

	public static final Class<?> TYPE_BYTES = (new byte[0]).getClass();
	public static final Class<?> TYPE_SHORTS = (new short[0]).getClass();
	public static final Class<?> TYPE_INTS = (new int[0]).getClass();
	public static final Class<?> TYPE_BYTE = byte.class;
	public static final Class<?> TYPE_SHORT = short.class;
	public static final Class<?> TYPE_INT = int.class;

	/**
	 * 获取对象字段长度（字节数目）
	 * 
	 * @param obj
	 *            对象
	 * @param afd
	 *            字段注解
	 * @return 长度
	 */
	private static int getFieldSize(Object obj, MsgField afd) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Class<?> clz = obj.getClass();
		if ("".equals(afd.sizedepend())) {
			// 不依赖其它字段
			return afd.size();
		} else {
			// 从依赖字段获取长度
			Field dep = clz.getField(afd.sizedepend());
			long size = dep.getLong(obj);
			return (int) size;
		}
	}

	private static void writeValueToSream(OutputStream os, int val, FieldType type) throws IOException {
		if (type == FieldType.BYTE || type == FieldType.BYTES) {
			os.write(0x00ff & val);
		} else if (type == FieldType.BIG16 || type == FieldType.BIG16S) {
			os.write(0x00ff & (val >> 8));
			os.write(0x00ff & val);
		} else if (type == FieldType.LIT16 || type == FieldType.LIT16S) {
			os.write(0x00ff & val);
			os.write(0x00ff & (val >> 8));
		} else
			throw new IllegalArgumentException("unsupport type " + type);
	}

	public static void writeToStream(SeekableOutputStream sos, Object obj) throws IOException, IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException {
		Class<?> clz = obj.getClass();
		if (clz.isAnnotationPresent(MsgBody.class) == false)
			throw new IllegalArgumentException(String.format("%s not a MsgBody Annotation class", clz));

		int offset = sos.getPosition();
		for (Field f : clz.getFields()) {
			MsgField afd = f.getAnnotation(MsgField.class);
			if (afd != null) {
				sos.seek(afd.offset() + offset);
				FieldType type = afd.type();
				switch (type) {
				case BYTE:
				case BIG16:
				case LIT16: {
					writeValueToSream(sos, f.getInt(obj), afd.type());
					break;
				}
				case BYTES:
				case BIG16S:
				case LIT16S: {
					int len = getFieldSize(obj, afd) / afd.sizeunit();
					if (f.getType() == TYPE_BYTES) {
						// byte[]
						byte[] dts = (byte[]) f.get(obj);
						if (len == 0 && dts != null)
							len = dts.length;
						for (int i = 0; i < len; ++i) {
							writeValueToSream(sos, dts[i], type);
						}
					} else if (f.getType() == TYPE_SHORTS) {
						// short[]
						short[] dts = (short[]) f.get(obj);
						if (len == 0 && dts != null)
							len = dts.length;
						for (int i = 0; i < len; ++i) {
							writeValueToSream(sos, dts[i], type);
						}
					} else if (f.getType() == TYPE_INTS) {
						// int[]
						int[] dts = (int[]) f.get(obj);
						if (len == 0 && dts != null)
							len = dts.length;
						for (int i = 0; i < len; ++i) {
							writeValueToSream(sos, dts[i], type);
						}
					} else
						throw new IllegalArgumentException("must use int[]/short[]/byte[] for 16bit[] field.");
					break;
				}
				default:
					throw new IllegalArgumentException("unsupport type " + afd.type());
				}
			}
		}
	}

	/**
	 * 将消息序列号为二进制数据
	 * 
	 * @param obj
	 *            需要序列化的消息对象
	 * @return 该消息的二进制数据
	 */
	public static byte[] msgToBin(Object obj) throws IOException, IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException {
		SeekableBuffedOutputStream sbos = new SeekableBuffedOutputStream();
		writeToStream(sbos, obj);
		byte[] dats = sbos.toByteArray();
		sbos.close();
		return dats;
	}

	/**
	 * 从缓冲区读取值
	 * 
	 * @param data
	 *            缓冲区
	 * @param offset
	 *            偏移
	 * @param index
	 *            第index个type类型值
	 * @param type
	 *            值类型
	 * @return 值
	 */
	private static int readValFromBuffer(byte[] data, int offset, int index, FieldType type) {
		if (type == FieldType.BYTE || type == FieldType.BYTES) {
			return data[offset + index] & 0x00ff;
		} else if (type == FieldType.BIG16 || type == FieldType.BIG16S) {
			return ((data[offset + index * 2] & 0x00ff) << 8) | (data[offset + index * 2 + 1] & 0x00ff);
		} else if (type == FieldType.LIT16 || type == FieldType.LIT16S) {
			return ((data[offset + index * 2 + 1] & 0x00ff) << 8) | (data[offset + index * 2] & 0x00ff);
		} else
			throw new IllegalArgumentException("unsupport type " + type);
	}

	/**
	 * 反序列化数组字段
	 * 
	 * @param data
	 *            二进制数据
	 * @param offset
	 *            偏移
	 * @param obj
	 *            对象
	 * @param f
	 *            字段
	 */
	private static <T> void deArrayField(byte[] data, int offset, T obj, Field f) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		MsgField afd = f.getAnnotation(MsgField.class);
		int size = afd.size();
		if (size == 0) {
			size = getFieldSize(obj, afd);
		}
		int count = size / afd.sizeunit();
		offset += afd.offset();
		byte[] bts = null;
		short[] sts = null;
		int[] its = null;

		if (f.getType() == TYPE_BYTES) {
			bts = new byte[count];
			f.set(obj, bts);
		} else if (f.getType() == TYPE_SHORTS) {
			sts = new short[count];
			f.set(obj, sts);
		} else if (f.getType() == TYPE_INTS) {
			its = new int[count];
			f.set(obj, its);
		} else
			throw new IllegalArgumentException("must use int[]/short[]/byte[] for array field: " + f.getName());
		FieldType type = afd.type();
		for (int i = 0; i < count; ++i) {
			if (bts != null) {
				bts[i] = (byte) readValFromBuffer(data, offset, i, type);
			} else if (sts != null) {
				sts[i] = (short) readValFromBuffer(data, offset, i, type);
			} else if (its != null) {
				its[i] = (int) readValFromBuffer(data, offset, i, type);
			}
		}
	}

	/**
	 * 反序列化对象
	 * 
	 * @param data
	 *            二进制数据
	 * @param offset
	 *            偏移
	 * @param clz
	 *            对象类型
	 * @return clz类型的对象
	 */
	public static <T> T binToMsg(byte[] data, int offset, Class<T> clz) throws InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		if (clz.isAnnotationPresent(MsgBody.class) == false)
			throw new IllegalArgumentException(String.format("%s not a MsgBody Annotation class", clz));

		T obj = clz.newInstance();
		int datalen = data.length - offset;
		List<Field> arrfields = new ArrayList<Field>();
		for (Field f : obj.getClass().getFields()) {
			MsgField afd = f.getAnnotation(MsgField.class);
			if (afd != null) {
				if (afd.offset() > datalen)
					throw new IllegalArgumentException("数据不足");
				FieldType type = afd.type();
				switch (type) {
				case BYTE:
				case BIG16:
				case LIT16: {
					if (f.getType() == TYPE_BYTE)
						f.setByte(obj, (byte) readValFromBuffer(data, offset + afd.offset(), 0, type));
					else if (f.getType() == TYPE_SHORT)
						f.setShort(obj, (short) readValFromBuffer(data, offset + afd.offset(), 0, type));
					else if (f.getType() == TYPE_INT)
						f.setInt(obj, readValFromBuffer(data, offset + afd.offset(), 0, type));
					else
						throw new IllegalArgumentException("must use int/short/byte for single field: " + f.getName());
					break;
				}
				case BYTES:
				case BIG16S:
				case LIT16S: {
					arrfields.add(f);
					break;
				}
				default:
					throw new IllegalArgumentException("unsupport type " + afd.type());
				}
			}
		}
		for (Field f : arrfields) {
			deArrayField(data, offset, obj, f);
		}
		return obj;
	}

	/**
	 * 反序列化对象
	 * 
	 * @param data
	 *            二进制数据
	 * @param clz
	 *            对象类型
	 * @return clz类型的对象
	 */
	public static <T> T binToMsg(byte[] data, Class<T> clz) throws InstantiationException, IllegalAccessException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		return binToMsg(data, 0, clz);
	}
}
