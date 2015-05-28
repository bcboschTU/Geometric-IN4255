package workshop;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;
import java.lang.Math;

import jv.geom.PgElementSet;
import jv.project.PgGeometry;
import jv.vecmath.PdVector;
import jv.vecmath.PiVector;
import jv.geom.PgEdgeStar;
import jvx.project.PjWorkshop;

public class MyWorkshopAssignment2 extends PjWorkshop {

	PgElementSet m_geom;
	PgElementSet m_geomSave;
	PgElementSet m_geomReset;
	
	public MyWorkshopAssignment2() {
		super("Geometric Modeling Practical 1");
		init();
	}
	
	@Override
	public void setGeometry(PgGeometry geom) {
		super.setGeometry(geom);
		m_geom 		= (PgElementSet)super.m_geom;
		m_geomSave 	= (PgElementSet)super.m_geomSave;
		m_geomReset	= (PgElementSet) geom.clone();
		m_geom.allocateEdgeStars();
		m_geomSave.allocateEdgeStars();
	}
	
	public void init() {		
		super.init();
	}

	public void reset() {
		for(int i = 0; i<m_geomReset.getNumVertices();i++) {
			m_geom.setVertex(i, m_geomReset.getVertex(i));
		}
		m_geomSave = m_geom;
	}

	// Color elements based on logaritmic scale
	public void makeElementColors(double[] entries) {
		double[] statistics = calculateStatistics(entries);
		double min = Math.log10(statistics[1]);
		min = Double.isNaN(min) || (min < 0) ? 0 : min;
		double max = Math.log10(statistics[2]);

		double[] normalizedValues = new double[entries.length];

		for (int i = 0; i < entries.length; i++) {
			normalizedValues[i] = ((entries[i]) - min) / (max - min);
		}

		//assure that the color array is allocated
		m_geom.assureElementColors();

		Color color;

		int noe = m_geom.getNumElements();
		for (int i = 0; i < noe; i++) {
			color = Color.getHSBColor(0.0f, 0.0f, (float) normalizedValues[i]);
			m_geom.setElementColor(i, color);
		}
		m_geom.showElementColorFromVertices(false);
		m_geom.showElementColors(true);
		m_geom.showSmoothElementColors(false);
	}
	
	public void setXOff(double xOff) {
		int nov = m_geom.getNumVertices();
		PdVector v = new PdVector(3);
		// the double array is v.m_data 
		for (int i=0; i<nov; i++) {
			v.copyArray(m_geomSave.getVertex(i));
			v.setEntry(0, v.getEntry(0)+xOff);
			m_geom.setVertex(i, v);
		}
	}
	
	//ratio between the inscribed and the circumscribe circle
	public double[] calculateShapeRegularity(){
		PiVector [] elements = m_geom.getElements();
        double[] shapeRegularities = new double[elements.length];
		for(int i = 0; i < m_geom.getNumElements(); i++){

			double currentSmallestAngle = Double.MAX_VALUE;
            for (int j = 0; j < elements[i].getEntries().length ; j++) {
                double angle = m_geom.getVertexAngle(i, j);
                if (currentSmallestAngle > angle && angle != 0.0) {
                    currentSmallestAngle = angle;
                }
            }
            // Calculate shape regularity
            double shapeRegularity = 2 / Math.sin(Math.toRadians(currentSmallestAngle));
            shapeRegularities[i] = shapeRegularity;
		}

		makeElementColors(shapeRegularities);
		
		return shapeRegularities;
	}

	//mean,min,max,std calculation from dataset
	public double[] calculateStatistics(double[] values) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		double sum = 0;

		for(int i = 0; i < values.length; i++){
			if(values[i] < min) {
				min = values[i];
			}
			if(values[i] > max) {
				max = values[i];
			}
			sum += values[i];
		}

		double mean = sum / values.length;
		
		double sumsq = 0;
		for(int i = 0; i < values.length; i++){
			sumsq += Math.pow(values[i] - mean, 2);
		}
		double std = Math.sqrt(sumsq / (values.length - 1));

        //PsDebug.message("Min: "+min);
        //PsDebug.message("Max: "+max);
        //PsDebug.message("Mean: "+mean);
        //PsDebug.message("Std: "+std);

		return new double[]{mean, min, max, std};
	}

	//mean,min,max,std calculation from dataset
	public double[] calculateStatistics(double[][] values) {
		int total = 0;
		for(int i = 0; i < values.length; i++){
			total += values[i].length;
		}
		double[] newValues = new double[total];
		int index = 0;
		for(int i = 0; i < values.length; i++){
			for(int j = 0; j < values[i].length; j++){
				newValues[index++] = values[i][j];
			}
		}

		return calculateStatistics(newValues);
	}

	// ---- Mesh Analysis
	
	//number of adjacent edges
	public double[] calculateValence(){
		PiVector vv = PgElementSet.getVertexValence(m_geom);
		double[] valence = new double[vv.getSize()];

		for(int i = 0; i < vv.getSize(); i++) {
			valence[i] = vv.getEntry(i);
		}

		return valence;
	}
	
	//three angles of all triangles
	public double[][] calculateAngles(){
		PiVector [] elements = m_geom.getElements();
        double[][] angles = new double[elements.length][];
		for(int i = 0; i < elements.length; i++){
			double[] triangle = new double[elements[i].getEntries().length];
			for (int j = 0; j < elements[i].getEntries().length ; j++) {
                double angle = m_geom.getVertexAngle(i, j);
                triangle[j] = angle;
            }
            angles[i] = triangle;
		}
        return angles;
	}
	
	//length of all edges
	public double[] calculateLengthEdges(){
		double[] lengths = new double[m_geom.getNumEdges()];
		//System.out.println(m_geom.getEdgeStar(0));

		for(int i=0; i< m_geom.getNumEdgeStars(); i++){
			PgEdgeStar edgeStar = m_geom.getEdgeStar(i);
			lengths[i] = calculateDistance(m_geom.getVertex(edgeStar.getVertexInd(0)), m_geom.getVertex(edgeStar.getVertexInd(1)));
		}
		return lengths;
	}

	public double calculateDistance(PdVector point1,PdVector point2){
		double x = Math.pow(point1.getEntry(0) - point2.getEntry(0), 2);
		double y = Math.pow(point1.getEntry(1) - point2.getEntry(1), 2);
		double z = Math.pow(point1.getEntry(2) - point2.getEntry(2),2);
		return Math.sqrt(x + y + z);
	}
}