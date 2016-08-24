package com.iyonger.perf.jmeter.util;

/**
 * Created by fuyong on 6/4/15.
 */
public class InfluxDbConstant {

    public static final String DATABASE="jmeter";
    public static String DEFAULT_SERIE_NAME="project_testCase";
    public static String[] COLUMNS={"time","threads","duration","code"};
    public static int BUFFER_SIZE=100;
}
