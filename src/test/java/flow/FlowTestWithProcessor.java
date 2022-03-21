package flow;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Flow;

@Slf4j
public class FlowTestWithProcessor {
    @Test
    public void getTempTest() {
        getTemperatures("New York").subscribe(new TempSubscriber());
    }

    private static Flow.Publisher<TempInfo> getTemperatures(String town) {
        return subscriber -> {
            TempProcessor tempProcessor = new TempProcessor();
            tempProcessor.subscribe(subscriber);
            tempProcessor.onSubscribe(new TempSubscription(tempProcessor, town));
        };
    }
}
