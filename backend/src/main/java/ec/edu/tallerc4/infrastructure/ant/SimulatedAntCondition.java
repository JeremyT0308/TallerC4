package ec.edu.tallerc4.infrastructure.ant;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class SimulatedAntCondition implements Condition {
    @Override public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String provider = context.getEnvironment().getProperty("ant.provider", "simulated");
        return "simulated".equalsIgnoreCase(provider);
    }
}
