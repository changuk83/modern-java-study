package javaRx;

import flow.TempInfo;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import rxjava.TempObserver;

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
        var obj = getTempObservable();

        // Observer 의 onNext() 만 구현하고 있음.
        // onError() 는 구현하지 않은 것임. -> 에러가 날 때 아래와 같은 메시지가 남.
        // io.reactivex.rxjava3.exceptions.OnErrorNotImplementedException: The exception was not handled due to missing onError handler in the subscribe() method call.
        // 다음 예제처럼 onError를 구현한 onserver를 이용하면 해당 메시지에서 에러 핸들링을 할 수 있음.
        try {
            obj.blockingSubscribe(i -> log.info("{}", i));
        } catch(Exception e) {
            log.error("Exception log of Error : {} ", e.getMessage());
        }


        // 이렇게 Observer 를 따로 구현해서 사용해도 됨.
        // 여긴 onError() 를 구현했음.
        getTempObservable().blockingSubscribe(new TempObserver());
    }

    public Observable<TempInfo> getTempObservable() {
        return Observable.create(
                emitter -> {
                    Observable.interval(1, TimeUnit.SECONDS)
                            .subscribe(i -> {
                                if(!emitter.isDisposed()) {// 이전 루프에서 에러가 발생한 경우 true 일 수 있음.
                                    if( i >= 5 ) {
                                        emitter.onComplete();
                                    } else {
                                        try {
                                            // fetch 에서 랜덤으로 Exception 발생.
                                            // New York 의 최근 온도를 Observer 에게 보낸다. emitter.onNext() 를 통해.
                                            emitter.onNext(TempInfo.fetch("New York")); // TempInfo 를 publish 한다.
                                        } catch (Exception e) {
                                            emitter.onError(e);
                                        }
                                    }
                                }
                            });
                }
        );
    }

}
