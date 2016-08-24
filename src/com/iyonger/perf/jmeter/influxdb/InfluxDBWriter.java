package com.iyonger.perf.jmeter.influxdb;


import com.iyonger.perf.jmeter.util.StatusNotifierCallback;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: iyonger
 * Date: 15-6-3
 * Time: 下午11:23
 * To change this template use File | Settings | File Templates.
 */
public class InfluxDBWriter extends ResultCollector implements StatusNotifierCallback, Runnable, TestStateListener {
    private static final Logger log = LoggingManager.getLoggerForClass();
    private Object LOCK = new Object();

    private InfluxDBAPIClient apiClient;
    private InfluxDBAggregator aggregator;

    private BlockingQueue<SampleEvent> processingQueue;
    private Thread processorThread;

    public static final String PROJECT = "project";
    private String ADDRESS = "address";
    private String USER = "user";
    private String PASSWORD = "password";
    private String TESTCASE = "serieName";


    public InfluxDBWriter() {
        super();
    }

    @Override
    public void testStarted(String host) {
        synchronized (LOCK) {
            this.apiClient = getAPIClient();
            initiateOnline();
        }
        super.testStarted(host);
    }


    /**
     * start follow with testStarted method
     * it will start itself as thread, running as daemon
     */
    private void initiateOnline() {

        try {

            informUser("InfluxDB writer started");

            aggregator = new InfluxDBAggregator();
            processingQueue = new LinkedBlockingQueue<SampleEvent>();
            processorThread = new Thread(this);
            processorThread.setDaemon(true);

            processorThread.start();
        } catch (RuntimeException ex) {
            informUser("Failed to start writer");
            log.warn("Failed to start writer", ex);
        }

    }


    @Override
    public void notifyAbout(String info) {
        //To change body of implemented methods use File | Settings | File Templates.
        informUser(info);
    }

    public InfluxDBAPIClient getAPIClient() {
        return new InfluxDBAPIClient(this, getAddress(), getUser(), getPassword());
    }


    private void informUser(String string) {
        log.info(string);
        if (getVisualizer() != null && getVisualizer() instanceof InfluxDBWriterGui) {
            ((InfluxDBWriterGui) getVisualizer()).inform(string);
        } else {
            log.info(string);
        }
    }

    @Override
    public void sampleOccurred(SampleEvent event) {
        super.sampleOccurred(event);

        try {
            if (!processingQueue.offer(event, 1, TimeUnit.SECONDS)) {
                log.warn("Failed first dequeue insert try, retrying");
                if (!processingQueue.offer(event, 1, TimeUnit.SECONDS)) {
                    log.error("Failed second try to inser into deque, dropped sample");
                }
            }
        } catch (InterruptedException ex) {
            log.info("Interrupted while putting sample event into deque", ex);
        }

    }

    @Override
    public void run() {
        while (true) {
            try {
                SampleEvent event = processingQueue.poll(1, TimeUnit.SECONDS);
                if (event != null) {
                    aggregator.addSample(event.getResult());
                }

                if (aggregator.haveDataToSend()) {
                    try {
                        //log.info("rearch default buffer size,sending data ...");
                        this.apiClient.sendOnlineData(aggregator.getDataToSend());
                    } catch (IOException ex) {
                        log.warn("Failed to send active test data", ex);
                    }
                }
            } catch (InterruptedException ex) {
                log.debug("Interrupted while taking sample event from deque", ex);
                break;
            }
        }
    }


    /**
     * continue to write data to db to avoid some data doesn't send out in time
     * when test finish.
     * stop thread which start at the beginning
     *
     * @param host
     */
    @Override
    public void testEnded(String host) {
        super.testEnded(host);
        synchronized (LOCK) {
            // FIXME: trying to handle safe upgrade, needs to be removed in the future
            // @see https://issues.apache.org/bugzilla/show_bug.cgi?id=56807
            try {
                this.apiClient.sendOnlineData(aggregator.getDataToSend());
            } catch (IOException ex) {
                log.warn("Failed to send active test data", ex);
            }

            finishOnline();

        }
        clearData();
    }


    private void finishOnline() {
        processorThread.interrupt();
        while (processorThread.isAlive() && !processorThread.isInterrupted()) {
            log.info("Waiting for aggregator thread to stop...");
            try {
                Thread.sleep(50);
                processorThread.interrupt();
            } catch (InterruptedException ex) {
                log.warn("Interrupted sleep", ex);
            }
        }
        log.info("Ending influxDB writer");
        apiClient.endOnline();
    }

    public void setProject(String proj) {
        setProperty(PROJECT, proj);
    }

    public String getProject() {
        return getPropertyAsString(PROJECT);
    }

    public String getAddress() {
        return getPropertyAsString(ADDRESS);
    }

    public void setAddress(String address) {
        setProperty(ADDRESS, address);
    }

    public String getUser() {
        return getPropertyAsString(USER);
    }

    public void setUser(String user) {
        setProperty(USER, user);
    }

    public String getPassword() {
        return getPropertyAsString(PASSWORD);
    }

    public void setPassword(String password) {
        setProperty(PASSWORD, password);
    }

    public String getSerieName() {
        return getPropertyAsString(TESTCASE);
    }

    public void setSerieName(String serieName) {
        setProperty(TESTCASE, serieName);
    }
}
