import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Function;

@Slf4j
public class FutureTest {
    @Test
    public void functionTest() {
        FunctionExt<Integer, Integer> f = (a) -> a + 5;
        FunctionExt<Integer, Integer> dble = (a) -> a * 2;
        FunctionExt<Integer, Integer> q1 = (a) -> a + 1;
        FunctionExt<Integer, Integer> q2 = (a) -> a + 2;

        var result = f.andThen(dble).thenBoth(q1, q2).apply(5);

        log.info("result : {}", f.apply(5));
        log.info("result : {}", f.andThen(dble).apply(5));
        log.info("result : {}", result);
    }

    public interface FunctionExt<T extends Integer, R extends Integer> extends Function<Integer, Integer> {
        default FunctionExt<T, R> andThen(FunctionExt<T, R> after) {
            Objects.requireNonNull(after);
            return (t) -> after.apply(apply(t));
        }

        default FunctionExt<T, R> thenBoth(FunctionExt<T, R> q1, FunctionExt<T, R> q2) {
            return (t) -> {
                var v1 = q1.apply(apply(t));
                var v2 = q2.apply(apply(t));
                log.info("thenBoth : {} {}", v1, v2);
                return v1 + v2;
            };
        }
    }


    @Test
    public void oldFutureTest() {
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<Double> future = executor.submit(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                return doSomeLongComputation();
            }

            private Double doSomeLongComputation() {
                log.info("doing some long computation");
                return 1.1;
            }
        });

        doSomethingElse();

        try {
            // get 을 하는 순간 계산이 완료되었다면 즉시 결과를 반환하겠지만
            // 그렇지 않다면 준비될 때 까지 현재 스레드를 block 시킨다.
            Double result = future.get(1, TimeUnit.SECONDS);
        } catch(ExecutionException e) {
            log.error("{}", e);
        } catch (InterruptedException ie) {
            log.error("{}", ie);
        } catch (TimeoutException te) {
            log.error("{}", te);
        }

        log.info("finish");
    }

    private void doSomethingElse() {
        log.info("doing something else");
    }
}
