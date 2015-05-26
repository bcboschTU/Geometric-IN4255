package workshop;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

import jv.number.PuDouble;

import jv.object.PsConfig;
import jv.object.PsDebug;
import jv.object.PsUpdateIf;
import jvx.project.PjWorkshop_IP;

public class MyWorkshop_IP extends PjWorkshop_IP implements ActionListener {

	protected Button m_bMakeRandomElementColors;
	protected Button m_bMakeRandomVertexColors;

	protected PuDouble m_xOff;


	// Task 1
	protected Button m_bShapeRegularty;
	protected Button m_bValence;
	protected Button m_bAngles;
	protected Button m_bLengthEdges;

	protected Label m_mean;
    protected Label m_mean_label;
	protected Label m_min;
    protected Label m_min_label;
	protected Label m_max;
    protected Label m_max_label;
	protected Label m_sd;
    protected Label m_sd_label;

	//Task 2
	protected Button m_bGenus;
	protected Label m_genus_label;
	protected Label m_genus;
	protected Button m_bArea;
	protected Label m_area;
    protected Label m_area_label;
    protected Button m_bCurvature;    
	protected Label m_meancurvature_label;
	protected Label m_c_mean;
    protected Label m_c_mean_label;
	protected Label m_c_min;
    protected Label m_c_min_label;
	protected Label m_c_max;
    protected Label m_c_max_label;
	protected Label m_c_sd;
    protected Label m_c_sd_label;


	//Task3
	protected Button m_bItterSmooth;
	protected Label smoothing_iteration_label_iterative;
	protected TextField smoothing_iteration_iterative;
	protected Label smoothing_parameter_label_iterative;
	protected TextField smoothing_parameter_iterative;
	protected int numIter = 20;
	protected Button m_bMeanCurvatureSmooth;
	protected Label smoothing_iteration_label_curvature;
	protected TextField smoothing_iteration_curvature;
	protected Label smoothing_parameter_label_curvature;
	protected TextField smoothing_parameter_curvature;

	
	MyWorkshop m_ws;
	
	public MyWorkshop_IP() {
		super();
		if(getClass() == MyWorkshop_IP.class)
			init();
	}
	
	public void init() {
		super.init();
		setTitle("Geometric Modeling Practical 1");
	}
	
	public String getNotice() {
		return "Practical Assignment 1";
	}
	
	public void setParent(PsUpdateIf parent) {
		super.setParent(parent);
		m_ws = (MyWorkshop)parent;
	
		/*addSubTitle("Example functions:");
		
		m_bMakeRandomElementColors = new Button("Random Element Colors");
		m_bMakeRandomElementColors.addActionListener(this);
		m_bMakeRandomVertexColors = new Button("Random Vertex Colors");
		m_bMakeRandomVertexColors.addActionListener(this);
		Panel panel1 = new Panel(new FlowLayout(FlowLayout.CENTER));
		panel1.add(m_bMakeRandomElementColors);
		panel1.add(m_bMakeRandomVertexColors);
		add(panel1);
		
		m_xOff = new PuDouble("X Offset");
		m_xOff.setDefBounds(-10, 10, 0.1, 1);
		m_xOff.addUpdateListener(this);
		m_xOff.init();
		add(m_xOff.getInfoPanel());*/
		
		addSubTitle("Task 1 - Shape statistics:");
		
		m_bShapeRegularty = new Button("Shape regularity");
		m_bShapeRegularty.addActionListener(this);
		m_bValence = new Button("Valence");
		m_bValence.addActionListener(this);
		m_bAngles = new Button("Angles");
		m_bAngles.addActionListener(this);
		m_bLengthEdges = new Button("Length");
		m_bLengthEdges.addActionListener(this);
		Panel panel2 = new Panel(new FlowLayout(FlowLayout.LEFT));
		panel2.add(m_bShapeRegularty);
		panel2.add(m_bValence);
		panel2.add(m_bAngles);
		panel2.add(m_bLengthEdges);
		add(panel2);

        Panel shapeStatistics = new Panel(new GridBagLayout());
        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.EAST;
        GridBagConstraints right = new GridBagConstraints();
        right.weightx = 2.0;
        right.fill = GridBagConstraints.HORIZONTAL;
        right.gridwidth = GridBagConstraints.REMAINDER;
		m_mean = new Label("");
        m_mean_label = new Label("Mean:");
		m_min = new Label("");
        m_min_label = new Label("Min:");
		m_max = new Label("");
        m_max_label = new Label("Max:");
		m_sd = new Label("");
        m_sd_label = new Label("Standard Deviation:");
        shapeStatistics.add(m_mean_label, left);
        shapeStatistics.add(m_mean, right);
        shapeStatistics.add(m_min_label, left);
        shapeStatistics.add(m_min, right);
        shapeStatistics.add(m_max_label, left);
        shapeStatistics.add(m_max, right);
        shapeStatistics.add(m_sd_label, left);
        shapeStatistics.add(m_sd, right);

        add(shapeStatistics);

		addSubTitle("Task 2 - Surface Analyses:");
		Panel panel3 = new Panel(new FlowLayout(FlowLayout.LEFT));
		m_bGenus = new Button("Genus");
		m_bGenus.addActionListener(this);
		m_bArea= new Button("Area");
		m_bArea.addActionListener(this);
		m_bCurvature= new Button("Mean Curvature");
		m_bCurvature.addActionListener(this);
		panel3.add(m_bGenus);
		panel3.add(m_bArea);
		panel3.add(m_bCurvature);
		add(panel3);

        Panel panel4 = new Panel(new GridBagLayout());
        left = new GridBagConstraints();
        left.anchor = GridBagConstraints.EAST;
        right = new GridBagConstraints();
        right.weightx = 2.0;
        right.fill = GridBagConstraints.HORIZONTAL;
        right.gridwidth = GridBagConstraints.REMAINDER;
        m_genus_label = new Label("Genus:");
        panel4.add(m_genus_label, left);
		m_genus = new Label("");
        panel4.add(m_genus, right);
        m_area_label = new Label("Area:");
        panel4.add(m_area_label, left);
        m_area = new Label("");
        panel4.add(m_area, right);
        m_meancurvature_label = new Label("Mean curvature:");
        panel4.add(m_meancurvature_label, left);
        panel4.add(new Label(""), right);
        add(panel4);

        Panel meanCurvatureStatistics = new Panel(new GridBagLayout());
        left = new GridBagConstraints();
        left.anchor = GridBagConstraints.EAST;
        right = new GridBagConstraints();
        right.weightx = 2.0;
        right.fill = GridBagConstraints.HORIZONTAL;
        right.gridwidth = GridBagConstraints.REMAINDER;
		m_c_mean = new Label("");
        m_c_mean_label = new Label("Mean:");
		m_c_min = new Label("");
        m_c_min_label = new Label("Min:");
		m_c_max = new Label("");
        m_c_max_label = new Label("Max:");
		m_c_sd = new Label("");
        m_c_sd_label = new Label("Standard Deviation:");
        meanCurvatureStatistics.add(m_c_mean_label, left);
        meanCurvatureStatistics.add(m_c_mean, right);
        meanCurvatureStatistics.add(m_c_min_label, left);
        meanCurvatureStatistics.add(m_c_min, right);
        meanCurvatureStatistics.add(m_c_max_label, left);
        meanCurvatureStatistics.add(m_c_max, right);
        meanCurvatureStatistics.add(m_c_sd_label, left);
        meanCurvatureStatistics.add(m_c_sd, right);
        add(meanCurvatureStatistics);

		addSubTitle("Task 3 - Surface Smoothing:");
		Panel panel5 = new Panel(new FlowLayout(FlowLayout.LEFT));
		m_bItterSmooth = new Button("Smooth iterative");
		m_bItterSmooth.addActionListener(this);
		panel5.add(m_bItterSmooth);
		add(panel5);

		Panel panel6 = new Panel(new GridBagLayout());
		smoothing_parameter_label_iterative = new Label("Smoothing parameter iterative:");
		smoothing_parameter_iterative = new TextField("0");
		smoothing_iteration_label_iterative = new Label("Amount of smoothing iterations iterative:");
		smoothing_iteration_iterative = new TextField("0");
		panel6.add(smoothing_iteration_label_iterative, left);
		panel6.add(smoothing_iteration_iterative, right);
		panel6.add(smoothing_parameter_label_iterative, left);
		panel6.add(smoothing_parameter_iterative, right);
		add(panel6);

		Panel panel7 = new Panel(new FlowLayout(FlowLayout.LEFT));
		m_bMeanCurvatureSmooth = new Button("Mean Curvature iterative");
		m_bMeanCurvatureSmooth.addActionListener(this);
		panel7.add(m_bMeanCurvatureSmooth);
		add(panel7);

		Panel panel8 = new Panel(new GridBagLayout());
		smoothing_parameter_label_curvature = new Label("Smoothing parameter curvature:");
		smoothing_parameter_curvature = new TextField("0");
		smoothing_iteration_label_curvature = new Label("Amount of smoothing iterations curvature:");
		smoothing_iteration_curvature = new TextField("0");
		panel8.add(smoothing_iteration_label_curvature, left);
		panel8.add(smoothing_iteration_curvature, right);
		panel8.add(smoothing_parameter_label_curvature, left);
		panel8.add(smoothing_parameter_curvature, right);
		add(panel8);

		validate();
	}
	
	@Override
	public boolean update(Object event) {
		if (event == m_xOff) {
			m_ws.setXOff(m_xOff.getValue());
			m_ws.m_geom.update(m_ws.m_geom);
			return true;
		} else
			return super.update(event);
	}
	
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == m_bMakeRandomElementColors) {
			m_ws.makeRandomElementColors();
			m_ws.m_geom.update(m_ws.m_geom);
			return;
		}
		else if (source == m_bMakeRandomVertexColors) {
			m_ws.makeRandomVertexColors();
			m_ws.m_geom.update(m_ws.m_geom);
			return;
		}
		else if(source == m_bShapeRegularty){
			updateValues(m_ws.calculateStatistics(m_ws.calculateShapeRegularity()));
			m_ws.m_geom.update(m_ws.m_geom);
			return;
		}
		else if(source == m_bValence){
			updateValues(m_ws.calculateStatistics(m_ws.calculateValence()));
			m_ws.m_geom.update(m_ws.m_geom);
			return;
		}
		else if(source == m_bAngles){
			updateValues(m_ws.calculateStatistics(m_ws.calculateAngles()));
			m_ws.m_geom.update(m_ws.m_geom);
			return;
		}
		else if(source == m_bLengthEdges){
			updateValues(m_ws.calculateStatistics(m_ws.calculateLengthEdges()));
			m_ws.m_geom.update(m_ws.m_geom);
			return;
		}
		else if(source == m_bGenus){
			updateValueGenus(m_ws.calculateGenus());
			m_ws.m_geom.update(m_ws.m_geom);
			return;
		}
		else if(source == m_bArea){
			updateValueArea(m_ws.calculateArea());
			m_ws.m_geom.update(m_ws.m_geom);
			return;
		}
		else if(source == m_bCurvature){
			updateCurvatureValues(m_ws.calculateStatistics(m_ws.calculateMeanCurvature()));
			m_ws.m_geom.update(m_ws.m_geom);
			return;
		}
		else if(source == m_bItterSmooth){
			String s1 = smoothing_iteration_iterative.getText();
			String s2 = smoothing_parameter_iterative.getText();
			m_ws.surfaceSmoothIter(Integer.parseInt(s1), Double.parseDouble(s2));
			m_ws.m_geom.update(m_ws.m_geom);
			return;
		}
		else if(source == m_bMeanCurvatureSmooth){
			String s1 = smoothing_iteration_curvature.getText();
			String s2 = smoothing_parameter_curvature.getText();
			m_ws.meanCurvaturSmooth(Integer.parseInt(s1), Float.parseFloat(s2));
			m_ws.m_geom.update(m_ws.m_geom);
			return;
		}
	}
	
	public void updateValues(double[] statistics){
		m_mean.setText("" + new DecimalFormat("##.##").format(statistics[0]));
		m_min.setText("" + new DecimalFormat("##.##").format(statistics[1]));
		m_max.setText("" + new DecimalFormat("##.##").format(statistics[2]));
		m_sd.setText("" + new DecimalFormat("##.##").format(statistics[3]));
	}
	
	public void updateCurvatureValues(double[] statistics){
		m_c_mean.setText("" + new DecimalFormat("##.##").format(statistics[0]));
		m_c_min.setText("" + new DecimalFormat("##.##").format(statistics[1]));
		m_c_max.setText("" + new DecimalFormat("##.##").format(statistics[2]));
		m_c_sd.setText("" + new DecimalFormat("##.##").format(statistics[3]));
	}

	public void updateValueGenus(int genus){
		m_genus.setText("" + genus);
	}

	public void updateValueArea(double area){
		m_area.setText("" + new DecimalFormat("##.##").format(area));
	}
	
}
