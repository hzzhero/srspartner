package com.he.srs.util;

import com.he.srs.bean.vo.BaseMsg;
import com.he.srs.bean.vo.ProtoStuffActionEnum;
import io.protostuff.ByteString;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gaoweicong
 * @Description TODO
 * @CreateTime 2021/11/24 14:36
 */
@Slf4j
public final class ProtoStaffUtil {
    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private static final LinkedBuffer BUFFER = LinkedBuffer.allocate(DEFAULT_BUFFER_SIZE);

    private static final Map<Class<?>, Schema<?>> SCHEMA_CACHE = new ConcurrentHashMap<Class<?>, Schema<?>>();

    /**
     * 序列化消息
     *
     * @author gaoweicong
     * @param action 类型
     * @param data 数据
     * @param <T> BaseMsg
     * @return byte[]
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseMsg> byte[] serializeMsg(ProtoStuffActionEnum action, T data) {
        if (data == null) {
            throw new NullPointerException("the serialize data is null");
        }
        ProtoStuffMsg protoStuffMsg = new ProtoStuffMsg(action.ordinal(), data);
        return protoStuffMsg.serialize();
    }

    /**
     * 反序列化消息
     *
     * @author gaoweicong
     * @param data 数据
     * @param clazz 类
     * @param <T> BaseMsg
     * @return T
     */
    public static <T extends BaseMsg> T deserializeMsg(byte[] data, Class<T> clazz) {
        return ProtoStuffMsg.deserialize(data, clazz);
    }

    @SuppressWarnings("unchecked")
    private static synchronized <T extends BaseMsg> byte[] serialize(T data) {
        Class<T> clazz = (Class<T>) data.getClass();
        Schema<T> schema = getSchema(clazz);
        byte[] result;
        try {
            result = ProtostuffIOUtil.toByteArray(data, schema, BUFFER);
        } finally {
            BUFFER.clear();
        }
        return result;
    }

    private static <T extends BaseMsg> T deserialize(byte[] data, Class<T> clazz) {
        Schema<T> schema = getSchema(clazz);
        T result = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, result, schema);
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T extends BaseMsg> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) SCHEMA_CACHE.get(clazz);
        if (schema == null) {
            schema = RuntimeSchema.getSchema(clazz);
            if (schema != null) {
                SCHEMA_CACHE.put(clazz, schema);
            }
        }
        return schema;
    }

    @Slf4j
    @ToString
    private static final class ProtoStuffMsg<T extends BaseMsg> extends BaseMsg {

        /**
         * @see ProtoStuffActionEnum#ACTION_MQ
         * @see ProtoStuffActionEnum#ACTION_HTTP
         * @see ProtoStuffActionEnum#ACTION_FILE
         */
        private int action;

        /**
         * biz data
         */
        private ByteString bizData;

        private transient T data;

        private long bizDataLength;

        private long timestamp;

        private ProtoStuffMsg(Integer action, T data) {
            this.action = action;
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }

        private byte[] serialize() {
            if (data != null) {
                this.bizData = ByteString.copyFrom(ProtoStaffUtil.serialize(data));
                this.bizDataLength = (long) bizData.size();
            }
            log.debug("ProtoStuffMsg serialize payload: {}", this);
            return ProtoStaffUtil.serialize(this);
        }

        private static <T extends BaseMsg> T deserialize(byte[] data, Class<T> clazz) {
            if (clazz.equals(ProtoStuffMsg.class)) {
                T result = ProtoStaffUtil.deserialize(data, clazz);
                log.debug("ProtoStuffMsg deserialize result:{}", result);
                return result;
            }
            ProtoStuffMsg protoStuffMsg = ProtoStaffUtil.deserialize(data, ProtoStuffMsg.class);
            log.debug("ProtoStuffMsg deserialize result: {}", protoStuffMsg);
            if (protoStuffMsg.bizData != null) {
                T result = ProtoStaffUtil.deserialize(protoStuffMsg.bizData.toByteArray(), clazz);
                log.debug("BaseMsg deserialize result: {}", result);
                return result;
            }
            log.warn("BaseMsg is null");
            return null;
        }
    }

}
