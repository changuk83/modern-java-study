package javaRx;

import flow.TempInfo;
import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ObservableTest {
    @Test
    public void obTest() {
        Observable<Long> onePerSec = Observable.interval(1, TimeUnit.SECONDS);

        onePerSec.blockingSubscribe(
                // 여기는 subscriber 를 구현함. onNext()
                i -> log.info("{}" , TempInfo.fetch("New York"))
        );

        log.info("end");
    }

    /**
     * Error 처리를 위해 Observable.create() 를 이용해 Observable 구현.
     */
    @Test
    public void obTest2() {
        // Observable<T> create(@NonNull ObservableOnSubscribe<T> source) { ... }
        // ObservableOnSubscribe 은 파라미터로 emitter를 전달하는 void subscribe(@NonNull ObservableEmitter<T> emitter) 를 구현.
        var obj = Observable.create(
                emitter -> {
                    Observable.interval(1, TimeUnit.SECONDS)
                            .subscribe(i -> {
                               if(!emitter.isDisposed()) {// 이전 루프에서 에러가 발생한 경우 true 일 수 있음.
                                   if( i >= 5 ) {
                                       emitter.onComplete();
                                   } else {
                                       try {
                                           // fetch 에서 랜덤으로 Exception 발생.
                                           emitter.onNext(TempInfo.fetch("New York")); // TempInfo 를 publish 한다.
                                       } catch (Exception e) {
                                           emitter.onError(e);
                                       }
                                   }
                               }
                            });
                }
        );

        obj.blockingSubscribe(i -> log.info("{}", i));
    }
}
