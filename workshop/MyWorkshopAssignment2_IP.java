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
	protected Button m_bSparseMatrix;

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
		
		m_bSparseMatrix = new Button("Compute sparse matrix G");
		m_bSparseMatrix.addActionListener(this);
		Panel panel2 = new Panel(new FlowLayout(FlowLayout.LEFT));
		panel2.add(m_bSparseMatrix);
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
		if(source == m_bSparseMatrix){
			m_ws.CalculateSparseMatrix();
			m_ws.m_geom.update(m_ws.m_geom);
			return;
		}
		else if(source == m_bReset){
			m_ws.reset();
			m_ws.m_geom.update(m_ws.m_geom);
			return;
		}
	}
}
