Purpose
=====
A Jmeter plugin which can write jmeter result to influxdb,then display as chart by grafana with other OS system performance metrics, that help one detect issue quickly.

Usage
====
* compile source with maven

   mvn clean package

* move the generated jar to lib/ext under jmeter root folder

* restart jmeter, then you will see the new plugin in listener components

* connect influxdb

   Input correct influxdb url and user and password. 

   The plugin will write data when running or before finishing.

* view data in influxdb

   It create a database named "jmeter", and the serie name is the test case name that you input.
   
