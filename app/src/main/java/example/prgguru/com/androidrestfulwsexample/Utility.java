package example.prgguru.com.androidrestfulwsexample;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by deksen on 4/6/16 AD.
 */
public class Utility {

    private static Pattern pattern;
    private static Matcher matcher;
    //Email Pattern


    /**
     * Checks for Null String object
     *
     * @param txt
     * @return true for not null and false for null String object
     */
    public static boolean isNotNull(String txt) {
        return txt != null && txt.trim().length() > 0 ? true : false;
    }
}