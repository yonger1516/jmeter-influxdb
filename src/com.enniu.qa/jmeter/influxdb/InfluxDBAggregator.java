package com.enniu.qa.jmeter.influxdb;

import com.enniu.qa.jmeter.util.InfluxDbConstant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
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
    private static final long SEND_SECONDS = 5;
    private long lastTime = 0;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");


    public void addSample(SampleResult res) {
        if (log.isDebugEnabled()) {
            log.debug("Got sample to process: " + res);
        }

        log.info("Got sample to process: " + res);

        Long time = res.getEndTime() / 1000;
        if (!buffer.containsKey(time)) {
            // we need to create new sec list
            if (time < lastTime) {
                // a problem with times sequence - taking last available
                Iterator<Long> it = buffer.keySet().iterator();
                while (it.hasNext()) {
                    time = it.next();
                }
            }
            buffer.put(time, new LinkedList<SampleResult>());
        }
        lastTime = time;
        buffer.get(time).add(res);
    }

    public boolean haveDataToSend() {
        return buffer.size() > SEND_SECONDS + 1;
    }

    public Serie[] getDataToSend() {
        List<Serie> series=new ArrayList<Serie>();

        Iterator<Long> it = buffer.keySet().iterator();
        //int cnt = 0;
        while (it.hasNext()) {
            Long sec = it.next();
            List<SampleResult> raw = buffer.get(sec);
            series.add(getAggregateSecond(raw));
            it.remove();
            //cnt++;
        }
        return (Serie[])series.toArray(new Serie[series.size()]);
    }

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

        return new Serie.Builder(InfluxDbConstant.DEFAULT_SERIE_NAME).columns(InfluxDbConstant.COLUMNS).values(format.format(ts),cnt,threads/cnt,avg_rt/cnt).build();
    }
}
