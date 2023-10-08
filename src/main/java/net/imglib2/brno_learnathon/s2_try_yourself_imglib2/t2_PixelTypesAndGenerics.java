package net.imglib2.brno_learnathon.s2_try_yourself_imglib2;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.real.FloatType;

public class t2_PixelTypesAndGenerics {

	public static <T extends NumericType<T>>
	Img<T> createCloneOfTheInputWithGenerics(final Img<T> sourceImg) {
		//We wish only the geometry and backend of the image to be preserved
		return sourceImg.factory().create( sourceImg );

		//If also voxel values were to be copied...
		//return sourceImg.copy();

		//Notice: RAIs nor IIs don't have the factor() and copy() methods available
		//as they alone are in some sense incomplete and thus cannot be (re)constructed
	}

	public static void main(String[] args) {
		final Img<FloatType> img = t1_CreateImage.createCellImgs();
		final Img<?> clonedImg = createCloneOfTheInputWithGenerics(img);
		if (isClone(img, clonedImg)) System.out.println("GOOD JOB!");

		final Img<FloatType> fakeRef = t1_CreateImage.createArrayImgs();
		if (isClone(fakeRef, clonedImg)) System.out.println("If you read this, something is wrong...");
	}

	private static <T extends NumericType<T>>
	boolean isClone(final Img<T> ref, final Img<?> clone) {
		if (! clone.firstElement().getClass().equals( ref.firstElement().getClass() ) ) {
			System.out.println("Pixel types of the clone and source: Mismatch in pixel types");
			return false;
		}
		System.out.println("Pixel types of the clone and source: OK");

		if (clone.numDimensions() != ref.numDimensions()) {
			System.out.println("Dimensions of the clone and source: Dimensionality is different");
			return false;
		}
		System.out.println("Dimensions of the clone and source: OK");

		for (int n = 0; n < ref.numDimensions(); ++n) {
			if (clone.dimension(n) != ref.dimension(n)) {
				System.out.println("The size in dimension "+n+": The sizes are different");
				return false;
			}
			System.out.println("The size in dimension "+n+": OK");
		}

		if (! clone.iterationOrder().equals( ref.iterationOrder() ) ) {
			System.out.println("Iteration order (backend likely) of the clone and source: Order is different");
			return false;
		}
		System.out.println("Iteration order (backend likely) of the clone and source: OK");

		return true;
	}
}