/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.components.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;

import java.io.ByteArrayOutputStream;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @modified by xiangbin
 * @version V1.1
 * @Title: Kryo 序列化
 * @Description: 性能和 fst一样
 */
public class KryoSerializer implements JbootSerializer {

    private final Pool<Kryo> kryoPool = new Pool<Kryo>(true, false, 8){
        @Override
        protected Kryo create() {
            return new Kryo();
        }
    };

    @Override
    public byte[] serialize(Object obj) {
        if (obj == null) {
            return null;
        }
        Kryo kryo = kryoPool.obtain();
        try (Output output = new Output(new ByteArrayOutputStream())) {
            kryo.writeClassAndObject(output, obj);
            return output.toBytes();
        } finally {
            kryoPool.free(kryo);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        Kryo kryo = kryoPool.obtain();
        try (ByteBufferInput input = new ByteBufferInput(bytes)) {
            return kryo.readClassAndObject(input);
        } finally {
            kryoPool.free(kryo);
        }
    }
}
