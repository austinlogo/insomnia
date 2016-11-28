package northstar.planner.utils;

import android.widget.EditText;

/**
 * Created by Austin on 11/24/2016.
 */

public class StringUtils extends org.apache.commons.lang3.StringUtils{

    public static int getIntFromEditText(EditText editText) {
        try {
            return Integer.parseInt(editText.getText().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
