package com.exactpro.th2.simulator.template.service;

import org.jetbrains.annotations.NotNull;

import com.exactpro.th2.simulator.ISimulator;
import com.exactpro.th2.simulator.ISimulatorPart;
import com.exactpro.th2.simulator.RuleID;
import com.exactpro.th2.simulator.template.TemplateFixCreate;
import com.exactpro.th2.simulator.template.TemplateSimulatorServiceGrpc;
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
        RuleID ruleId = simulator.addRule(rule, request.getConnectivityId());
        responseObserver.onNext(ruleId);
        responseObserver.onCompleted();
    }
}
