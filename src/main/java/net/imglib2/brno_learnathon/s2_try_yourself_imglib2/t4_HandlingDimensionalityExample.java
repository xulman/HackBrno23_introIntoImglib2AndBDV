package net.imglib2.brno_learnathon.s2_try_yourself_imglib2;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class t4_HandlingDimensionalityExample {
	//assuming the original task was to flip image in its 2nd and 3rd dimension,
	//but delivered a rather unfortunate solution as, among other issues, it does
	//not accept 2D image as an input even when the problem is intrinsically 2D...
	public static <T extends FloatType>
	void flipFixedDimsWithinParticularSetting(final RandomAccessibleInterval<T> image) {
		//this is a very limiting
		if (image.numDimensions() < 3)
			throw new IllegalArgumentException("Since rotating in yz, input image must be at least 3D.");

		//assume the plane is square, that's actually a "legal" wanted test ;)
		//(even though it could have been made general with boundaries extensions using the Views util class...)
		if (image.dimension(1) != image.dimension(2))
			throw new IllegalArgumentException("The rotated plane of the input image must be square.");

		RandomAccess<T> ra = image.randomAccess();
		RandomAccess<T> rb = image.randomAccess();
		for (int z = 0; z < image.dimension(2); ++z)
			for (int y = z; y < image.dimension(1); ++y) {
				//NB: doesn't even iterate over all x-values
				final long x = 10;
				float val = ra.setPositionAndGet(x,y,z).getRealFloat();
				//ra.setPositionAndGet(x,y,z).setReal( ra.setPositionAndGet(x,z,y).getRealFloat() );
				//beware! accessor is positioned, but before written to it is positioned elsewhere!
				//
				//ra.setPositionAndGet(x,y,z).setReal( rb.setPositionAndGet(x,z,y).getRealFloat() );
				//up: using two accessors is a workaround,
				//below: but don't need to position second time
				ra.get().setReal( rb.setPositionAndGet(x,z,y).getRealFloat() );
				rb.get().setReal( val );
			}
	}

	//TODO this will have to be provided by students
	public static <T extends FloatType>
	void flipExactly2D(final RandomAccessibleInterval<T> image) {
		//it is expected that the caller will massage the image to become
		//2D in exactly the wanted dimensions... using the Views util class
		if (image.numDimensions() != 2)
			throw new IllegalArgumentException("The input image must be exactly 2D.");

		//assume the plane is square, that's actually a "legal" wanted test ;)
		//(even though it could have been made general with boundaries extensions using the Views util class...)
		if (image.dimension(0) != image.dimension(1))
			throw new IllegalArgumentException("The input image must be square.");

		RandomAccess<T> ra = image.randomAccess();
		RandomAccess<T> rb = image.randomAccess();
		for (int coord1 = 0; coord1 < image.dimension(1); ++coord1)
			for (int coord0 = coord1; coord0 < image.dimension(0); ++coord0) {
				float val = ra.setPositionAndGet(coord0,coord1).getRealFloat();
				ra.get().setReal(
						//move to the opposite position
						//btw, two extra calls of setPosition() are removed
						rb.setPositionAndGet(coord1,coord0).getRealFloat()
				);
				rb.get().setReal( val );
			}
	}

	public static void main(String[] args) {
		Img<FloatType> imageA = get3dImageWithPattern();
		flipFixedDimsWithinParticularSetting( imageA );

		//equivalent of the above
		Img<FloatType> imageB = get3dImageWithPattern();
		flipExactly2D( Views.hyperSlice(imageB,0,10) );

		//extras available only for the latter solution
		//this could be wrapped in a for loop to process the full volume
		flipExactly2D( Views.hyperSlice(imageB,0,50) );

		//flips in another dimension than originally planned, no extra code was needed!
		Img<FloatType> imageC = get3dImageWithPattern();
		flipExactly2D( Views.hyperSlice(imageC,2,0) );

		//show everything in IJ, and considering using the ortho view
		ImageJFunctions.show(imageA, "3D image rotated in yz & x=0 using the fixed dims solution");
		ImageJFunctions.show(imageB, "3D image rotated in yz & x=0,50 using the flexible dims solution");
		ImageJFunctions.show(imageC, "3D image rotated in xy & z=0 using the flexible dims solution");
	}

	public static Img<FloatType> get3dImageWithPattern() {
		Img<FloatType> image = ArrayImgs.floats(100, 100, 100);

		float currentPixelValue = 50.f;
		for (int y = 0; y < 5; ++y)
			for (int x = 0; x < 5; ++x) {
				final float valForLambda = currentPixelValue;
				Views.interval(image, new long[] {20*x, 20*y, 0}, new long[] {20*x +19, 20*y +19, 99})
						.forEach(p -> p.setReal(valForLambda));
				currentPixelValue += 50;
			}

		return image;
	}
}
