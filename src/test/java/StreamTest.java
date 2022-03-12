import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.stream.LongStream;

@Slf4j
public class StreamTest {
    public class Accumulator {
        public long total = 0;
        public void add(long value) { total += value; }
    }

    @Test
    public void parallelSumInFault() {
        long n = 10000000;
        Accumulator accumulator = new Accumulator();
        LongStream.rangeClosed(1, n).parallel().forEach(accumulator::add);
        log.info("result : {}", accumulator.total);
        // 50000005000000 이 나와야 하는데 여러 스레드가 동시에 접근하는 data race 문제때문에 매번 값이 다르게 나옴. 속도도 느림.
        // total += value 가 atomic 연산이 아님.
    }
}
