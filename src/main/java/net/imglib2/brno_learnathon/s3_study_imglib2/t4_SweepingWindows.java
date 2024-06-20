package net.imglib2.brno_learnathon.s3_study_imglib2;

import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.brno_learnathon.s2_try_yourself_imglib2.t4_HandlingDimensionalityExample;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class t4_SweepingWindows {
	public static int SPHERE_RADIUS = 10;

	public static <T extends RealType<T>>
	void drawSphereAtImageCentre(final RandomAccessible<T> image,
	                             final Point sphere1Position,
	                             final Point sphere2Position)
	{
		//set up an n-dimensional sphere around the first position
		final HyperSphere<T> sphereShapedWindow = new HyperSphere<>(image, sphere1Position, SPHERE_RADIUS);

		//fill the sphere's pixels with some value, that is, set all pixels
		//that coincide with the sphere extent to that value
		Cursor<T> cursorInsideTheSphere = sphereShapedWindow.cursor();
		while (cursorInsideTheSphere.hasNext())
			cursorInsideTheSphere.next().setReal(2550);

		//to illustrate how to drag the sphere along the image, let's move
		//the sphere to the second position
		sphereShapedWindow.updateCenter(sphere2Position);

		//only then (after the update) reset the sphere's cursor and start filling,
		//
		//or, more simply, benefit from the iterability of the HyperSphere and also
		//from a clean/short construction that needs no explicit accessor variable
		sphereShapedWindow.forEach(p -> p.setReal(255));
	}

	public static < T extends RealType< T > >
	void drawSphereAtImageCentre2(final RandomAccessible< T > image,
	                              final Point sphere1Position,
	                              final Point sphere2Position)
	{
		//Here is another way to solve it using Shape
		//(net.imglib2.algorithm.neighborhood.Shape)
		//
		//This solution basically sets up a new image whose pixels contain individual
		//spheres, and (very untypically for images) each sphere stretches out from
		//its pixel over (selected, relevant) neighboring pixels. This is, however,
		//only an abstract construction and no such image is allocated in reality.
		//
		//Notice (which is the usual sign for "abstract constructions") that
		//'shape.neighborhoodsRandomAccessible' accepts RandomAccessible and
		//not RandomAccessibleInterval (iterability is not required).
		HyperSphereShape shape = new HyperSphereShape( SPHERE_RADIUS );
		RandomAccessible< Neighborhood< T > > hyperSpheres = shape.neighborhoodsRandomAccessible( image );

		//'hyperSpheres' is a RandomAccessible (an image-like container) over all
		//sphere-shaped neighborhoods in the image. RandomAccess allows to choose
		//a particular position, application of the shape, which is materialized
		//in a particular iterable Neighborhood object:
		RandomAccess< Neighborhood< T > > hyperSphere = hyperSpheres.randomAccess();
		hyperSphere.setPosition( sphere1Position );
		hyperSphere.get().forEach( t -> t.setReal( 2550 ) );
		//or, shorter using the already known pattern:
		//hyperSpheres.getAt( sphere1Position ).forEach( t -> t.setReal( 2550 ) );

		//reposition closer to the image boundary and draw another sphere
		hyperSphere.setPosition( sphere2Position );
		hyperSphere.get().forEach( t -> t.setReal( 255 ) );
	}

	public static void main(String[] args) {
		Img<FloatType> image = t4_HandlingDimensionalityExample.get3dImageWithPattern();

		final Point centerPos = new Point( image.numDimensions() );
		for (int n = 0; n < image.numDimensions(); ++n)
			centerPos.setPosition(image.dimension(n)/2, n);

		final Point closeToEdgePos = new Point( image.numDimensions() );
		closeToEdgePos.setPosition( centerPos );
		closeToEdgePos.setPosition(99, 0);

		drawSphereAtImageCentre( image, centerPos, closeToEdgePos );
		ImageJFunctions.show(image, "Patterned image with spheres at center and boundary.v1");

		image = t4_HandlingDimensionalityExample.get3dImageWithPattern();
		drawSphereAtImageCentre2( image, centerPos, closeToEdgePos );
		ImageJFunctions.show(image, "Patterned image with spheres at center and boundary.v2");

		//Notice that the second sphere, the one close to the (right) image boundary, is "continuing"
		//on the other side (left) of the image? This is an unwanted result, which additionally
		//depends on the backend of the image and where we write in the image (how close to the
		//end of relevant allocated memory segments).
		//
		//Nevertheless, both methods accept (unbounded) RandomAccessible, which we could afford
		//because the neighborhood methods are able to work with RandomAccessible too. Using the
		//Views utility the image is (again, abstract construction) extended with zero boundary,
		//and this extra zone is used to draw the "outside part" of the second sphere.

		image = t4_HandlingDimensionalityExample.get3dImageWithPattern();
		drawSphereAtImageCentre2(Views.extendZero(image), centerPos, closeToEdgePos );
		ImageJFunctions.show(image, "Patterned image with spheres at center and boundary.v2.zero_boundary");
	}
}
