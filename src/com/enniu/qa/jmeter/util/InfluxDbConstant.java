package com.enniu.qa.jmeter.util;

/**
 * Created by fuyong on 6/4/15.
 */
public class InfluxDbConstant {

    public static final String JMETER_RESULTS="jmeterResults";
    public static String DEFAULT_SERIE_NAME="project_testCase";
    public static String[] COLUMNS={"ts","rps","threads","avg_rt"};
}
