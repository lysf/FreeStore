package com.snailgame.cjg.common.model;

import com.snailgame.cjg.util.SaveUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Created by yftx on 2/24/16.
 */
public class PersistentVar implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    private static PersistentVar instance;

    private SystemConfig systemConfig;

    private PersistentVar() {

    }

    public static PersistentVar getInstance() {
        if (instance == null) {
            Object object = SaveUtils.restoreObject();
            if (object == null) {
                object = new PersistentVar();
                SaveUtils.saveObject(object);
            }

            instance = (PersistentVar) object;
        }

        if (instance.getSystemConfig() == null) {
            instance.setSystemConfig(new SystemConfig());
        }
        return instance;
    }

    public SystemConfig getSystemConfig() {
        return systemConfig;
    }

    public void setSystemConfig(SystemConfig systemConfig) {
        this.systemConfig = systemConfig;
        SaveUtils.saveObject(this);
    }


    // —————以下3个方法用于序列化————————
    public PersistentVar readResolve() throws ObjectStreamException, CloneNotSupportedException {
        instance = (PersistentVar) this.clone();
        return instance;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
    }

    public Object Clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * 每次启动时需要重置全局变量
     */
    public void reset() {
        systemConfig = null;
        SaveUtils.saveObject(this);
    }
}
