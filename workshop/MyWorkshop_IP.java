package workshop;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import jv.number.PuDouble;

import jv.object.PsConfig;
import jv.object.PsUpdateIf;
import jvx.project.PjWorkshop_IP;

public class MyWorkshop_IP extends PjWorkshop_IP implements ActionListener {

	protected Button m_bMakeRandomElementColors;
	protected Button m_bMakeRandomVertexColors;
	
	protected Button m_bShapeRegularty;
	protected Button m_bValence;
	protected Button m_bAngles;
	protected Button m_bLengthEdges;
	
	protected PuDouble m_xOff;
	
	protected PuDouble m_mean;
	protected PuDouble m_min;
	protected PuDouble m_max;
	protected PuDouble m_sd;
	
	MyWorkshop m_ws;
	
	public MyWorkshop_IP() {
		super();
		if(getClass() == MyWorkshop_IP.class)
			init();
	}
	
	public void init() {
		super.init();
		setTitle("My Workshop");
	}
	
	public String getNotice() {
		return "Practical Assignment 1";
	}
	
	public void setParent(PsUpdateIf parent) {
		super.setParent(parent);
		m_ws = (MyWorkshop)parent;
	
		addSubTitle("Example functions:");
		
		m_bMakeRandomElementColors = new Button("Random Element Colors");
		m_bMakeRandomElementColors.addActionListener(this);
		m_bMakeRandomVertexColors = new Button("Random Vertex Colors");
		m_bMakeRandomVertexColors.addActionListener(this);
		Panel panel1 = new Panel(new FlowLayout(FlowLayout.CENTER));
		panel1.add(m_bMakeRandomElementColors);
		panel1.add(m_bMakeRandomVertexColors);
		add(panel1);
		
		m_xOff = new PuDouble("X Offset");
		m_xOff.setDefBounds(-10,10,0.1,1);
		m_xOff.addUpdateListener(this);
		m_xOff.init();
		add(m_xOff.getInfoPanel());
		
		addSubTitle("Shape statistics:");
		
		m_bShapeRegularty = new Button("shape regularity");
		m_bShapeRegularty.addActionListener(this);
		m_bValence = new Button(" valence");
		m_bValence.addActionListener(this);
		m_bAngles = new Button("Angles");
		m_bAngles.addActionListener(this);
		m_bLengthEdges = new Button("Length");
		m_bLengthEdges.addActionListener(this);
		Panel panel2 = new Panel(new FlowLayout(FlowLayout.CENTER));
		panel2.add(m_bShapeRegularty);
		panel2.add(m_bValence);
		panel2.add(m_bAngles);
		panel2.add(m_bLengthEdges);
		add(panel2);
		
		m_mean= new PuDouble("Mean:");
		m_mean.addUpdateListener(this);
		m_min= new PuDouble("Min:");
		m_min.addUpdateListener(this);
		m_max= new PuDouble("Max:");
		m_max.addUpdateListener(this);
		m_sd= new PuDouble("Standard Deviation:");
		m_sd.addUpdateListener(this);
		add(m_mean.getInfoPanel());
		add(m_min.getInfoPanel());
		add(m_max.getInfoPanel());
		add(m_sd.getInfoPanel());

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
			updateValues(m_ws.calculateShapeRegularity());
			m_ws.m_geom.update(m_ws.m_geom);
			return;
		}
		else if(source == m_bValence){
			updateValues(m_ws.calculateValence());
			m_ws.m_geom.update(m_ws.m_geom);
			return;
		}
		else if(source == m_bAngles){
			updateValues(m_ws.calculateAngles());
			m_ws.m_geom.update(m_ws.m_geom);
			return;
		}
		else if(source == m_bLengthEdges){
			updateValues(m_ws.calculateLengthEdges());
			m_ws.m_geom.update(m_ws.m_geom);
			return;
		}
	}
	
	public void updateValues(double[] statistics){
		m_mean.setValue(statistics[0]);
		m_min.setValue(statistics[1]);
		m_max.setValue(statistics[2]);
		m_sd.setValue(statistics[3]);
	}
	
}
