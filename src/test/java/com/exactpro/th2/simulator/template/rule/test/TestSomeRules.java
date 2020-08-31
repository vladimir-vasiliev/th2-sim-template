/*******************************************************************************
 * Copyright 2020-2020 Exactpro (Exactpro Systems Limited)
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

package com.exactpro.th2.simulator.template.rule.test;

import static com.exactpro.th2.common.message.MessageUtilsKt.addField;
import static com.exactpro.th2.common.value.ValueUtilsKt.toValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.exactpro.th2.infra.grpc.Message;
import com.exactpro.th2.infra.grpc.Message.Builder;
import com.exactpro.th2.infra.grpc.MessageMetadata;
import com.exactpro.th2.infra.grpc.Value;
import com.exactpro.th2.simulator.rule.IRule;
import com.exactpro.th2.simulator.rule.test.AbstractRuleTest;
import com.exactpro.th2.simulator.template.rule.FIXRule;

public class TestSomeRules extends AbstractRuleTest {

    @Override
    protected int getCountMessages() {
        return 1000;
    }

    @NotNull
    @Override
    protected Message createMessage(int i, @NotNull Builder builder) {
        return (i % 2 == 0
                ? addField(builder, "ClOrdId", "ord_1")
                : addField(builder, "ClOrdId", "ord_2"))
                .setMetadata(MessageMetadata.newBuilder().setMessageType("NewOrderSingle").build())
                .build();
    }

    @NotNull
    @Override
    protected List<IRule> createRules() {
        List<IRule> list = new ArrayList<>();

        var arguments1 = new HashMap<String, Value>();
        arguments1.put("ClOrdId", toValue("ord_1"));
        list.add(new FIXRule(arguments1));

        var arguments2 = new HashMap<String, Value>();
        arguments2.put("ClOrdId", toValue("ord_2"));
        list.add(new FIXRule(arguments2));

        return list;
    }

    @Override
    protected boolean checkResultMessages(int index, List<Message> messages) {
        return messages.size() == 1
                && messages.get(0).getMetadata().getMessageType().equals("ExecutionReport");
    }
}
