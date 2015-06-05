Purpose
=====
A Jmeter plugin which can write jmeter result to influxdb,then display as chart by grafana with other OS system performance metrics, that help one detect issue quickly.

Usage
====
1.compile source with maven

mvn clean package

2.move the generated jar to lib/ext under jmeter root folder

3.restart jmeter, then you will see the new plugin in listener components

4.connect influxdb

Input correct influxdb url and user and password. 

The plugin will write data when running or before finishing.

5.view data in influxdb

It create a database named "jmeter", and the serie name is the test case name that you input.
   
