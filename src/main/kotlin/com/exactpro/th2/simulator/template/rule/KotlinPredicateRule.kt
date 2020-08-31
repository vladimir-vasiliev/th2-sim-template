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

import com.exactpro.th2.common.value.getBigDecimal
import com.exactpro.th2.common.value.getInt
import com.exactpro.th2.common.value.getString
import com.exactpro.th2.infra.grpc.Message
import com.exactpro.th2.simulator.rule.impl.MessagePredicateRule
import java.math.BigDecimal
import java.util.Collections
import java.util.function.Predicate

class KotlinPredicateRule : MessagePredicateRule() {
    init {
        init({mt -> mt == "NewOrderSingle" },
            mapOf(
                "field" to Predicate { value -> value.getInt()!! > 0},
                "field2" to Predicate { value -> value.getString()?.matches(Regex("A*")) ?: false},
                "field3" to Predicate {value -> value.getBigDecimal()?.compareTo(BigDecimal(12))!! >= 0 }
            ))
    }

    override fun handleTriggered(p0: Message): MutableList<Message> {
        return Collections.emptyList()
    }
}