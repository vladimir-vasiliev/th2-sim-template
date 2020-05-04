import static com.exactpro.th2.simulator.util.ValueUtils.getValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.exactpro.evolution.api.phase_1.Message;
import com.exactpro.evolution.api.phase_1.Message.Builder;
import com.exactpro.evolution.api.phase_1.Metadata;
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
                .setMetadata(Metadata.newBuilder().setMessageType("NewOrderSingle").build()).build();
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
