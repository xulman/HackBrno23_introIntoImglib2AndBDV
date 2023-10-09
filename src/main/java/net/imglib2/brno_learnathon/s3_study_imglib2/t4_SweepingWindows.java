package net.imglib2.brno_learnathon.s3_study_imglib2;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
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
	void drawWhiteSphereAtImageCentre(final RandomAccessibleInterval<T> image) {

		//define the sphere's "top-left corner" (that is, the minimal coordinate
		//of a minimal axis-aligned bounding Interval around the sphere) from
		//the image central position/coordinate
		final RandomAccess<T> sphereCornerPos = image.randomAccess();
		for (int n = 0; n < image.numDimensions(); ++n)
			sphereCornerPos.setPosition(image.dimension(n)/2 -SPHERE_RADIUS, n);

		//set up an n-dimensional sphere around this position
		final HyperSphere<T> sphereShapedWindow = new HyperSphere<>(image, sphereCornerPos, SPHERE_RADIUS);

		//fill the sphere pixels with some value, that is, set all pixels
		//that coincide with the sphere extent to that value
		Cursor<T> cursorInsideTheSphere = sphereShapedWindow.cursor();
		while (cursorInsideTheSphere.hasNext())
			cursorInsideTheSphere.next().setReal(2550);

		//---------------------------------------------------------------
		//to illustrate how to drag the sphere along the image, let's move
		//the sphere along the first dimension (usually the x-axis) a bit
		sphereCornerPos.move(2*(SPHERE_RADIUS+1), 0);
		sphereCornerPos.setPosition(99,0); //set close to the border!!! //TODO OVERFLOWS
		sphereShapedWindow.updateCenter(sphereCornerPos);

		//only then (after the update) reset the sphere's cursor
		cursorInsideTheSphere = sphereShapedWindow.cursor();

		//and draw a bit darker sphere again
		while (cursorInsideTheSphere.hasNext())
			cursorInsideTheSphere.next().setReal(255);
	}

	public static void main(String[] args) {
		Img<FloatType> image = t4_HandlingDimensionalityExample.get3dImageWithPattern();

		//not safe as the sphere radius is fixed!
		//TODO
		drawWhiteSphereAtImageCentre( image );

		//expand the image to each side along each dimension with the SPHERE_RADIUS
		//to make sure the image can always contain the full sphere
		drawWhiteSphereAtImageCentre( Views.expandValue(image, (float) 0, SPHERE_RADIUS, SPHERE_RADIUS, SPHERE_RADIUS) );

		ImageJFunctions.show(image, "Patterned image with white sphere at its centre");
	}
}
