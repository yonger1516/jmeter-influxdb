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

    /**
     * create database in influxdb, in case the database dropped some times
     * it will throw out runtime exception when creating an exist database,so
     * catch it and check the message
     *
     * @param address
     * @param user
     * @param password
     * @throws RuntimeException
     */
    public void initDB(String address,String user,String password)throws RuntimeException{
        influxDB = InfluxDBFactory.connect(address, user, password);

        try{
            influxDB.createDatabase(InfluxDbConstant.DATABASE);
        }catch (RuntimeException e){
            if (!e.getMessage().equals("database "+InfluxDbConstant.DATABASE+" exists")){
                throw e;
            }
        }

        callback.notifyAbout("InfluxDb connected successful");
    }


    /**
     * write series by batch, it acctually sending data by http restful api
     *
     * @param series
     * @throws IOException
     */
    public void sendOnlineData(Serie[] series) throws IOException {
        influxDB.write(InfluxDbConstant.DATABASE,TimeUnit.MILLISECONDS,series);
    }

    public void endOnline(){

    }
}