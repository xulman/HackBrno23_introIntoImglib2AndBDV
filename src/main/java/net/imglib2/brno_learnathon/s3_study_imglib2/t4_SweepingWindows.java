package net.imglib2.brno_learnathon.s3_study_imglib2;

import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.brno_learnathon.s2_try_yourself_imglib2.t4_HandlingDimensionalityExample;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

public class t4_SweepingWindows {
	public static int SPHERE_RADIUS = 10;

	public static <T extends RealType<T>>
	void drawSphereAtImageCentre(final RandomAccessibleInterval<T> image)
	{
		Point centerPos = new Point( image.numDimensions() );
		for (int n = 0; n < image.numDimensions(); ++n)
			centerPos.setPosition(image.dimension(n)/2, n);

		//set up an n-dimensional sphere around this position
		final HyperSphere<T> sphereShapedWindow = new HyperSphere<>(image, centerPos, SPHERE_RADIUS);

		//fill the sphere pixels with some value, that is, set all pixels
		//that coincide with the sphere extent to that value
		Cursor<T> cursorInsideTheSphere = sphereShapedWindow.cursor();
		while (cursorInsideTheSphere.hasNext())
			cursorInsideTheSphere.next().setReal(2550);

		//to illustrate how to drag the sphere along the image, let's move
		//the sphere along the first dimension (usually the x-axis) a bit
		centerPos.setPosition(99,0);
		sphereShapedWindow.updateCenter(centerPos);

		//only then (after the update) reset the sphere's cursor,
		//or, more simply, benefit from the iterability of the HyperSphere
		//and from the construction that needs no explicit accessor variable
		sphereShapedWindow.forEach(p -> p.setReal(255));
	}

	public static < T extends RealType< T > >
	void drawSphereAtImageCentre2(final RandomAccessibleInterval< T > image )
	{
		//Here is another way to solve it using Shape
		//(net.imglib2.algorithm.neighborhood.Shape)
		//
		//This solution basically sets up a new image whose pixels contain individual
		//spheres, and (very untypically for images) each sphere stretches out from
		//its pixel over (selected, relevant) neighboring pixels. This is, however,
		//only an abstract construction and no such image is allocated in reality.
		HyperSphereShape shape = new HyperSphereShape( SPHERE_RADIUS );
		RandomAccessible< Neighborhood< T > > hyperspheres = shape.neighborhoodsRandomAccessible( image );

		Point centerPos = new Point( image.numDimensions() );
		for ( int d = 0; d < image.numDimensions(); ++d )
			centerPos.setPosition( image.dimension( d ) / 2, d );

		//'hyperspheres' is a RandomAccessible (an image-like container) over all
		//sphere-shaped neighborhoods in the image. RandomAccess allows to choose
		//a particular position, application of the shape, which is materialized
		//in a particular iterable Neighborhood object:
		RandomAccess< Neighborhood< T > > hypersphere = hyperspheres.randomAccess();
		hypersphere.setPosition( centerPos );
		hypersphere.get().forEach( t -> t.setReal( 2550 ) );
		//or, shorter using the already known pattern:
		//hyperspheres.getAt( centerPos ).forEach( t -> t.setReal( 2550 ) );

		//reposition closer to the image boundary and draw another sphere
		hypersphere.setPosition( 99, 0 );
		hypersphere.get().forEach( t -> t.setReal( 255 ) );
	}

	public static void main(String[] args) {
		Img<FloatType> image = t4_HandlingDimensionalityExample.get3dImageWithPattern();
		drawSphereAtImageCentre( image );
		ImageJFunctions.show(image, "Patterned image with white sphere at its centre.v1");

		image = t4_HandlingDimensionalityExample.get3dImageWithPattern();
		drawSphereAtImageCentre2( image );
		ImageJFunctions.show(image, "Patterned image with white sphere at its centre.v2");
	}
}
