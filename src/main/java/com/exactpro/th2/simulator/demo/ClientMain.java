package com.exactpro.th2.simulator.demo;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exactpro.evolution.ConfigurationUtils;
import com.exactpro.evolution.RabbitMqMessageSender;
import com.exactpro.evolution.RabbitMqSubscriber;
import com.exactpro.evolution.api.phase_1.ListValue;
import com.exactpro.evolution.api.phase_1.Message;
import com.exactpro.evolution.api.phase_1.Metadata;
import com.exactpro.evolution.api.phase_1.Value;
import com.exactpro.evolution.configuration.MicroserviceConfiguration;
import com.exactpro.evolution.configuration.RabbitMQConfiguration;
import com.exactpro.th2.simulator.DemoFixCreate;
import com.exactpro.th2.simulator.DemoFixSimulatorGrpc;
import com.exactpro.th2.simulator.DemoFixSimulatorGrpc.DemoFixSimulatorBlockingStub;
import com.exactpro.th2.simulator.RuleID;
import com.exactpro.th2.simulator.RuleInfo;
import com.exactpro.th2.simulator.RulesInfo;
import com.exactpro.th2.simulator.ServiceSimulatorGrpc;
import com.exactpro.th2.simulator.ServiceSimulatorGrpc.ServiceSimulatorBlockingStub;
import com.google.protobuf.Empty;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.Delivery;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ClientMain {

    private static final Logger logger = LoggerFactory.getLogger("Client");

    public static void main(String[] args) {
        MicroserviceConfiguration configuration = readConfiguration(args);

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();
        DemoFixSimulatorBlockingStub fixSimulatorBlockingStub = DemoFixSimulatorGrpc.newBlockingStub(channel);
        ServiceSimulatorBlockingStub serviceSimulatorGrps = ServiceSimulatorGrpc.newBlockingStub(channel);

        RabbitMqSubscriber subscriber = createSubscriber(configuration);
        RabbitMqMessageSender sender = new RabbitMqMessageSender(configuration.getRabbitMQ(), "fix-client", "demo_exchange", "fix_client_to_send");

        logger.warn(LocalDateTime.now(ZoneId.of("UTC")).toString());

        logger.warn("");
        //Send message without rule
        logger.warn("Send message without rule");
        sendMessage(sender, createNewOrderSingle("order_id_1"));
        waitResult(500);

        //Create rule
        logger.warn("Create rule");
        RuleID id = fixSimulatorBlockingStub.createRuleFIX(
                DemoFixCreate
                        .newBuilder()
                        .putFields("ClOrdID", Value.newBuilder().setSimpleValue("order_id_2").build())
                        .build()
        );
        logger.warn("Rule status = " + id.getId());

        //Send message with wrong field's value with enable rule
        logger.warn("Send message with wrong field's value with enable rule");
        sendMessage(sender, createNewOrderSingle("order_id_1"));
        waitResult(500);

        //Send message with enable rule
        logger.warn("Send message with enable rule");
        sendMessage(sender, createNewOrderSingle("order_id_2"));
        waitResult(500);

        //Get rules info
        logger.warn("Get rules info:");
        RulesInfo rulesInfo = serviceSimulatorGrps.getRulesInfo(Empty.newBuilder().build());
        for (RuleInfo tmp : rulesInfo.getInfoList()) {
            logger.warn("{}: {}", tmp.getId().getId(), tmp.getClassName());
        }
        logger.warn("");

        //Remove rule
        logger.warn("Remove rule");
        serviceSimulatorGrps.removeRule(id);
        logger.warn("Rule removed");

        //Get rules info
        logger.warn("Get rules info:");
        rulesInfo = serviceSimulatorGrps.getRulesInfo(Empty.newBuilder().build());
        for (RuleInfo tmp : rulesInfo.getInfoList()) {
            logger.warn("{}: {}", tmp.getId().getId(), tmp.getClassName());
        }
        logger.warn("");

        try {
            sender.close();
        } catch (IOException e) {
            logger.error("Can not close sender", e);
        }

        try {
            subscriber.close();
        } catch (IOException e) {
            logger.error("Can not close subscriber");
        }

        logger.warn(LocalDateTime.now(ZoneId.of("UTC")).toString());
    }

    private static RabbitMqSubscriber createSubscriber(MicroserviceConfiguration configuration) {
        RabbitMqSubscriber subscriber = new RabbitMqSubscriber("demo_exchange", ClientMain::processMessage, null, "fix_client_in");
        RabbitMQConfiguration rabbitMQ = configuration.getRabbitMQ();
        try {
            subscriber.startListening(rabbitMQ.getHost(), rabbitMQ.getVirtualHost(), rabbitMQ.getPort(), rabbitMQ.getUsername(), rabbitMQ.getPassword());
        } catch (IOException | TimeoutException e) {
            logger.error("Can not listen fix-client");
        }
        return subscriber;
    }

    private static void sendMessage(RabbitMqMessageSender sender, Message newOrderSingle) {
        try {
            sender.send(newOrderSingle);
        } catch (IOException e) {
            logger.error("Can not send message", e);
        }
    }

    public static void processMessage(String consumingTag, Delivery delivery) {
        try {
            Message message = Message.parseFrom(delivery.getBody());

            if (message.getMetadata().getMessageType().equals("Heartbeat")) {
                return;
            }

            logger.warn("Handle message name = " + message.getMetadata().getMessageType());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private static void waitResult(int ms) {
        logger.warn("Wait {} ms", ms);
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            logger.warn("Waited not {} ms", ms);
        }
        logger.warn("Stop waiting");
    }

    private static MicroserviceConfiguration readConfiguration(String[] args) {
        if (args.length > 0) {
            return ConfigurationUtils.safeLoad(MicroserviceConfiguration::load, MicroserviceConfiguration::new, args[0]);
        } else {
            return new MicroserviceConfiguration();
        }
    }

    public static Message createNewOrderSingle() {
        return createNewOrderSingle("order_id_1");
    }

    private static Message createNewOrderSingle(String clOrdId) {
        return Message.newBuilder()
                .setMetadata(buildMetadata("NewOrderSingle"))
                .putFields("ClOrdID", buildValue(clOrdId))
                .putFields("SecurityID", buildValue("order_id_1_seq"))
                .putFields("SecurityIDSource", buildValue("G")) // COMMON by dictionary
                .putFields("OrdType", buildValue("1")) // MARKET by dictionary
                .putFields("Side", buildValue("1")) // BUY by dictionary
                .putFields("OrderQty", buildValue("10"))
                .putFields("DisplayQty", buildValue("10"))
                .putFields("AccountType", buildValue("1")) // CLIENT by dictionary
                .putFields("OrderCapacity", buildValue("A")) // AGENCY by dictionary
                .putFields("TradingParty", buildTradingParty())
                .putFields("TransactTime", buildValue(ISO_DATE_TIME.format(now())))
                .build();
    }

    private static Value buildTradingParty() {
        return Value.newBuilder().setMessageValue(
                Message.newBuilder()
                        .putFields("NoPartyIDs", Value.newBuilder()
                                .setListValue(buildParties())
                                .build())
                        .build())
                .build();
    }

    private static Metadata buildMetadata(String messageType) {
        return Metadata.newBuilder()
                .setMessageType(messageType).build();
    }

    private static ListValue buildParties() {
        return ListValue.newBuilder()
                .addValues(Value.newBuilder().setMessageValue(Message.newBuilder()
                        .setMetadata(buildMetadata("TradingParty_NoPartyIDs"))
                        .putFields("PartyID", buildValue("party_id_1"))
                        .putFields("PartyIDSource", buildValue("I")) // DIRECTED_BROKER by dictionary
                        .putFields("PartyRole", buildValue("1")) // EXECUTING_FIRM by dictionary
                        .build())
                        .build())
                .addValues(Value.newBuilder().setMessageValue(Message.newBuilder()
                        .setMetadata(buildMetadata("TradingParty_NoPartyIDs"))
                        .putFields("PartyID", buildValue("party_id_2"))
                        .putFields("PartyIDSource", buildValue("G")) // MIC by dictionary
                        .putFields("PartyRole", buildValue("2")) // BROKER_OF_CREDIT by dictionary
                        .build())
                        .build())
                .build();
    }

    private static Value buildValue(String value) {
        return Value.newBuilder().setSimpleValue(value).build();
    }

}
