package workshop;

import java.awt.Color;
import java.util.Random;

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
	
	//number of adjacent edges
	public double[] calculateValence(){
		return null;
	}
	
	//three angles of all triangles
	public double[] calculateAngles(){
		return null;
	}
	
	//length of all edges
	public double[] calculateLengthEdges(){
		//PsDebug.warning("")
		/*
        PdVector edges = m_geom.getEdgeSizes();
		double[] m_date = edges.getEntries();
		for(int i=0; i<m_date.size(); i++){
			PgEdgeStar edge = m_geomSave.getEdge(i);
			edge.toString();
		}
         */
		return null;
	}
	
	
	
}