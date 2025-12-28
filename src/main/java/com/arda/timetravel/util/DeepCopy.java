package com.arda.timetravel.util;

import java.io.*;

public final class DeepCopy {

    private DeepCopy() {}

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T copy(T object) {
        if (object == null) return null;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
                out.writeObject(object);
            }

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            try (ObjectInputStream in = new ObjectInputStream(bis)) {
                return (T) in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Deep copy failed. Ensure state is Serializable.", e);
        }
    }
}
