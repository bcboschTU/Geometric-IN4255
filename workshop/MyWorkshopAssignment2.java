package workshop;

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
		int m = m_geom.getNumVertices();
		PnSparseMatrix M = new PnSparseMatrix(n, m, 3);
		for (int i = 0; i < m_geom.getNumElements(); i++) {
			M.setEntry((i * 3), (i * 3), m_geom.getAreaOfElement(i));
			M.setEntry((i * 3) + 1, (i * 3) + 1, m_geom.getAreaOfElement(i));
			M.setEntry((i * 3) + 2, (i * 3) + 2, m_geom.getAreaOfElement(i));
		}

		PsDebug.message("Weight matrix M:");
		PsDebug.message(M.toString());

		PiVector[] selectedElements = getSelectedElements();
		PsDebug.message("Selected Elements: " + selectedElements.length);

		// Calculate right side of equation G_tranposed * WeightMatrix * G * X_tilde = G_tranposed * WeightMatrix * g_tilde
		PnSparseMatrix rightSideMatrix = PnSparseMatrix.multMatrices(G_transpose, M, null);

		PnSparseMatrix leftSideMatrix = PnSparseMatrix.multMatrices(G_transpose, PnSparseMatrix.multMatrices(M, G, null), null);

		PdVector g_tilde = new PdVector(m_geom.getNumElements()*3);
		PiVector[] elements = m_geom.getElements();
		// Calculate x gradient:
		// deformation matrix a moet vermenigvuldigen met x, y en z waarden en niet met alleen x waarden
		for (int i = 0; i < elements.length; i++) {
			PdVector temp = new PdVector(3);
			PdVector g_tilde_temp = new PdVector(3);
			// Extract gradient from sparse matrix
			temp.setEntry(0, G.getEntry(i, elements[i].getEntry(0)));
			temp.setEntry(1, G.getEntry(i, elements[i].getEntry(1)));
			temp.setEntry(2, G.getEntry(i, elements[i].getEntry(2)));
			// Apply matrix to x gradient
			PnSparseMatrix.rightMultVector(a, temp, g_tilde_temp);
			// Set transfromed values into vector
			g_tilde.setEntry(i*3, g_tilde_temp.getEntry(0));
			g_tilde.setEntry(i*3+1, g_tilde_temp.getEntry(1));
			g_tilde.setEntry(i*3+2, g_tilde_temp.getEntry(2));
		}
		PsDebug.message("g_tilde ");
		PsDebug.message(g_tilde.toString());

		PdVector rightSideVector = PnSparseMatrix.rightMultVector(rightSideMatrix, g_tilde, null);
		PdVector x_tilde = new PdVector(rightSideVector.getSize());
		// Solve x gradient:
		PnBiconjugateGradient solver = new PnBiconjugateGradient();
		solver.solve(leftSideMatrix, x_tilde, g_tilde);





		for (PiVector element : selectedElements) {
			// Create gradient vectors from positions
			PdVector gx = new PdVector();
			PdVector gy = new PdVector();
			PdVector gz = new PdVector();

			for (int e = 0; e < 3; e++) {
				PdVector p = m_geom.getVertex(element.getEntry(e));
				gx.setEntry(e, p.getEntry(0));
				gy.setEntry(e, p.getEntry(1));
				gz.setEntry(e, p.getEntry(2));
			}

			// Apply matrix to gradient vectors
			PdVector gtildex = new PdVector();
			PdVector gtildey = new PdVector();
			PdVector gtildez = new PdVector();
			PnSparseMatrix.rightMultVector(a, gx, gtildex);
			PnSparseMatrix.rightMultVector(a, gy, gtildey);
			PnSparseMatrix.rightMultVector(a, gz, gtildez);

			// Solve for new vertex positions
			PdVector xtilde = new PdVector();
			PdVector ytilde = new PdVector();
			PdVector ztilde = new PdVector();
			try {
				solver.solve(G, xtilde, gtildex);
//				long factorization = PnMumpsSolver.factor(G, PnMumpsSolver.Type.GENERAL_SYMMETRIC);
//
//				PnMumpsSolver.solve(factorization, xtilde, gtildex);
//				PnMumpsSolver.solve(factorization, ytilde, gtildey);
//				PnMumpsSolver.solve(factorization, ztilde, gtildez);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Set new vertex positions
			for (int e = 0; e < 3; e++) {
				PdVector p = m_geom.getVertex(element.getEntry(e));
				p.setEntry(0, xtilde.getEntry(e));
				p.setEntry(1, ytilde.getEntry(e));
				p.setEntry(2, ztilde.getEntry(e));
			}
		}

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