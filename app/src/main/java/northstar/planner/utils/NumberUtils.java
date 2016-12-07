package northstar.planner.utils;

/**
 * Created by Austin on 12/6/2016.
 */

public class NumberUtils {
    public static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
