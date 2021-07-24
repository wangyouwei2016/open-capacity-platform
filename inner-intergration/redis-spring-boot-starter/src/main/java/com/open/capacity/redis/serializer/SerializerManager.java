package com.open.capacity.redis.serializer;

/**
 * Serializer for serialize and deserialize.
 * 
 * @author jiangping
 * @version $Id: Serializer.java, v 0.1 2015-10-4 PM9:37:57 tao Exp $
 */
public class SerializerManager {

    private static Serializer[] serializers = new Serializer[5];
    public static final byte    Hessian2    = 1;
    //public static final byte    Json        = 2;

    static {
        addSerializer(Hessian2, new HessianSerializer());
    }

    public static Serializer getSerializer(int idx) {
        return serializers[idx];
    }

    public static void addSerializer(int idx, Serializer serializer) {
        if (serializers.length <= idx) {
            Serializer[] newSerializers = new Serializer[idx + 5];
            System.arraycopy(serializers, 0, newSerializers, 0, serializers.length);
            serializers = newSerializers;
        }
        serializers[idx] = serializer;
    }
}
