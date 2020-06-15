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

package com.exactpro.th2.simulator.template.rule

import com.exactpro.th2.common.message.addField
import com.exactpro.th2.common.message.copyField
import com.exactpro.th2.common.message.setMetadata
import com.exactpro.th2.infra.grpc.Direction
import com.exactpro.th2.infra.grpc.Message
import com.exactpro.th2.infra.grpc.Value
import com.exactpro.th2.simulator.rule.impl.MessageCompareRule
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

class KotlinFIXRule(field: Map<String, Value>) : MessageCompareRule() {

    private var orderId = AtomicInteger(0)
    private var execId = AtomicInteger(0)

    init {
        init("NewOrderSingle", field)
    }

    override fun handleTriggered(incomeMessage: Message): MutableList<Message> =
        arrayListOf(
            Message.newBuilder().apply {
                addField("OrderID", orderId.incrementAndGet())
                addField("ExecID", execId.incrementAndGet())
                addField("ExecType", "2")
                addField("OrdStatus", "0")
                addField("CumQty", "0")
                addField("TradingParty", null)
                addField("TransactTime", LocalDateTime.now().toString())
                copyField(incomeMessage, "Side", "LeavesQty", "ClOrdID", "SecurityID", "SecurityIDSource", "OrdType", "OrderQty")
                setMetadata("ExecutionReport", Direction.FIRST, "sessionAlias")
                //setMessageType("ExecutionReport")
            }.build()
        )
}