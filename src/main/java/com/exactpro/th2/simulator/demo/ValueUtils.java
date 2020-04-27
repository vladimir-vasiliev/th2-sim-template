package com.exactpro.th2.simulator.demo;

import com.exactpro.evolution.api.phase_1.ListValue;
import com.exactpro.evolution.api.phase_1.ListValue.Builder;
import com.exactpro.evolution.api.phase_1.Message;
import com.exactpro.evolution.api.phase_1.NullValue;
import com.exactpro.evolution.api.phase_1.Value;

//TODO: move to th2-simulator
public class ValueUtils {

    public static Value nullValue() {
        return Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build();
    }

    public static Value getValue(Object value) {
        if (value == null) {
            return nullValue();
        }

        if (value instanceof String) {
            return Value.newBuilder().setSimpleValue((String)value).build();
        }

        if (value instanceof Message) {
            return Value.newBuilder().setMessageValue((Message)value).build();
        }

        if (value instanceof Iterable<?>) {
            return Value.newBuilder().setListValue(getListValue((Iterable<?>)value)).build();
        }

        return Value.newBuilder().setSimpleValue(value.toString()).build();
    }

    public static ListValue getListValue(Iterable<?> value) {
        Builder result = ListValue.newBuilder();
        for (Object obj : value) {
            result.addValues(getValue(obj));
        }
        return result.build();
    }

}
