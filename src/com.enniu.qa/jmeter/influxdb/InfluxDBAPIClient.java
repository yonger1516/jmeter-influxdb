package com.enniu.qa.jmeter.influxdb;

import com.enniu.qa.jmeter.util.InfluxDbConstant;
import com.enniu.qa.jmeter.util.StatusNotifierCallback;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Serie;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 15-6-3
 * Time: 下午11:31
 * To change this template use File | Settings | File Templates.
 */
public class InfluxDBAPIClient {
    private static final Logger log= LoggingManager.getLoggerForClass();

    private StatusNotifierCallback callback;

    private InfluxDB influxDB;


    public InfluxDBAPIClient(StatusNotifierCallback callback, String address, String user,String password) {
        this.callback = callback;
        initDB(address,user,password);
    }

    public void initDB(String address,String user,String password)throws RuntimeException{
        influxDB = InfluxDBFactory.connect(address, user, password);

        try{
            influxDB.createDatabase(InfluxDbConstant.JMETER_RESULTS);
        }catch (RuntimeException e){
            if (!e.getMessage().equals("database "+InfluxDbConstant.JMETER_RESULTS+" exists")){
                throw e;
            }
        }

        callback.notifyAbout("InfluxDb connected successful");
        log.info("influxdb has been created");

        /*create series ,if not specify using default series name*/
        /*if (null!=serieName&&!serieName.isEmpty()){
            DEFAULT_SERIE_NAME=serieName;
        }
        defaultSerie=new Serie.Builder(DEFAULT_SERIE_NAME).columns("ts","rps","threads","avg_rt").build();
        influxDB.write(JMETER_RESULTS, TimeUnit.MILLISECONDS,defaultSerie);*/
    }


    public void sendOnlineData(Serie[] series) throws IOException {
        influxDB.write(InfluxDbConstant.JMETER_RESULTS,TimeUnit.MILLISECONDS,series);
    }

    public void endOnline(){

    }
}