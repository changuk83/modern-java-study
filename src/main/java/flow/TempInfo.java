package flow;

import lombok.Data;

import java.util.Random;

@Data
public class TempInfo {
    public static final Random random = new Random();

    private final String town;
    private final int temp;

    public TempInfo(String town, int temp) {
        this.town = town;
        this.temp = temp;
    }

    public static TempInfo fetch(String town) {
        // 10분의 1 확률로 실패.
        if(random.nextInt(10) == 0) throw new RuntimeException("Error!");

        return new TempInfo(town, random.nextInt(100));
    }

    @Override
    public String toString() {
        return town + " : " + temp;
    }

}
