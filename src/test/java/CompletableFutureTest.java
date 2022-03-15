import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;


@Slf4j
public class CompletableFutureTest {
    /**
     * result : 5초에 끝났음. 3개의 job이 각 스레드로 분산되어 실행된 것을 확인할 수 있었음.
     * [pool-1-thread-1] INFO CompletableFutureTest - job a start
     * [pool-1-thread-2] INFO CompletableFutureTest - job b start
     * [Test worker] INFO CompletableFutureTest - oh!
     * [pool-1-thread-1] INFO CompletableFutureTest - job a end
     * [pool-1-thread-2] INFO CompletableFutureTest - job b end
     * [pool-1-thread-2] INFO CompletableFutureTest - job c
     * [Test worker] INFO CompletableFutureTest - result : 8
     */
    @Test
    public void combineTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        int x = 1337;

        CompletableFuture<Integer> a = new CompletableFuture<>();
        CompletableFuture<Integer> b = new CompletableFuture<>();
        CompletableFuture<Integer> c = a.thenCombine(b, (y, z) -> {
            log.info("job c");
            return y+z;
        });
        // block 되지 않는다...
        // c 스레드는 a, b 가 완료가 되었을 때 실행되므로 block 을 피할 수 있다.

        executorService.submit(() -> a.complete(a()));
        executorService.submit(() -> b.complete(b()));

        log.info("oh!");
        sleep(5000);
        log.info("result : {}", c.get());

        log.info("finish!");
        executorService.shutdown();

    }

    @Test
    public void combineTest2() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        int x = 1337;

        CompletableFuture<Integer> a = new CompletableFuture<>();
        CompletableFuture<Integer> b = new CompletableFuture<>();


        executorService.submit(() -> a.complete(a()));
        executorService.submit(() -> b.complete(b()));

        log.info("main sleep!");

        sleep(3000);

        log.info("main awake!");
        log.info("result : {}", a.get() + b.get());

        log.info("finish!");
        executorService.shutdown();

    }

    public int a() throws InterruptedException {
        log.info("job a start");
        sleep(1000);
        log.info("job a end");
        return 5;
    }

    public int b() throws InterruptedException {
        log.info("job b start");

        sleep(5000);

        log.info("job b end");
        return 3;
    }
}
