package tests.util_test;

import util.DateUtil;

import java.util.Date;

/**
 * Created by Admin on 24.05.2017.
 */
public class DateUtilTest {
    public static void main(String[] args) {
        Date date = new Date();
        System.out.println(DateUtil.format(date));
    }
}
