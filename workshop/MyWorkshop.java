package workshop;

import java.awt.Color;
import java.util.Random;
import java.util.Arrays;

import jv.geom.PgElementSet;
import jv.project.PgGeometry;
import jv.vecmath.PdVector;
import jv.vecmath.PiVector;
import jv.geom.PgEdgeStar;
import jvx.project.PjWorkshop;
import jv.object.PsDebug;

public class MyWorkshop extends PjWorkshop {

	PgElementSet m_geom;
	PgElementSet m_geomSave;
	
	public MyWorkshop() {
		super("My Workshop");
		init();
	}
	
	@Override
	public void setGeometry(PgGeometry geom) {
		super.setGeometry(geom);
		m_geom 		= (PgElementSet)super.m_geom;
		m_geomSave 	= (PgElementSet)super.m_geomSave;
		m_geom.allocateEdgeStars();
		m_geomSave.allocateEdgeStars();
	}
	
	public void init() {		
		super.init();
	}
	
	public void makeRandomElementColors() {
		//assure that the color array is allocated
		m_geom.assureElementColors();
		
		Random rand = new Random();
		Color randomColor;
		
		int noe = m_geom.getNumElements();
		for(int i=0; i<noe; i++){
			randomColor = Color.getHSBColor(rand.nextFloat(), 1.0f, 1.0f);//new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
			m_geom.setElementColor(i, randomColor);
		}
		m_geom.showElementColorFromVertices(false);
		m_geom.showElementColors(true);	
		m_geom.showSmoothElementColors(false);
	}
	
	public void makeRandomVertexColors() {
		//assure that the color array is allocated
		m_geom.assureVertexColors();
		
		Random rand = new Random();
		Color randomColor;
		
		int nov = m_geom.getNumVertices();
		for(int i=0; i<nov; i++){
			randomColor = Color.getHSBColor(rand.nextFloat(), 1.0f, 1.0f);
			m_geom.setVertexColor(i, randomColor);
		}
		
		m_geom.showElementColors(true);	
		m_geom.showVertexColors(true);
		m_geom.showElementColorFromVertices(true);	
		m_geom.showSmoothElementColors(true);
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
		PdVector [] vertices = m_geom.getVertices();

		for(int i = 0; i < elements.length; i++) {
			for(int j = 0; j < elements[i].getSize(); j++) {
				System.out.println("Elements, index: " + i + ", index in element: " + j + ", value: " + elements[i].getEntry(j));
			}
		}

		int elementCounter = 0;
		int pointCounter = 0;
		for(int i = 0; i < m_geom.getNumElements(); i++){
			double currentSmallestAngle = Double.MAX_VALUE;
			for(int j = 0; j < elements[i].getSize(); j++) {
				if(m_geom.getVertexAngle(i, j) < currentSmallestAngle) {
					currentSmallestAngle = m_geom.getVertexAngle(i, j);
				}
				pointCounter++;
			}
			System.out.println("Element: index: " + i + ", angle: " + currentSmallestAngle);

			elementCounter = pointCounter;
		}
		return null;
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

		return new double[]{mean, min, max, std};
	}

	// ---- Mesh Analysis
	
	//number of adjacent edges
	public double[] calculateValence(){
		PiVector vv = PgElementSet.getVertexValence(m_geom);
		double[] result = new double[vv.getSize()];

		for(int i = 0; i < vv.getSize(); i++) {
			result[i] = vv.getEntry(i);
		}

		return calculateStatistics(result);
	}
	
	//three angles of all triangles
	public double[] calculateAngles(){
		return null;
	}
	
	//length of all edges
	public double[] calculateLengthEdges(){
		//PsDebug.warning("")
		double[] lengths = new double[m_geom.getNumEdges()];
		System.out.println(m_geom.getEdgeStar(0));

		for(int i=0; i< m_geom.getNumEdgeStars(); i++){
			PgEdgeStar edgeStar = m_geom.getEdgeStar(i);
			lengths[i] = edgeStar.getLength();
		}

		return calculateStatistics(lengths);
	}

	// ---- Surface Analysis
	
	//genus of surface
	public int calculateGenus(){
		int V = m_geom.getNumVertices();
		int E = m_geom.getNumEdges();
		int F = m_geom.getNumElements();


		PsDebug.warning("V:" + V);
		PsDebug.warning("E:" + E);
		PsDebug.warning("F:" + F);

		return -(((V-E+F) / 2) - 1);
	}

	//area of surface
	public double calculateArea(){
		return m_geom.getArea();
	}
}