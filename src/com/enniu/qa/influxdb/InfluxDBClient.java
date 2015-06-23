package com.enniu.qa.influxdb;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Serie;
import java.util.concurrent.TimeUnit;

/**
 * Created by fuyong on 6/4/15.
 */
public class InfluxDBClient {

    static InfluxDB influxDB;
    static String dbName="jmeterResults";
    public static void main(String[] args){
         influxDB= InfluxDBFactory.connect("http://192.168.2.201:8086", "root", "root");


        try{
            influxDB.createDatabase(dbName);
        }catch (RuntimeException e){
            if (!e.getMessage().equals("database "+dbName+" exists")){
                throw e;
            }
        }

        System.out.println("db has been already release");

        deleteSeries("default");

        Serie serie=new Serie.Builder("default").columns("time","latency").values(System.currentTimeMillis(),1).values(System.currentTimeMillis(), 2).build();
        influxDB.write(dbName, TimeUnit.MILLISECONDS,serie);

       /* System.out.println("series has written");

        influxDB.write(dbName, TimeUnit.MILLISECONDS, new Serie.Builder(serie.getName()).columns(serie.getColumns()).values(System.currentTimeMillis(), 444).build());

*/
    }

    public static void deleteSeries(String serieName){
        influxDB.deleteSeries(dbName,serieName);
    }
}
