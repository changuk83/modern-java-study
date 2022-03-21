package rxjava;

import flow.TempInfo;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TempObserver implements Observer<TempInfo> {

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        log.info("onSubscribe!");
    }

    @Override
    public void onNext(@NonNull TempInfo tempInfo) {
        log.info("!!!! {}", tempInfo);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        log.error("Got problem!!! {}", e.getMessage());
    }

    @Override
    public void onComplete() {
        log.info("Done!");
    }
}
