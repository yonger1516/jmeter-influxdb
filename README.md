Purpose:
Write jmeter result data to influxdb,then display by grafana with OS system performance metrics, which help one detect issue quickly.

Usage:
1.compile source with maven
mvn clean package

2.move the generated jar to lib/ext under jmeter root folder

3.restart jmeter, then you will see the new plugin in listener components

4.connect influxdb
Input correct influxdb url and user and password. The plugin will write data when running or before finishing.
   
