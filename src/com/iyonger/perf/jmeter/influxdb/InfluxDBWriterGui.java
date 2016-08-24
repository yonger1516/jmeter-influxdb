package com.iyonger.perf.jmeter.influxdb;

import kg.apc.jmeter.JMeterPluginsUtils;
import kg.apc.jmeter.gui.GuiBuilderHelper;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.visualizers.gui.AbstractVisualizer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by fuyong on 6/4/15.
 */
public class InfluxDBWriterGui extends AbstractVisualizer {

    public static final String WIKIPAGE = "InfluxDB Writer @sum2000@live.cn";
    private JTextField testTitle;
    private JTextField projectKey;
    private JTextArea infoArea;
    private JTextField address;
    private JTextField user;
    private JTextField password;

    public InfluxDBWriterGui(){
        super();
        init();
        initFields();
    }

    @Override
    protected Component getFilePanel() {
        return new JPanel();
    }

    @Override
    public String getStaticLabel() {
        return JMeterPluginsUtils.prefixLabel("InfluxDB Writer");
    }

    @Override
    public String getLabelResource() {
        return getClass().getCanonicalName();
    }

    @Override
    public TestElement createTestElement() {
        TestElement te = new InfluxDBWriter();
        modifyTestElement(te);
        te.setComment("write Jmeter result to influxDb");
        return te;
    }

    @Override
    public void modifyTestElement(TestElement te) {
        super.modifyTestElement(te);
        if (te instanceof InfluxDBWriter) {
            InfluxDBWriter fw = (InfluxDBWriter) te;
            fw.setProject(projectKey.getText());
            fw.setAddress(address.getText());
            fw.setUser(user.getText());
            fw.setPassword(password.getText());
            fw.setSerieName(testTitle.getText());

        }
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        InfluxDBWriter fw = (InfluxDBWriter) element;

        projectKey.setText(fw.getProject());
        address.setText(fw.getAddress());
        user.setText(fw.getUser());
        password.setText(fw.getPassword());
        testTitle.setText(fw.getSerieName());

    }


    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        add(JMeterPluginsUtils.addHelpLinkToPanel(makeTitlePanel(), WIKIPAGE), BorderLayout.NORTH);//TODO:the link target jmeter-plugin site, which should be changed

        JPanel mainPanel = new JPanel(new GridBagLayout());

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;

        GridBagConstraints editConstraints = new GridBagConstraints();
        editConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        editConstraints.weightx = 1.0;
        editConstraints.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("Project: ", JLabel.RIGHT));
        addToPanel(mainPanel, editConstraints, 1, row, projectKey = new JTextField(20));

        editConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        labelConstraints.insets = new java.awt.Insets(2, 0, 0, 0);

        row++;
        addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("Test Case: ", JLabel.RIGHT));
        addToPanel(mainPanel, editConstraints, 1, row, testTitle = new JTextField(20));

        row++;
        addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("InfluxDB url address: ", JLabel.RIGHT));
        addToPanel(mainPanel, editConstraints, 1, row, address = new JTextField(30));

        row++;
        addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("user: ", JLabel.RIGHT));
        addToPanel(mainPanel, editConstraints, 1, row, user = new JTextField(30));

        row++;
        addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("password: ", JLabel.RIGHT));
        addToPanel(mainPanel, editConstraints, 1, row, password = new JTextField(30));

        editConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        labelConstraints.insets = new java.awt.Insets(4, 0, 0, 0);

        row++;
        addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("Info Area: ", JLabel.RIGHT));
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setOpaque(false);

        addToPanel(mainPanel, editConstraints, 1, row, GuiBuilderHelper.getTextAreaScrollPaneContainer(infoArea, 6));

        JPanel container = new JPanel(new BorderLayout());
        container.add(mainPanel, BorderLayout.NORTH);
        add(container, BorderLayout.CENTER);
    }

    private void initFields() {
        testTitle.setText("project_testcase");
        projectKey.setText("DEFAULT");
        address.setText("http://localhost:8086");
        user.setText("root");
        password.setText("root");
    }

    public void inform(String string) {
        infoArea.append(string + "\n");
    }

    private void addToPanel(JPanel panel, GridBagConstraints constraints, int col, int row, JComponent component) {
        constraints.gridx = col;
        constraints.gridy = row;
        panel.add(component, constraints);
    }

    @Override
    public void clearGui() {
        super.clearGui();
        initFields();
    }


    @Override
    public void clearData() {
        infoArea.setText("");
    }


    @Override
    public void add(SampleResult sampleResult) {

    }
}
