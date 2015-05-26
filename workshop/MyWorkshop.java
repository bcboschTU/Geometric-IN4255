package workshop;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Arrays;
import java.util.Collections;
import java.lang.Math;

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
	PgElementSet m_geomReset;
	
	public MyWorkshop() {
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

	// Color elements based on logaritmic scale
	public void makeElementColors(double[] entries) {
		double[] statistics = calculateStatistics(entries);
		double min = Math.log10(statistics[1]);
		min = Double.isNaN(min) || (min < 0) ? 0 : min;
		double max = Math.log10(statistics[2]);

		double[] normalizedValues = new double[entries.length];

		for(int i=0;i<entries.length;i++) {
			normalizedValues[i] = ((entries[i]) - min) / (max-min);
		}

		//assure that the color array is allocated
		m_geom.assureElementColors();

		Color color;

		int noe = m_geom.getNumElements();
		for(int i=0; i<noe; i++){
			color = Color.getHSBColor(0.0f, 0.0f, (float)normalizedValues[i]);
			m_geom.setElementColor(i, color);
		}
		m_geom.showElementColorFromVertices(false);
		m_geom.showElementColors(true);
		m_geom.showSmoothElementColors(false);
	}
	
	// Color vertices based on logaritmic scale
	public void makeVertexColors(double[] entries) {
		double[] statistics = calculateStatistics(entries);
		double min = Math.log10(statistics[1]);
		min = Double.isNaN(min) || (min < 0) ? 0 : min;
		double max = Math.log10(statistics[2]);

		double[] normalizedValues = new double[entries.length];

		for(int i=0;i<entries.length;i++) {
			normalizedValues[i] = (Math.log10(entries[i]) - min) / (max-min);
		}

		//assure that the color array is allocated
		m_geom.assureVertexColors();
		
		Color color;
		
		int nov = m_geom.getNumVertices();
		for(int i=0; i<nov; i++){
			color = Color.getHSBColor(0.0f, 0.0f, (float)normalizedValues[i]);
			m_geom.setVertexColor(i, color);
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

	//absolute mean curvature
	public double[] calculateMeanCurvature(){
		NeighboursSizesPair nsp = createNeighbours();
		int[][] neighbours = nsp.neighbours;
		int[] sizes = nsp.sizes;

		double[] meanCurvature = new double[neighbours.length];

		PdVector[] meanCurvatureVectors = calculateMeanCurvatureVectors();
		for(int i = 0; i < meanCurvatureVectors.length; i++) {
			meanCurvature[i] = Double.isNaN(meanCurvatureVectors[i].length()) ? 0 : meanCurvatureVectors[i].length();
		}
		//PsDebug.message("Coloring object...");
		makeVertexColors(meanCurvature);
		//PsDebug.message("Done!");

		return meanCurvature;
	}

	private List<Integer> createIntList(int[] array) {
		List<Integer> list = new ArrayList<Integer>();
	    for (int index = 0; index < array.length; index++)
	    {
	        list.add(array[index]);
	    }
	    return list;
	}

	//Create neighbours array, works better than standard getNeighbours().
	private NeighboursSizesPair createNeighbours() {
		int maxValence = (int) calculateStatistics(calculateValence())[2];
		int[][] neighbours = new int[m_geom.getNumVertices()][maxValence];
		int[] sizes = new int[m_geom.getNumVertices()];
		for(int i = 0; i< sizes.length; i++){
			sizes[i] = 0;
		}

		for(int i=0; i< m_geom.getNumEdgeStars(); i++){
			PgEdgeStar edgeStar = m_geom.getEdgeStar(i);
			int point1 = edgeStar.getVertexInd(0);
			int point2 = edgeStar.getVertexInd(1);
			int sizePoint1 = sizes[point1];
			int sizePoint2 = sizes[point2];
			neighbours[point1][sizePoint1] = point2;
			neighbours[point2][sizePoint2] = point1;
			sizes[point1]++;
			sizes[point2]++;
		}

		/*for(int i=0;i<neighbours.length;i++) {
			String msg = "Vertex["+i+"]:";
			for(int j=0;j<sizes[i];j++) {
				msg += " "+neighbours[i][j];
			}
			PsDebug.message(msg);
		}*/

		return new NeighboursSizesPair(neighbours, sizes);
	}

	//iterative averaging
	public void surfaceSmoothIter(int iters, double scalar) {
		//PiVector[] neighbours = m_geom.getNeighbours();
		System.out.println("num vertices: " + m_geomSave.getNumVertices());
		System.out.println("num elements: " + m_geomSave.getNumElements());

		NeighboursSizesPair nsp = createNeighbours();
		int[][] neighbours = nsp.neighbours;
		int[] sizes = nsp.sizes;

		for (int iter = 0; iter < iters; iter++) {

			for (int i = 0; i < neighbours.length; i++) {
				PdVector vertex1 = m_geomSave.getVertex(i);
				PdVector temp = new PdVector(0, 0, 0);

				for (int j = 0; j < sizes[i]; j++) {
					int index = neighbours[i][j];
					PdVector neighbour = m_geomSave.getVertex(index);
					temp.add(neighbour);
				}

				double x = vertex1.getEntry(0) + scalar*((temp.getEntry(0) / sizes[i]) - vertex1.getEntry(0));
				double y = vertex1.getEntry(1) + scalar*((temp.getEntry(1) / sizes[i]) - vertex1.getEntry(1));
				double z = vertex1.getEntry(2) + scalar*((temp.getEntry(2) / sizes[i]) - vertex1.getEntry(2));
				PdVector newVertex = new PdVector(x, y, z);

				//PdVector ret = vertex1;
				//ret.add(newVertex);
				m_geom.setVertex(i, newVertex);
			}
		}
		m_geomSave = m_geom;
	}

	void meanCurvaturSmooth(int iters, float scalar){
		for(int iter = 0; iter< iters; iter++) {
			PdVector[] vectorsMeanCur = calculateMeanCurvatureVectors();
			double[] vectorLenghts = calculateMeanCurvature();
			double max = 0;

			for (int i = 0; i < vectorLenghts.length; i++) {
				if (vectorLenghts[i] > max)
					max = vectorLenghts[i];
			}
			for (int i = 0; i < vectorLenghts.length; i++) {
				vectorLenghts[i] = vectorLenghts[i] / max;
			}

			for (int i = 0; i < vectorLenghts.length; i++) {
				PdVector vertex1 = m_geomSave.getVertex(i);
				PdVector meanCurVer = vectorsMeanCur[i];
				double length = vectorLenghts[i];

				double x, y, z;
				if (length >= 0.00001) {
					x = vertex1.getEntry(0) + (scalar * length) * (meanCurVer.getEntry(0));
					y = vertex1.getEntry(1) + (scalar * length) * (meanCurVer.getEntry(1));
					z = vertex1.getEntry(2) + (scalar * length) * (meanCurVer.getEntry(2));
				} else {
					x = vertex1.getEntry(0);
					y = vertex1.getEntry(1);
					z = vertex1.getEntry(2);
				}

				PdVector ret = new PdVector(x, y, z);

				m_geom.setVertex(i, ret);

			}
			m_geomSave = m_geom;
		}
	}

	public PdVector[] calculateMeanCurvatureVectors(){
		NeighboursSizesPair nsp = createNeighbours();
		int[][] neighbours = nsp.neighbours;
		int[] sizes = nsp.sizes;
		PdVector[] meanCurvature = new PdVector[neighbours.length];
		// For each vertex that has neighbours..
		for(int i = 0; i < neighbours.length; i++){
			PdVector vertex = m_geom.getVertex(i);
			PdVector mcv = new PdVector();

			double area = 0;
			int numTriangles = 0;
			PdVector previous = null;
			PdVector neighbour = null;
			// For each neighbour of the vertex
			for(int j = 0; j < sizes[i]; j++) {
				// Calculate AREA
				if (previous != null) {
					double sideA = calculateDistance(vertex, previous);
					double sideB = calculateDistance(vertex, neighbour);
					double sideC = calculateDistance(neighbour, previous);
					double s = 0.5 * (sideA + sideB + sideC);
					area += Math.sqrt(s*(s-sideA)*(s-sideB)*(s-sideC));
					numTriangles++;
				}
				if (neighbour != null) {
					previous = neighbour;
				}
				neighbour = m_geom.getVertex(neighbours[i][j]);

				// Create list of common neighbours
				List<Integer> commonNeighbours = createIntList(neighbours[i]);
				List<Integer> nbNeighbours = createIntList(neighbours[neighbours[i][j]]);
				commonNeighbours.retainAll(nbNeighbours);
				commonNeighbours.removeAll(Collections.singleton(0));

				// Calculate ANGLES
				ArrayList<Double> angles = new ArrayList<Double>();
				for(int k = 0; k < commonNeighbours.size(); k++) {
					PdVector third = m_geom.getVertex(commonNeighbours.get(k).intValue());
					//http://math.stackexchange.com/questions/361412/finding-the-angle-between-three-points
					PdVector ab = PdVector.subNew(third, vertex);
					PdVector bc = PdVector.subNew(neighbour, third);
					double dot = PdVector.dot(ab, bc);
					double angle = Math.acos(dot / (ab.length() * bc.length())); //in rad
					if(angle >= 0) {
						angles.add(angle);
						//PsDebug.message("neighbour: "+neighbours[i][j]+" third: "+commonNeighbours.get(k).intValue()+" angle: "+angle);
					}
				}

				//If vertex and neighbour have 2 common neighbours
				//PsDebug.message("Vector "+i+": "+angles.size()+" angles");
				if(angles.size() == 2) {
					//First create the sum vector
					//Slide 39, lecture 3, sum((cotaij + cotbij) * (xi - xj))
					PdVector ximinusxj = PdVector.subNew(vertex, neighbour);
					ximinusxj.multScalar(((1/Math.tan(angles.get(0))) + (1/Math.tan(angles.get(1)))));
					mcv.add(ximinusxj);
				}
			}
			//Now scale to the area of the vertex and its neighbours
			//Slide 39, lecture 3, 3/2area(star(xi))
			//PsDebug.message("Area based on "+numTriangles+" triangles: "+area);
			mcv.multScalar(3 / (2*area));

			//Save the absolute value of the mean curvature vector
			meanCurvature[i] = mcv;
			//PsDebug.message(i+": "+absmcv);
		}
		return meanCurvature;
	}

	// Helper class to combine return values
	public class NeighboursSizesPair {
	    public final int[][] neighbours;
	    public final int[] sizes;

	    public NeighboursSizesPair(int[][] neighbours, int[] sizes) {
	        this.sizes = sizes;
	        this.neighbours = neighbours;
	    }
	}
}