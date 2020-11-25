# Overview
Simulator is a passive th2 component with parameterised rules which is implemented simulate logic.
Script or other components can create, remove and get information about running rules in simulator via gRPC. Simulator can interact with conn (Connects).

This project implemented gRPC API described in the [th2-grpc-sim-template](https://github.com/th2-net/th2-grpc-sim-template/blob/master/src/main/proto/th2_grpc_sim_template/sim_template.proto "sim_template.proto")
 
This project is template how create and add custom rules.

## Rules

Rules have two methods:
1. checkTriggered - for check, if rule will generate message
1. handle or handleTriggered - for generate outgoing messages

Rules can use arguments. For this you should use constructor in your custom class.

Rules can be one of 3 types, which deffer only login for method checkTriggered:
1. Compare rule ([Example](https://github.com/th2-net/th2-sim-template/blob/master/src/main/kotlin/com/exactpro/th2/sim/template/rule/TemplateAbstractRule.kt "TemplateAbstractRule.kt"))
1. Predicate rule ([Example](https://github.com/th2-net/th2-sim-template/blob/master/src/main/kotlin/com/exactpro/th2/sim/template/rule/TemplatePredicateRule.kt "TemplatePredicateRule.kt"))
1. Abstract rule ([Example](https://github.com/th2-net/th2-sim-template/blob/master/src/main/kotlin/com/exactpro/th2/sim/template/rule/TemplateFixRule.kt "TemplateFixRule.kt"))

### Compare rule
This type has the most simple logic for check. Rules of this type will be triggered only if message type and fields incoming message is equals of values which we set in rule.

### Predicate rule
This type has more flexible check conditions. Rules of this type will be triggered only if message type and fields logical functions, which set in rule, return a true value. Logical functions of fields in this rule is isolated between each other.

### Abstract rule
This type has the most flexible check conditions. Rules of this type will be triggered if your custom logic in method checkTriggered return true.

## Service
If you want to add possibility for create a rule via gRPC you should edit [th2-grpc-sim-template](https://github.com/th2-net/th2-grpc-sim-template/blob/master/src/main/proto/th2_grpc_sim_template/sim_template.proto "sim_template.proto") and class [TemplateService](https://github.com/th2-net/th2-sim-template/blob/master/src/main/kotlin/com/exactpro/th2/sim/template/service/TemplateService.kt "TemplateService.kt").
For add a rule to simulator you can use utility method ``ServiceUtils.addRule`` or method from ``Simulator`` class with name ``addRule``. On gRPC request you should return ``RuleID``.

## Work example

On picture present example of work simulator with enabled rule ``TemplateFixRule``. This rule send ``ExecutionReport`` message if income message is ``NewOrderSingle``.
If rule in simulator income wrong message (not ``NewOrderSingle``), rule will not generate outgoing message. 
If rule in simulator income message is ``NewOrderSingle``, rule will generate one ``ExecutionReport``.

![picture](scheme.png)