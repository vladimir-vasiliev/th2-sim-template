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

package com.exactpro.th2.simulator.template.rule

import com.exactpro.th2.common.grpc.Message
import com.exactpro.th2.common.grpc.Value
import com.exactpro.th2.common.message.*
import com.exactpro.th2.common.value.getMessage
import com.exactpro.th2.common.value.getString
import com.exactpro.th2.sim.rule.impl.MessageCompareRule

import java.util.concurrent.atomic.AtomicInteger

class KotlinFIXRule(field: Map<String, Value>) : MessageCompareRule() {

    private var orderId = AtomicInteger(0)
    private var execId = AtomicInteger(0)

    init {
        init("NewOrderSingle", field)
    }
    //.getString("Side")?.isBlank() == true){  //null
    override fun handleTriggered(incomeMessage: Message): MutableList<Message> {
        val result = ArrayList<Message>()
        if (!incomeMessage.containsFields("Side")) {
                    val rej = message("Reject").addFields(
                            "RefTagID", "453",
                            "RefMsgType", "D",
//                            "RefSeqNum", incomeMessage.getField("BeginString")?.getMessage()?.getField("MsgSeqNum"),
                            "Text", "Simulating reject message",
                            "SessionRejectReason", "1"
                    )
                    result.add(rej.build())
                }
        else {
            when (incomeMessage.getString("Side")) {
                "1" -> {val FixER1 = message("ExecutionReport")
                    .copyFields(incomeMessage,  // fields from NewOrder
                            "Side",
                            "ClOrdID",
                            "SecurityID",
                            "SecurityIDSource",
                            "OrdType",
                            "OrderQty",
                            "TradingParty",
                            "TimeInForce"
                    )
                    .addFields(
                            "OrderID", orderId.incrementAndGet(),
                            "ExecID", execId.incrementAndGet(),
                            "LeavesQty", incomeMessage.getField("OrderQty")!!.getString(),
                            "Text", "This is simulated Execution Report for Buy Side"
                    )
                    result.add(FixER1.build())
                }
                "2" -> {val FixER1 = message("ExecutionReport")
                        .copyFields(incomeMessage,
                            "Side",
                                "ClOrdID",
                                "SecurityID",
                                "SecurityIDSource",
                                "OrdType",
                                "OrderQty",
                                "TradingParty",
                                "TimeInForce"
                        )
                        .addFields(
                                "OrderID", orderId.incrementAndGet(),
                                "ExecID", execId.incrementAndGet(),
                                "LeavesQty", incomeMessage.getField("OrderQty")!!.getString(),
                                "Text", "This is simulated Execution Report for Sell Side"
                    )
                    result.add(FixER1.build())
                }
            }
        }
        return result;
    }
}




