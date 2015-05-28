package workshop;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import jv.number.PuDouble;

import jv.object.PsUpdateIf;
import jvx.project.PjWorkshop_IP;

public class MyWorkshopAssignment2_IP extends PjWorkshop_IP implements ActionListener {

	protected Button m_bReset;

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

	MyWorkshopAssignment2 m_ws;
	
	public MyWorkshopAssignment2_IP() {
		super();
		if(getClass() == MyWorkshopAssignment2_IP.class)
			init();
	}
	
	public void init() {
		super.init();
		setTitle("Geometric Modeling Practical 2");
	}
	
	public String getNotice() {
		return "Practical Assignment 2";
	}
	
	public void setParent(PsUpdateIf parent) {
		super.setParent(parent);
		m_ws = (MyWorkshopAssignment2)parent;
		
		addSubTitle("Gradients of Linear Polynomial:");
		
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

		addSubTitle("Mesh editing");
		Panel panel3 = new Panel(new FlowLayout(FlowLayout.LEFT));
		add(panel3);


		Panel resetPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		m_bReset = new Button("Reset model");
		m_bReset.addActionListener(this);
		resetPanel.add(m_bReset);
		add(resetPanel);

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
		if(source == m_bShapeRegularty){
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
		else if(source == m_bReset){
			m_ws.reset();
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
}
