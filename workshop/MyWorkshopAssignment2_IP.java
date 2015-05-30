package workshop;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import jv.number.PuDouble;
import jvx.numeric.PnSparseMatrix;

import jv.object.PsUpdateIf;
import jvx.project.PjWorkshop_IP;
import jv.object.PsDebug;


public class MyWorkshopAssignment2_IP extends PjWorkshop_IP implements ActionListener {

	protected Button m_bReset;

	protected PuDouble m_xOff;


	// Task 1
	protected Button m_bSparseMatrix;

	// Task 2
	protected Button m_bMeshEditing;
	protected TextField mesh_matrix_1_1;
	protected TextField mesh_matrix_1_2;
	protected TextField mesh_matrix_1_3;
	protected TextField mesh_matrix_2_1;
	protected TextField mesh_matrix_2_2;
	protected TextField mesh_matrix_2_3;
	protected TextField mesh_matrix_3_1;
	protected TextField mesh_matrix_3_2;
	protected TextField mesh_matrix_3_3;

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
		m_bMeshEditing = new Button("Apply matrix A to mesh");
		m_bMeshEditing.addActionListener(this);
		panel3.add(m_bMeshEditing);	
		panel3.add(new Label("Matrix A"));	
		Panel matrix = new Panel(new GridBagLayout());
		GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.EAST;
        GridBagConstraints right = new GridBagConstraints();
        right.weightx = 2.0;
        right.fill = GridBagConstraints.HORIZONTAL;
        right.gridwidth = GridBagConstraints.REMAINDER;
		
		Panel row_1 = new Panel(new FlowLayout(FlowLayout.LEFT));
		Panel row_2 = new Panel(new FlowLayout(FlowLayout.LEFT));
		Panel row_3 = new Panel(new FlowLayout(FlowLayout.LEFT));
		mesh_matrix_1_1 = new TextField("1");
		mesh_matrix_1_2 = new TextField("0");
		mesh_matrix_1_3 = new TextField("0");
		mesh_matrix_2_1 = new TextField("0");
		mesh_matrix_2_2 = new TextField("1");
		mesh_matrix_2_3 = new TextField("0");
		mesh_matrix_3_1 = new TextField("0");
		mesh_matrix_3_2 = new TextField("0");
		mesh_matrix_3_3 = new TextField("1");
		row_1.add(mesh_matrix_1_1);
		row_1.add(mesh_matrix_1_2);
		row_1.add(mesh_matrix_1_3);
		row_2.add(mesh_matrix_2_1);
		row_2.add(mesh_matrix_2_2);
		row_2.add(mesh_matrix_2_3);
		row_3.add(mesh_matrix_3_1);
		row_3.add(mesh_matrix_3_2);
		row_3.add(mesh_matrix_3_3);
		matrix.add(row_1, right);
		matrix.add(row_2, right);
		matrix.add(row_3, right);
		panel3.add(matrix);
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
		else if(source == m_bMeshEditing){
			PsDebug.message(mesh_matrix_1_1.getText());
			PnSparseMatrix a = new PnSparseMatrix(3,3);
			a.setEntry(0,0, Double.parseDouble(mesh_matrix_1_1.getText()));
			a.setEntry(0,1, Double.parseDouble(mesh_matrix_1_2.getText()));
			a.setEntry(0,2, Double.parseDouble(mesh_matrix_1_3.getText()));
			a.setEntry(1,0, Double.parseDouble(mesh_matrix_2_1.getText()));
			a.setEntry(1,1, Double.parseDouble(mesh_matrix_2_2.getText()));
			a.setEntry(1,2, Double.parseDouble(mesh_matrix_2_3.getText()));
			a.setEntry(2,0, Double.parseDouble(mesh_matrix_3_1.getText()));
			a.setEntry(2,1, Double.parseDouble(mesh_matrix_3_2.getText()));
			a.setEntry(2,2, Double.parseDouble(mesh_matrix_3_3.getText()));
			m_ws.editTriangleMesh(a);
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
