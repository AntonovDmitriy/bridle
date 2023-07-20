package routetest.utils;

import org.apache.camel.impl.event.ExchangeSentEvent;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class EndpointSendEventNotifier extends EventNotifierSupport {
    private final AtomicInteger counter = new AtomicInteger(0);

    private final String endpointName;

    private int counterValueToStartAction;

    private Consumer<CamelEvent> action;

    public EndpointSendEventNotifier(String endpointName) {
        this.endpointName = endpointName;
    }

    @Override
    public boolean isEnabled(CamelEvent event) {
        return event instanceof ExchangeSentEvent;
    }

    public int getCounter() {
        return counter.get();
    }

    @Override
    public void notify(CamelEvent event) {
        if (event instanceof ExchangeSentEvent sentEvent &&
                sentEvent.getEndpoint().getEndpointUri().startsWith(endpointName)) {
            if (counter.incrementAndGet() == counterValueToStartAction) {
                action.accept(event);
            }
        }
    }

    public void runActionWhenCounterExactlyEquals(int counterValueToStartAction, Consumer<CamelEvent> action) {
        this.counterValueToStartAction = counterValueToStartAction;
        this.action = action;
    }
}
