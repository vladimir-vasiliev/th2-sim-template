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

package com.exactpro.th2.simulator.template.service;

import org.jetbrains.annotations.NotNull;

import com.exactpro.th2.simulator.ISimulator;
import com.exactpro.th2.simulator.ISimulatorPart;
import com.exactpro.th2.simulator.grpc.RuleID;
import com.exactpro.th2.simulator.template.grpc.TemplateFixCreate;
import com.exactpro.th2.simulator.template.grpc.TemplateSimulatorServiceGrpc;
import com.exactpro.th2.simulator.template.rule.FIXRule;

import io.grpc.stub.StreamObserver;

public class TemplateService extends TemplateSimulatorServiceGrpc.TemplateSimulatorServiceImplBase implements ISimulatorPart {

    private ISimulator simulator;

    @Override
    public void init(@NotNull ISimulator simulator) {
        this.simulator = simulator;
    }

    @Override
    public void createRuleFIX(TemplateFixCreate request, StreamObserver<RuleID> responseObserver) {
        FIXRule rule = new FIXRule(request.getFieldsMap());
        RuleID ruleId = simulator.addRule(rule, request.getConnectionId());
        responseObserver.onNext(ruleId);
        responseObserver.onCompleted();
    }
}
