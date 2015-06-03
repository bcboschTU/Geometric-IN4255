package workshop;

import jv.geom.PgElementSet;
import jv.project.PgGeometry;
import jv.vecmath.PdMatrix;
import jv.vecmath.PdVector;
import jv.vecmath.PiVector;
import jvx.numeric.PnSparseMatrix;
import jvx.project.PjWorkshop;
import jv.object.PsDebug;

import java.util.ArrayList;


public class MyWorkshopAssignment2 extends PjWorkshop {

	PgElementSet m_geom;
	PgElementSet m_geomSave;
	PgElementSet m_geomReset;

	public MyWorkshopAssignment2() {
		super("Geometric Modeling Practical 2");
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

	public void reset() {
		for(int i = 0; i<m_geomReset.getNumVertices();i++) {
			m_geom.setVertex(i, m_geomReset.getVertex(i));
		}
		m_geomSave = m_geom;
	}

	// e1 = p3 - p2
	// e2 = p1 - p3
	// e3 = p2 - p1
	// R =	( 0 , -1)
	//		( 1 , 0 )
	// re1 = R . e1
	// re2 = R . e2
	// re3 = R . e3
	//G = Transpose[1/2area(T) * {re1, re2, re3} ]
	private PnSparseMatrix calculateGradient(PiVector element) {
		PnSparseMatrix R = new PnSparseMatrix(3, 3, 3);

		PdVector p1 = m_geom.getVertex(element.getEntry(0));
		PdVector p2 = m_geom.getVertex(element.getEntry(1));
		PdVector p3 = m_geom.getVertex(element.getEntry(2));

		//http://math.stackexchange.com/questions/361412/finding-the-angle-between-three-points
		PdVector e3 = PdVector.subNew(p2, p1);
		PdVector e2 = PdVector.subNew(p1, p3);
		PdVector e1 = PdVector.subNew(p3, p2);
		double a1 = Math.acos(PdVector.dot(e3, e1) / (e3.length() * e1.length())); //in rad
		double a2 = Math.acos(PdVector.dot(e2, e3) / (e2.length() * e3.length())); //in rad
		double a3 = Math.acos(PdVector.dot(e1, e2) / (e1.length() * e2.length())); //in rad

		// Convert vector to matrix
		PdVector Re1 = PdVector.copyNew(e2);
		Re1.multScalar(1 / Math.cos(a2));
		PdVector temp1 = PdVector.copyNew(e3);
		temp1.multScalar(1 / Math.cos(a3));
		Re1.sub(temp1);

		PdVector Re2 = PdVector.copyNew(e3);
		Re2.multScalar(1 / Math.cos(a3));
		PdVector temp2 = PdVector.copyNew(e1);
		temp2.multScalar(1 / Math.cos(a1));
		Re2.sub(temp2);

		PdVector Re3 = PdVector.copyNew(e1);
		Re3.multScalar(1 / Math.cos(a1));
		PdVector temp3 = PdVector.copyNew(e2);
		temp3.multScalar(1 / Math.cos(a2));
		Re3.sub(temp3);

		double sideA = calculateDistance(p1, p2);
		double sideB = calculateDistance(p2, p3);
		double sideC = calculateDistance(p3, p1);
		double s = 0.5 * (sideA + sideB + sideC);
		double area = Math.sqrt(s*(s-sideA)*(s-sideB)*(s-sideC));

		double f = 1/(2*area);

		R.setEntry(0,0,f*Re1.getEntry(0));
		R.setEntry(0,1,f*Re1.getEntry(1));
		R.setEntry(0,2,f*Re1.getEntry(2));
		R.setEntry(1,0,f*Re2.getEntry(0));
		R.setEntry(1,1,f*Re2.getEntry(1));
		R.setEntry(1,2,f*Re2.getEntry(2));
		R.setEntry(2,0,f*Re3.getEntry(0));
		R.setEntry(2,1,f*Re3.getEntry(1));
		R.setEntry(2,2,f*Re3.getEntry(2));

		return R;
	}

	private double calculateDistance(PdVector point1,PdVector point2){
		double x = Math.pow(point1.getEntry(0) - point2.getEntry(0), 2);
		double y = Math.pow(point1.getEntry(1) - point2.getEntry(1), 2);
		double z = Math.pow(point1.getEntry(2) - point2.getEntry(2),2);
		return Math.sqrt(x + y + z);
	}

	/*
	Implement a method that computes the sparse matrix G, which maps a continuous
	linear polynomial over all triangles of a mesh to its gradient vectors.
			*/
	public PnSparseMatrix calculateLinearPolynomialGradients() {
		int n = 3 * (2 - m_geom.getNumVertices() + m_geom.getNumEdges());
		int m = m_geom.getNumVertices();
		PnSparseMatrix G = new PnSparseMatrix(n, m, 3);

		PiVector [] elements = m_geom.getElements();
		PiVector element = elements[0];

		PsDebug.message(calculateGradient(element).toShortString());


		/*You can use the method addEntry(int k, int l, double value) for
		constructing the matrix. The method adds value at position k; l in the
		matrix. If the matrix entry with k; l does not exist, the space for storing
		it is created.

		For multiplication of sparse matrices you can use:
		AB = PnSparsematrix.multMatrices(A,B, null);

		For multiplication of a sparse matrix and a vector you can use:
		Av = PnSparsematrix.rightMultVector(A, v, null);.
		This method generates an new PdVector that is the product of the matrix and the vector. If a
		PdVector w for storing the result is already allocated, use
		PnSparsematrix:rightMultVector(A, v,w);. The method then additionally
		returns a reference to w.*/

		return G;
	}

	/*
	As a first step, compute the 3x3 matrix that maps a linear polynomial
	over a triangle to its gradient vector. Then use this method to compute the
	matrix G for a triangle mesh.
	*/
	private PnSparseMatrix mapTriangleToGradient(PdVector a, PdVector b, PdVector c) {
		PnSparseMatrix matrix = new PnSparseMatrix(3, 3, 3);

		return matrix;
	}

	/*
	Implement a tool for editing triangle meshes (a simplified version of the
	brushes tool we discussed in the lecture). It should allow to specify a 3x3
	matrix A, which is applied to the gradient vectors of all selected triangles
	of the mesh. Then, the vertex positions of the mesh are modified such that
	the gradient vectors of the new mesh are as-close-as-possible (in the least-
	squares sense) to the modified gradients.

	Remark: The vertex positions is only determined up to translations of the
	whole mesh in R^3. You can deal with this by keeping the barycenter of the
	mesh constant.
	*/
	public void editTriangleMesh(PnSparseMatrix a) {
		/*For solving the sparse linear systems (Task 2), you can use
		dev6.numeric.PnMumpsSolver. This class offers an interface to the direct
		solvers of the MUMPS library. To solve the system Ax = b, you can use the
		method solve(A, x, b, PnMumpsSolver.Type.GENERAL SYMMETRIC).
		For solving a number of systems with the same matrix, compute the factorization once using:
		- public static long factor(PnSparseMatrix matrix, Type sym)
		and use
		- public static native void solve(long factorization, PdVector x, PdVector b)
		for solving the systems.
		The MUMPS library should work on WINDOWS 64-bit systems. For MAC,
		I added the file libMumpsJNI.jnilib to blackboard. Please copy the le to
		the "dll"folder. This file may not work on your MAC, because it depends
		on other libraries including gcc and gfortran.
		If the MUMPS library does not work on your system, you can use
		jvx.numeric.PnBiconjugateGradient instead. However, this is less efficient (do not use too large meshes in this case).*/

		PsDebug.message(a.toString());
	}
}