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
import jvx.numeric.PnSparseMatrix;
import jv.geom.PgEdgeStar;
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

	/*
	Implement a method that computes the sparse matrix G, which maps a continuous
	 linear polynomial over all triangles of a mesh to its gradient vectors.
	*/
	public PnSparseMatrix calculateLinearPolynomialGradients() {
		int n = 2 - m_geom.getNumVertices() + m_geom.getNumEdges();
		int m = m_geom.getNumVertices();
		PnSparseMatrix G = new PnSparseMatrix(n, m, 3);

		// for each triangle
		int[][] triangles = getTriangles();
		for (int i = 0; i < triangles.length; i++) {
			int[] triangle = triangles[i];
			PsDebug.message(triangle[1] + " " + triangle[2] + " " + triangle[3]);
			PdVector a = m_geom.getVertex(triangle[1]);
			PdVector b = m_geom.getVertex(triangle[2]);
			PdVector c = m_geom.getVertex(triangle[3]);
			PnSparseMatrix triangleLP = mapTriangleToGradient(a, b, c);

			// TODO do magic with this
		}

		return G;

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

	// Get a Nx3 array of vertex indices which form triangles.
	private int[][] getTriangles() {
		int[][] triangles = new int[2 - m_geom.getNumVertices() + m_geom.getNumEdges()][3];

		// TODO create triangles somehow

		return triangles;
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