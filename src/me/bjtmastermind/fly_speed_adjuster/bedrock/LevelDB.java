package me.bjtmastermind.fly_speed_adjuster.bedrock;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

public class LevelDB {
    private DB db;
    private DBIterator iterator;

    public LevelDB(String db) {
        DBFactory factory = new Iq80DBFactory();
        Options options = new Options();

        try {
            this.db = factory.open(new File(db), options);
            this.iterator = this.db.iterator();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] get(byte[] key) {
        return db.get(key);
    }

    public void put(byte[] key, byte[] value) {
        db.put(key, value);
    }

    public void delete(byte[] key) {
        db.delete(key);
    }

    public void update(byte[] key, byte[] value) {
        delete(key);
        put(key, value);
    }

    public byte[] getNextRegex(String regex) {
        while (this.iterator.hasNext()) {
            Entry<byte[], byte[]> entry = this.iterator.next();
            if (new String(entry.getKey()).matches(regex)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public DBIterator iterator() {
        return this.iterator;
    }

    public void close() {
        try {
            this.db.close();
        } catch (IOException e) {}
    }
}
