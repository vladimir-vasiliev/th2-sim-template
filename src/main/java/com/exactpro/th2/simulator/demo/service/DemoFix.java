package com.exactpro.th2.simulator.demo.service;

import org.jetbrains.annotations.NotNull;

import com.exactpro.th2.simulator.DemoFixCreate;
import com.exactpro.th2.simulator.DemoFixSimulatorGrpc;
import com.exactpro.th2.simulator.ISimulator;
import com.exactpro.th2.simulator.ISimulatorPart;
import com.exactpro.th2.simulator.RuleID;
import com.exactpro.th2.simulator.demo.rule.FIXRule;

import io.grpc.stub.StreamObserver;

public class DemoFix extends DemoFixSimulatorGrpc.DemoFixSimulatorImplBase implements ISimulatorPart {

    private ISimulator simulator;

    @Override
    public void init(@NotNull ISimulator simulator) {
        this.simulator = simulator;
    }

    @Override
    public void createRuleFIX(DemoFixCreate request, StreamObserver<RuleID> responseObserver) {
        responseObserver.onNext(simulator.addRule(new FIXRule(request.getFieldsMap())));
        responseObserver.onCompleted();
    }
}
