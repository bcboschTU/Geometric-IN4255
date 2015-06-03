package workshop;

import jv.geom.PgElementSet;
import jv.project.PgGeometry;
import jv.vecmath.PdMatrix;
import jv.vecmath.PdVector;
import jv.vecmath.PiVector;
import jvx.numeric.PnSparseMatrix;
import jvx.project.PjWorkshop;
import jv.object.PsDebug;


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
	private void calculateGradient(PiVector element) {
		PdVector[] verticesOfElement = new PdVector[element.getSize()];
		for (int i = 0; i < element.getSize(); i++) {
			verticesOfElement[i] = m_geom.getVertex(element.getEntries()[i]);
		}
		double[][] test = new double[3][3];

		PdMatrix R = new PdMatrix(test);

		PdVector[] edges = new PdVector[3];
		if (verticesOfElement.length == 3) {
			edges[0] = PdVector.copyNew(verticesOfElement[2]);
			edges[0].min(verticesOfElement[1]);
			edges[1] = PdVector.copyNew(verticesOfElement[0]);
			edges[1].min(verticesOfElement[2]);
			edges[2] = PdVector.copyNew(verticesOfElement[1]);
			edges[2].min(verticesOfElement[0]);
		}
		// Convert vector to matrix
		double[][] temp = new double[1][3];
		temp[0] = edges[0].getEntries();
		PdMatrix edgeMatrix0 = new PdMatrix(temp);

		R.leftMult(edgeMatrix0);
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

		calculateGradient(element);


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