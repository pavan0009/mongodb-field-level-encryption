package com.micro.secureapp.secure;

import java.util.function.Function;

import org.bson.BSONCallback;
import org.bson.BSONObject;
import org.bson.BasicBSONCallback;
import org.bson.BasicBSONDecoder;
import org.bson.BasicBSONEncoder;
import org.bson.BasicBSONObject;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;

import com.micro.secureapp.crypt.CryptOperationException;
import com.micro.secureapp.crypt.CryptVault;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class AbstractEncryptionEventListener<T> extends AbstractMongoEventListener {
    protected CryptVault cryptVault;
    private boolean silentDecryptionFailure = false;

    public AbstractEncryptionEventListener(CryptVault cryptVault) {
        this.cryptVault = cryptVault;
    }

    public T withSilentDecryptionFailure(boolean silentDecryptionFailure) {
        this.silentDecryptionFailure = silentDecryptionFailure;
        return (T) this;
    }

    class Decoder extends BasicBSONDecoder implements Function<Object, Object> {
        public Object apply(Object o) {
            byte[] data;

            if (o instanceof Binary) data = ((Binary) o).getData();
            else if (o instanceof byte[]) data = (byte[]) o;
            else throw new IllegalStateException("Got " + o.getClass() + ", expected: Binary or byte[]");

            try {
                byte[] serialized = cryptVault.decrypt((data));
                BSONCallback bsonCallback = new BasicDBObjectCallback();
                decode(serialized, bsonCallback);
                BSONObject deserialized = (BSONObject) bsonCallback.get();
                return deserialized.get("");
            } catch (CryptOperationException e) {
                if (silentDecryptionFailure) return null;
                throw e;
            }
        }
    }

    /**
     * BasicBSONEncoder returns BasicBSONObject which makes mongotemplate converter choke :(
     */
    class BasicDBObjectCallback extends BasicBSONCallback {
        @Override
        public BSONObject create() {
            return new BasicDBObject();
        }

        @Override
        protected BSONObject createList() {
            return new BasicDBList();
        }

        @Override
        public BSONCallback createBSONCallback() {
            return new BasicDBObjectCallback();
        }
    }

    class Encoder extends BasicBSONEncoder implements Function<Object, Object> {
        public Object apply(Object o) {
            // FIXME: switch to BsonDocumentWriter
            // we need to put even BSONObject and BSONList in a wrapping object before serialization, otherwise the type information is not encoded.
            // this is not particularly effective, however, it is the same that mongo driver itself uses on the wire, so it has 100% compatibility w.r.t de/serialization
            byte[] serialized = encode(new BasicBSONObject("", o));
            return new Binary(cryptVault.encrypt(serialized));
        }
    }
}
