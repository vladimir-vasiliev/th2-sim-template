package com.exactpro.th2.simulator.template.rule;

import static com.exactpro.th2.simulator.util.MessageUtils.copyFields;
import static com.exactpro.th2.simulator.util.MessageUtils.putFields;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.exactpro.evolution.api.phase_1.Message;
import com.exactpro.evolution.api.phase_1.Metadata;
import com.exactpro.evolution.api.phase_1.Value;
import com.exactpro.th2.simulator.rule.impl.MessageCompareRule;

public class FIXRule extends MessageCompareRule {

    AtomicInteger orderId = new AtomicInteger(0);
    AtomicInteger execId = new AtomicInteger(0);

    public FIXRule(@Nullable Map<String, Value> fieldsValue) {
        super("NewOrderSingle", fieldsValue);
    }

    @NotNull
    @Override
    public List<Message> handleTriggered(@NotNull Message message) {
        return Collections.singletonList(
                copyFields(
                        putFields(Message.newBuilder(),
                                "OrderID", orderId.incrementAndGet(),
                                "ExecID", execId.incrementAndGet(),
                                "ExecType", "2",
                                "OrdStatus", "0",
                                "CumQty", "0",
                                "TradingParty", null,
                                "TransactTime", LocalDateTime.now().toString()),
                        message,
                        "Side",
                        "LeavesQty",
                        "ClOrdID",
                        "SecurityID",
                        "SecurityIDSource",
                        "OrdType",
                        "OrderQty")
                        .setMetadata(Metadata
                                .newBuilder()
                                .setMessageType("ExecutionReport")
                                .build())
                        .build()
        );
    }


}
