package com.enniu.qa.jmeter.influxdb;

import com.enniu.qa.jmeter.util.InfluxDbConstant;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.influxdb.dto.Serie;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by fuyong on 6/4/15.
 */
public class InfluxDBAggregator {
    private static final Logger log = LoggingManager.getLoggerForClass();

    private SortedMap<Long, List<SampleResult>> buffer = new TreeMap<Long, List<SampleResult>>();
    private List<SampleResult> list=new LinkedList<>();
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");


    public void addSample(SampleResult res) {
        if (log.isDebugEnabled()) {
            log.debug("Got sample to process: " + res);
        }

        list.add(res);
    }

    /**
     * send_second means the number of samples in buffer
     * it can control the rate of sending data out when Jmeter is running on business
     * @return
     */
    public boolean haveDataToSend() {
        return buffer.size() > InfluxDbConstant.BUFFER_SIZE + 1;
    }


    public Serie[] getDataToSend() {
        List<Serie> series=new ArrayList<Serie>();

       for(SampleResult res:list){
            series.add(getSerieFromResult(res));
        }
        return (Serie[])series.toArray(new Serie[series.size()]);
    }

    private Serie getSerieFromResult(SampleResult result){
        int threads=result.getAllThreads();
        long duration=result.getTime();

        long start=result.getStartTime();
        String code=result.getResponseCode();

        return new Serie.Builder(InfluxDbConstant.DEFAULT_SERIE_NAME).columns(InfluxDbConstant.COLUMNS).values(start,threads,duration,code).build();
    }

    /**
     * get statistic for jmeter result in a second
     * @param raw
     * @return
     */
    private Serie getAggregateSecond(List<SampleResult> raw) {

        Date ts = new Date(raw.iterator().next().getEndTime());

        int threads = 0;
        int avg_rt = 0;
        Long[] rtimes = new Long[raw.size()];
        String[] rcodes = new String[raw.size()];
        int cnt = 0;
        int failedCount = 0;
        for (Iterator<SampleResult> it = raw.iterator(); it.hasNext();) {
            SampleResult res = it.next();
            threads += res.getAllThreads();
            avg_rt += res.getTime();
            rtimes[cnt] = Long.valueOf(res.getTime());
            rcodes[cnt] = res.getResponseCode();
            if (!res.isSuccessful()) {
                failedCount++;
            }
            cnt++;
        }

        return new Serie.Builder(InfluxDbConstant.DEFAULT_SERIE_NAME).columns(InfluxDbConstant.COLUMNS).values(format.format(ts),cnt,threads/cnt,avg_rt/cnt,failedCount).build();
    }
}
