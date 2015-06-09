package workshop;

//import workshop.dev6.numeric.PnMumpsSolver;
//import dev6.numeric.PnMumpsSolver;
import jvx.numeric.PnBiconjugateGradient;
import jv.geom.PgElementSet;
import jv.object.PsObject;
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
		m_geom.makeElementNormals();
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
		PdVector normal = m_geom.getElementNormal(0);

		PnSparseMatrix R = new PnSparseMatrix(3, 3, 3);

		PdVector p1 = m_geom.getVertex(element.getEntry(0));
		PdVector p2 = m_geom.getVertex(element.getEntry(1));
		PdVector p3 = m_geom.getVertex(element.getEntry(2));

		PdVector e1 = PdVector.subNew(p3, p2);
		PdVector e2 = PdVector.subNew(p1, p3);
		PdVector e3 = PdVector.subNew(p2, p1);

		double sideA = calculateDistance(p1, p2);
		double sideB = calculateDistance(p2, p3);
		double sideC = calculateDistance(p3, p1);
		double s = 0.5 * (sideA + sideB + sideC);
		double area = Math.sqrt(s*(s-sideA)*(s-sideB)*(s-sideC));

		double scalar = 1/(2*area);

		PdVector e1n = PdVector.crossNew(normal, e1);
		PdVector e2n = PdVector.crossNew(normal, e2);
		PdVector e3n = PdVector.crossNew(normal, e3);

		R.setEntry(0,0, scalar * e1n.getEntry(0));
		R.setEntry(1,0, scalar * e1n.getEntry(1));
		R.setEntry(2,0, scalar * e1n.getEntry(2));

		R.setEntry(0,1, scalar * e2n.getEntry(0));
		R.setEntry(1,1, scalar * e2n.getEntry(1));
		R.setEntry(2,1, scalar * e2n.getEntry(2));

		R.setEntry(0,2, scalar * e3n.getEntry(0));
		R.setEntry(1,2, scalar * e3n.getEntry(1));
		R.setEntry(2,2, scalar * e3n.getEntry(2));

		//TEST FOR CORRECT GRADIENT PER FACE, uncomment to see the result in debug
		//screen. This is commented because it will slow down the making of Big G.
		/*
		PdVector function = new PdVector(3);
		function.setEntry(0, m_geom.getVectorField(0).getVector(0).getEntry(0));
		function.setEntry(1, m_geom.getVectorField(0).getVector(1).getEntry(0));
		function.setEntry(2, m_geom.getVectorField(0).getVector(2).getEntry(0));

		PsDebug.message(Double.toString(m_geom.getVectorField(0).getVector(0).getEntry(0)));
		PsDebug.message(Double.toString(m_geom.getVectorField(0).getVector(1).getEntry(0)));
		PsDebug.message(Double.toString(m_geom.getVectorField(0).getVector(2).getEntry(0)));
		PsDebug.message(R.toString());
		PnSparseMatrix R2 = R;
		PsDebug.message(PnSparseMatrix.rightMultVector(R2, function, null).toString());
		*/

		return R;
	}

	private double calculateDistance(PdVector point1,PdVector point2){
		double x = Math.pow(point1.getEntry(0) - point2.getEntry(0), 2);
		double y = Math.pow(point1.getEntry(1) - point2.getEntry(1), 2);
		double z = Math.pow(point1.getEntry(2) - point2.getEntry(2), 2);
		return Math.sqrt(x + y + z);
	}

	/*
	Implement a method that computes the sparse matrix G, which maps a continuous
	linear polynomial over all triangles of a mesh to its gradient vectors.
	*/
	public PnSparseMatrix calculateLinearPolynomialGradients() {
		int n = 3 * m_geom.getNumElements();
		int m = m_geom.getNumVertices();
		PnSparseMatrix G = new PnSparseMatrix(n, m, 3);

		PiVector [] elements = m_geom.getElements();
		for(int i = 0; i < elements.length ; i++) {
			PnSparseMatrix gradient = calculateGradient(elements[i]);
			PsDebug.message(gradient.toString());

			// Add calculated gradient to the sparse matrix G
			for(int j = 0; j< 3; j++) {
				G.addEntry((3 * i) + j , elements[i].getEntry(0), gradient.getEntry(j, 0));
				G.addEntry((3 * i) + j, elements[i].getEntry(1), gradient.getEntry(j, 1));
				G.addEntry((3 * i) + j, elements[i].getEntry(2), gradient.getEntry(j, 2));
			}
		}

		PsDebug.message("Sparse matrix G:");
		PsDebug.message(G.toString());

		return G;
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
		PnSparseMatrix G = calculateLinearPolynomialGradients();
		PnSparseMatrix G_transpose = new PnSparseMatrix();
		G_transpose.copy(G);
		G_transpose.transpose();

		// Calculate weight matrix, diagonal matrix with area of each triangle on element index
		int n = 3 * m_geom.getNumElements();
		PnSparseMatrix M = new PnSparseMatrix(n, n, 3);
		for (int i = 0; i < m_geom.getNumElements(); i++) {
			M.setEntry((i * 3), (i * 3), m_geom.getAreaOfElement(i));
			M.setEntry((i * 3) + 1, (i * 3) + 1, m_geom.getAreaOfElement(i));
			M.setEntry((i * 3) + 2, (i * 3) + 2, m_geom.getAreaOfElement(i));
		}

		PsDebug.message("Weight matrix M:");
		PsDebug.message(M.toString());

		PiVector[] elements = m_geom.getElements();
		PdVector[] vertices = m_geom.getVertices();
		// Calculate x,y and z gradient vector x_gradiant:
		PdVector x_positional = new PdVector(3 * m_geom.getNumElements());
		PdVector y_positional = new PdVector(3 * m_geom.getNumElements());
		PdVector z_positional = new PdVector(3 * m_geom.getNumElements());
		for (int i = 0; i < elements.length; i++) {
			// Set transformed values into vector
			x_positional.setEntry(i * 3, vertices[elements[i].getEntry(0)].getEntry(0));
			x_positional.setEntry(i * 3 + 1, vertices[elements[i].getEntry(1)].getEntry(0));
			x_positional.setEntry(i * 3 + 2, vertices[elements[i].getEntry(2)].getEntry(0));

			y_positional.setEntry(i * 3, vertices[elements[i].getEntry(0)].getEntry(1));
			y_positional.setEntry(i * 3 + 1, vertices[elements[i].getEntry(1)].getEntry(1));
			y_positional.setEntry(i * 3 + 2, vertices[elements[i].getEntry(2)].getEntry(1));

			z_positional.setEntry(i * 3, vertices[elements[i].getEntry(0)].getEntry(2));
			z_positional.setEntry(i * 3 + 1, vertices[elements[i].getEntry(1)].getEntry(2));
			z_positional.setEntry(i * 3 + 2, vertices[elements[i].getEntry(2)].getEntry(2));
		}

		PsDebug.message("Resulting positional values:");
		PsDebug.message(x_positional.toString());
		PsDebug.message(y_positional.toString());
		PsDebug.message(z_positional.toString());

		PdVector g_x = PnSparseMatrix.rightMultVector(G, x_positional, null);
		PdVector g_y = PnSparseMatrix.rightMultVector(G, y_positional, null);
		PdVector g_z = PnSparseMatrix.rightMultVector(G, z_positional, null);

		PsDebug.message("Resulting gradiant vectors:");
		PsDebug.message(g_x.toString());
		PsDebug.message(g_y.toString());
		PsDebug.message(g_z.toString());

		// Apply transformation matrix to the selected elements:
		// TODO: add for loop for selected elements and apply transformation matrix
		for(int i = 0; i < elements.length; i++) {
			if (elements[i].hasTag(PsObject.IS_SELECTED)) {
				applyTransformationMatrix(g_x, a, i);
				applyTransformationMatrix(g_y, a, i);
				applyTransformationMatrix(g_z, a, i);
			}
		}

		// Calculate right side of equation G_tranposed * WeightMatrix * G * X_tilde = G_tranposed * WeightMatrix * g_tilde_x
		PnSparseMatrix rightSideMatrix = PnSparseMatrix.multMatrices(G_transpose, M, null);
		PsDebug.message("Resulting right side matrix:");
		PsDebug.message(rightSideMatrix.toString());

		// Complete right hand side computation of equation:
		PdVector rightSideResult_x = PnSparseMatrix.rightMultVector(rightSideMatrix, g_x, null);
		PdVector rightSideResult_y = PnSparseMatrix.rightMultVector(rightSideMatrix, g_y, null);
		PdVector rightSideResult_z = PnSparseMatrix.rightMultVector(rightSideMatrix, g_z, null);

		PsDebug.message("Resulting rightside vectors:");
		PsDebug.message(rightSideResult_x.toString());
		PsDebug.message(rightSideResult_y.toString());
		PsDebug.message(rightSideResult_z.toString());

		// Calculate left hand side matrix
		PnSparseMatrix leftSideMatrix = PnSparseMatrix.multMatrices(G_transpose, PnSparseMatrix.multMatrices(M, G, null), null);
		PsDebug.message("Resulting left side matrix:");
		PsDebug.message(leftSideMatrix.toString());

		PdVector x_tilde = new PdVector(rightSideResult_x.getSize());
		PdVector y_tilde = new PdVector(rightSideResult_y.getSize());
		PdVector z_tilde = new PdVector(rightSideResult_z.getSize());

		PnBiconjugateGradient solver = new PnBiconjugateGradient();
		// Solve for x_tilde:
		solver.solve(leftSideMatrix, x_tilde, rightSideResult_x);

		// Solve for y_tilde:
		solver.solve(leftSideMatrix, y_tilde, rightSideResult_y);

		// Solve for z_tilde
		solver.solve(leftSideMatrix, z_tilde, rightSideResult_z);

		// Resulting X coordinates
		PsDebug.message("Resulting coordinates");
		PsDebug.message(x_tilde.toString());
		PsDebug.message(y_tilde.toString());
		PsDebug.message(z_tilde.toString());

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
	}

	private void applyTransformationMatrix(PdVector vector, PnSparseMatrix matrix, int index) {
		PdVector temp = new PdVector(3);
		temp.setEntry(0, vector.getEntry((index * 3)));
		temp.setEntry(1, vector.getEntry((index * 3)+1));
		temp.setEntry(2, vector.getEntry((index * 3)+2));

		temp = PnSparseMatrix.rightMultVector(matrix, temp, null);
		vector.setEntry((index * 3), temp.getEntry(0));
		vector.setEntry((index * 3) + 1, temp.getEntry(1));
		vector.setEntry((index * 3) + 2, temp.getEntry(2));
	}

	private PiVector[] getSelectedElements() {
		PiVector[] elements = m_geom.getElements();
		ArrayList<PiVector> selectedElements = new ArrayList<>();
		for(int i = 0; i < elements.length ; i++) {
			if (elements[i].hasTag(PsObject.IS_SELECTED)) {
				selectedElements.add(elements[i]);
			}
		}

		PiVector[] array = new PiVector[selectedElements.size()];
		return selectedElements.toArray(array);
	}
}