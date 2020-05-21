/*******************************************************************************
 * Copyright 2020 Exactpro (Exactpro Systems Limited)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

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

import com.exactpro.th2.infra.grpc.Message;
import com.exactpro.th2.infra.grpc.MessageMetadata;
import com.exactpro.th2.infra.grpc.Value;
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
                        .setMetadata(MessageMetadata
                                .newBuilder()
                                .setMessageType("ExecutionReport")
                                .build())
                        .build()
        );
    }


}
