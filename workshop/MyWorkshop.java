package workshop;

import java.awt.Color;
import java.util.ArrayList;
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

	public void makeElementColors(double[] statistics, double[] entries) {
		double mean = statistics[0];
		double min = statistics[1];
		double max = statistics[2];
		double std = statistics[3];

		double[] normalizedValues = new double[entries.length];

		for(int i=0;i<entries.length;i++) {
			normalizedValues[i] = (entries[i] - min) / max;
		}

		//assure that the color array is allocated
		m_geom.assureElementColors();

		Color color;

		int noe = m_geom.getNumElements();
		for(int i=0; i<noe; i++){
			color = Color.getHSBColor((float)normalizedValues[i], 1.0f, 1.0f);
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
        double[] statistics = calculateStatistics(shapeRegularities);
		makeElementColors(statistics, shapeRegularities);
		return statistics;
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
		double[] result = new double[vv.getSize()];

		for(int i = 0; i < vv.getSize(); i++) {
			result[i] = vv.getEntry(i);
		}

		return calculateStatistics(result);
	}
	
	//three angles of all triangles
	public double[] calculateAngles(){
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
        return calculateStatistics(angles);
	}
	
	//length of all edges
	public double[] calculateLengthEdges(){
		//PsDebug.warning("")
		double[] lengths = new double[m_geom.getNumEdges()];
		//System.out.println(m_geom.getEdgeStar(0));

		for(int i=0; i< m_geom.getNumEdgeStars(); i++){
			PgEdgeStar edgeStar = m_geom.getEdgeStar(i);
			lengths[i] = calculateDistance(m_geom.getVertex(edgeStar.getVertexInd(0)),m_geom.getVertex(edgeStar.getVertexInd(1)));
		}
		return calculateStatistics(lengths);
	}

	public double calculateDistance(PdVector point1,PdVector point2){
		double x = Math.pow(point1.getEntry(0) - point2.getEntry(0), 2);
		double y = Math.pow(point1.getEntry(1) - point2.getEntry(1),2);
		double z = Math.pow(point1.getEntry(2) - point2.getEntry(2),2);
		return Math.sqrt(x + y + z);
	}
	// ---- Surface Analysis
	
	//genus of surface
	public int calculateGenus(){
		int V = m_geom.getNumVertices();
		int E = m_geom.getNumEdges();
		int F = m_geom.getNumElements();

		return -(((V-E+F) / 2) - 1);
	}

	//area of surface
	public double calculateArea(){
		return m_geom.getArea();
	}


	//iterative averaging
	public void surfaceSmoothIter(int iters) {
		//PiVector[] neighbours = m_geom.getNeighbours();
		System.out.println("num vertices: " + m_geomSave.getNumVertices());
		System.out.println("num elements: " + m_geomSave.getNumElements());

		int maxValence = (int)calculateStatistics(calculateValence())[2];
		int[][] neighbours = new int[m_geomSave.getNumVertices()][maxValence];
		int[] sizes = new int[m_geomSave.getNumVertices()];
		for(int i = 0; i< sizes.length; i++){
			sizes[i] = 0;
		}

		for(int i=0; i< m_geomSave.getNumEdgeStars(); i++){
			PgEdgeStar edgeStar = m_geomSave.getEdgeStar(i);
			int point1 = edgeStar.getVertexInd(0);
			int point2 = edgeStar.getVertexInd(1);
			int sizePoint1 = sizes[point1];
			int sizePoint2 = sizes[point2];
			neighbours[point1][sizePoint1] = point2;
			neighbours[point2][sizePoint2] = point1;
			sizes[point1]++;
			sizes[point2]++;
		}

		for (int iter = 0; iter < iters; iter++) {

			for (int i = 0; i < neighbours.length; i++) {
				PdVector vertex1 = m_geomSave.getVertex(i);
				PdVector temp = new PdVector(0, 0, 0);

				for (int j = 0; j < sizes[i]; j++) {
					int index = neighbours[i][j];
					PdVector neighbour = m_geomSave.getVertex(index);
					temp.add(neighbour);
				}

				double x = temp.getEntry(0) / sizes[i];
				double y = temp.getEntry(1) / sizes[i];
				double z = temp.getEntry(2) / sizes[i];
				PdVector newVertex = new PdVector(x, y, z);
				newVertex.min(vertex1);
				m_geom.setVertex(i, newVertex);
			}
		}
		m_geomSave = m_geom;
	}
}