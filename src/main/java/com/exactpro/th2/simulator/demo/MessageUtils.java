package com.exactpro.th2.simulator.demo;

import java.util.Map;
import java.util.Map.Entry;

import org.jetbrains.annotations.NotNull;

import com.exactpro.evolution.api.phase_1.Message;
import com.exactpro.evolution.api.phase_1.NullValue;
import com.exactpro.evolution.api.phase_1.Value;

//TODO: move to th2-simulator
public class MessageUtils {


    public static Value nullValue() {
        return Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build();
    }

    public static Message.Builder putField(@NotNull Message.Builder builder, @NotNull String key, Object value) {
        return builder.putFields(key, ValueUtils.getValue(value));
    }

    public static Message.Builder putFields(@NotNull Message.Builder builder, @NotNull Map<String, Object> fields) {
        for (Entry<String, Object> entry :  fields.entrySet()) {
            putField(builder, entry.getKey(), entry.getValue());
        }
        return builder;
    }

    public static Message.Builder putFields(@NotNull Message.Builder builder, Object... obj) {
        for (int i = 0; i < obj.length - 1; i += 2) {
            if (obj[i] instanceof String) {
                putField(builder, (String) obj[i], obj[i + 1]);
            }
        }
        return builder;
    }

    public static Message.Builder copyField(@NotNull Message.Builder builder, @NotNull String key, @NotNull Message message) {
        Value value = message.getFieldsOrDefault(key, null);
        if (value != null) {
            builder.putFields(key, value);
        }
        return builder;
    }

    public static Message.Builder copyField(@NotNull Message.Builder builder, @NotNull String key, @NotNull Message.Builder message) {
        Value value = message.getFieldsOrDefault(key, null);
        if (value != null) {
            builder.putFields(key, value);
        }
        return builder;
    }

    public static Message.Builder copyFields(@NotNull Message.Builder builder, @NotNull Iterable<String> keys, @NotNull Message message) {
        for (String key : keys) {
            copyField(builder, key, message);
        }
        return builder;
    }

    public static Message.Builder copyFields(@NotNull Message.Builder builder, @NotNull Message message, String... keys) {
        for (String key : keys) {
            copyField(builder, key, message);
        }
        return builder;
    }

    public static Message.Builder copyFields(@NotNull Message.Builder builder, @NotNull Iterable<String> keys, @NotNull Message.Builder message) {
        for (String key : keys) {
            copyField(builder, key, message);
        }
        return builder;
    }

    public static Message.Builder copyFields(@NotNull Message.Builder builder, @NotNull Message.Builder message, String... keys) {
        for (String key : keys) {
            copyField(builder, key, message);
        }
        return builder;
    }

}
