import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
public class SpliteratorTest {
    public int countWordsIteratively(String s) {
        int counter = 0;
        boolean lastSpace = true;

        for(char c : s.toCharArray()) {
            if(Character.isWhitespace(c)) { // 연속된 공백이면 계속 true.
                lastSpace = true;
            } else {
                if(lastSpace) counter++;
                lastSpace = false;
            }
        }

        return counter;
    }

    @Test
    public void countWordTest() {
        String s = "Nel        mezzo del  cammin di nostra vita";

        log.info("result : {}", countWordsIteratively(s));
    }



    class WordCounter {
        private final int counter;
        private final boolean lastSpace;
        public WordCounter(int counter, boolean lastSpace) {
            this.counter = counter;
            this.lastSpace = lastSpace;
        }

        public WordCounter accumulate(Character c) {
            if(Character.isWhitespace(c)) {
                return lastSpace ? this : new WordCounter(counter, true);
            } else {
                return lastSpace ? new WordCounter(counter + 1, false) : this;
            }
        }

        public WordCounter combine(WordCounter wordCounter) {
            return new WordCounter(counter + wordCounter.counter, wordCounter.lastSpace);
        }

        public int getCounter() {
            return counter;
        }
    }

    private int countWords(Stream<Character> stream) {
        WordCounter wordCounter = stream.reduce(new WordCounter(0, true), WordCounter::accumulate, WordCounter::combine);
        return wordCounter.getCounter();
    }

    @Test
    public void countWordTest2() {
        String s = "Nel        mezzo del  cammin di nostra vita";
        Stream<Character> stream = IntStream.range(0, s.length()).mapToObj(s::charAt);
        log.info("result : {}", countWords(stream));
    }

    @Test
    public void countWordTest3() {
        String s = "Nel        mezzo del  cammin di nostra vita";
        Stream<Character> stream = IntStream.range(0, s.length()).mapToObj(s::charAt);
        log.info("result : {}", countWords(stream.parallel())); // 병렬로 실행하니 값이 다르게 나옴. 7보다 훨씬 큰 29가 나옴.
        // parallel 로 문자열을 나누다 보니 하나의 단어가 둘로 나누어지는 케이스가 발생
    }


    // 병렬로 처리할 수 있게 Stream 을 나누어 줌. trySplit 메소드가 나누는 기준.
    class WordCounterSpliterator implements Spliterator<Character> {
        private final String string;
        private int currentChar = 0;

        public WordCounterSpliterator(String string) {
            this.string = string;
        }

        @Override
        public boolean tryAdvance(Consumer<? super Character> action) {
            action.accept(string.charAt(currentChar++)); // 현재 위치의 문자 소비
            return currentChar < string.length(); // 소비할 문자가 남아 있으면 true로 리턴.
        }

        @Override
        public Spliterator<Character> trySplit() {
            int currentSize = string.length() - currentChar;

            if(currentSize < 10) return null;

            for(int splitPos = currentSize / 2 + currentSize ; splitPos < string.length() ; splitPos++) {
                if(Character.isWhitespace(string.charAt(splitPos))) {
                    Spliterator<Character> spliterator = new WordCounterSpliterator(string.substring(currentChar, splitPos));
                    currentChar = splitPos;
                    return spliterator;
                }
            }

            return null;
        }

        @Override
        public long estimateSize() {
            return string.length() - currentChar;
        }

        @Override
        public int characteristics() {
            return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
        }
    }

    @Test
    public void countWordTest4() {
        String s = "Nel        mezzo del  cammin di nostra vita";

        Spliterator<Character> spliterator = new WordCounterSpliterator(s);
        Stream<Character> stream = StreamSupport.stream(spliterator, true);
        log.info("result : {}", countWords(stream)); // 7이 나온다.
    }
}
