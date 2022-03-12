import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Objects;
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
}
