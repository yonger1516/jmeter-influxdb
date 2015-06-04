package com.enniu.qa.influxdb;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Serie;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by fuyong on 6/4/15.
 */
public class InfluxDBClient {

    public static void main(String[] args){
        InfluxDB influxDB = InfluxDBFactory.connect("http://192.168.2.26:8086", "root", "root");

        String dbName="jmeterResults";
        try{
            influxDB.createDatabase(dbName);
        }catch (RuntimeException e){
            if (!e.getMessage().equals("database "+dbName+" exists")){
                throw e;
            }
        }

        System.out.println("db has been already release");

        Serie serie=new Serie.Builder("default").columns("ts","latency").values(System.currentTimeMillis(),1).values(20, 1).build();
        influxDB.write(dbName, TimeUnit.MILLISECONDS,serie);

        System.out.println("series has written");

        Serie serie2=new Serie.Builder(serie.getName()).columns("c1","c2").build();

        influxDB.write(dbName, TimeUnit.MILLISECONDS, new Serie.Builder(serie.getName()).columns(serie.getColumns()).values(System.currentTimeMillis(), 444).build());

        List<Serie> series=new ArrayList<Serie>();

        influxDB.write(dbName,TimeUnit.MILLISECONDS,(Serie[])series.toArray());
    }
}
