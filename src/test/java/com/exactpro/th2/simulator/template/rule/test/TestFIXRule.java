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

package com.exactpro.th2.simulator.template.rule.test;

import static com.exactpro.th2.simulator.util.ValueUtils.getValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.exactpro.th2.infra.grpc.Message;
import com.exactpro.th2.infra.grpc.Message.Builder;
import com.exactpro.th2.infra.grpc.MessageMetadata;
import com.exactpro.th2.simulator.rule.IRule;
import com.exactpro.th2.simulator.rule.test.AbstractRuleTest;
import com.exactpro.th2.simulator.template.rule.FIXRule;
import com.exactpro.th2.simulator.util.MessageUtils;

public class TestFIXRule extends AbstractRuleTest {
    @NotNull
    @Override
    protected Message createMessage(int i, @NotNull Builder builder) {
        return (i % 4 == 0 ?
                MessageUtils.putField(builder, "ClOrdId", "order_id_2") :
                MessageUtils.putFields(builder, "ClOrdId", "order_id_1", "1", "1", "2", "2"))
                .setMetadata(MessageMetadata.newBuilder().setMessageType("NewOrderSingle").build()).build();
    }

    @Override
    protected int getCountMessages() {
        return 1000;
    }

    @NotNull
    @Override
    protected List<IRule> createRules() {
        List<IRule> rules = new ArrayList<>();
        rules.add(new FIXRule(Collections.singletonMap("ClOrdId", getValue("order_id_1"))));
        rules.add(new FIXRule(Collections.singletonMap("ClOrdId", getValue("order_id_2"))));
        rules.add(new FIXRule(Collections.singletonMap("ClOrdId", getValue("order_id_3"))));
        rules.add(new FIXRule(Collections.singletonMap("ClOrdId", getValue("order_id_4"))));
        return rules;
    }
}
