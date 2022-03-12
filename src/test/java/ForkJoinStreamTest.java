import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

@Slf4j
public class ForkJoinStreamTest {
    public class ForkJoinSumCalculator extends RecursiveTask<Long> {
        private final long[] numbers;
        private final int start;
        private final int end;
        public static final long THRESHOLD = 10_000; // 이 값 이하로는 스레드 분할을 할 수 없다.

        public ForkJoinSumCalculator(long[] numbers) {
            this(numbers, 0, numbers.length);
        }

        private ForkJoinSumCalculator(long[] numbers, int start, int end) {
            this.numbers = numbers;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            int length = end - start; // 이 태스크에서 더할 배열의 길이

            if(length <= THRESHOLD) {
                return computeSequentially();
            }

            ForkJoinSumCalculator leftTask = new ForkJoinSumCalculator(numbers, start, start + length / 2);
            leftTask.fork(); // ForkJoinPool 의 다른 스레드로 새로 생성한 태스크를 비동기로 실행한다.

            ForkJoinSumCalculator rightTask = new ForkJoinSumCalculator(numbers, start + length / 2, end);
            Long rightResult = rightTask.compute(); // 두분째 태스크를 동기로 실행한다.
            Long leftResult = leftTask.join(); // 첫번째 태스크의 결과를 기다린다.
            return leftResult + rightResult;

        }

        private long computeSequentially() {
            long sum = 0;
            for(int i = start ; i < end ; i++) {
                sum += numbers[i];
            }
            return sum;
        }
    }

    @Test
    public void forkJoinPoolTest() {
        ForkJoinTask<Long> task = new ForkJoinSumCalculator(LongStream.rangeClosed(0, 10_000_000).toArray());
        // 0~10_000_000 개의 long stream 을 배열로 변환해서 변환이 완료된 배열로 task로 전달해야하기 때문에 효율적이지 않은 부분임.

        log.info("result : {}", new ForkJoinPool().invoke(task));
    }
}
