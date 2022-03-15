package flow;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Flow;

@Slf4j
public class TempSubscriber implements Flow.Subscriber<TempInfo> {
    private Flow.Subscription subscription;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(TempInfo item) {
        log.info("tempInfo : {}", item);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("error : {}", throwable.getMessage());
    }

    @Override
    public void onComplete() {
        log.info("Done!");
    }
}
