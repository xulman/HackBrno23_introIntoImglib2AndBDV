package net.imglib2.brno_learnathon.s3_study_imglib2;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.brno_learnathon.s2_try_yourself_imglib2.t4_HandlingDimensionalityExample;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import java.util.List;

public class t1_Views {
	public static <T extends RealType<T>>
	void enlargingImagesWithArtificialOutsideSurroundings(final RandomAccessibleInterval<T> image) {
		//convolutions:
		//- extending the image with certain boundary while preserving
		//its iterability domain... sweeps over the original image but
		//can fetch pixels from outside

		noteOnTheExpandVsExtend(image);
	}

	public static <T extends RealType<T>>
	void noteOnTheExpandVsExtend(final RandomAccessibleInterval<T> image) {
		//example that interval and getting values is not firmly connected:
		//
		//expands .. enlarges the Interval, does not remove the Interval
		//extend .. remove Interval, making the source potential of infinite spatial extent
		//          leaving the caller to wrap with Views.Interval to reintroduce it
	}

	public static <T extends RealType<T>>
	void multipleViews(final RandomAccessibleInterval<T> image) {
		//e.g. for parallel sweeping over the same image

		//- multiple views on the same image for parallel sweeping
	}

	public static <T extends RealType<T>>
	void limitingViews(final RandomAccessibleInterval<T> image) {
		//- limiting the view, setting up a ROI
	}

	public static <T extends RealType<T>>
	void axesAndDimensions(final RandomAccessibleInterval<T> image) {
		// or removing/permuting the dimensions
	}

	public static <T extends RealType<T>>
	void addingIterability(final RandomAccessibleInterval<T> image) {
		//- turing RAI into II (adding iterability)
	}

	public static <T extends RealType<T>>
	void addingBounds(final RandomAccessibleInterval<T> image) {
		//adding back Interval
	}

	public static <T extends RealType<T>>
	void fromIntegerToRealDomainUsingInterpolation(final RandomAccessibleInterval<T> image) {

	}

	public static <T extends RealType<T>>
	void fromPointCloudToRealDomainUsingInterpolation(final List<RealLocalizable> pointCloud, final T pixelType) {

	}

	public static void main(String[] args) {

		//This session is about the utility class Views -- a collection of extremely useful
		//tools (a tool = one individual View) in the hands of image processing programmer.

		//The governing idea of the Views is basically, with every single View on a given
		//input image, to provide a fake, made-up image that shows certain modified version
		//of the input image. The input image is never modified, and the provided made-up image
		//is computed on-demand and on-the-fly.
		//
		//Another strong design motif is that the Views can be chained. This allows for the
		//building of sophisticated constructs over original "normal" images. These constructs
		//are, again, evaluated only on on-demand basis and shall not be proactively computed, and
		//thus memorized on some discrete grids, which often leads to accuracy and information loss.
		//
		//Especially, however, the Views can masquerade (alleviate) the usual "technicalities"
		//present in image processing pipelines (such as treating boundary conditions within the
		//main for loop), enabling the programmer to focus on the image processing itself.

		//Examples of Views are reduction of the input image dimensionality, flipping axes,
		//extending images with "procedurally generated outside surroundings", or various
		//image transforms. A complete list of them is best obtained using the autocompletion
		//functionality of most IDEs, just type 'Views.' and study the pop-up list of available
		//methods.

		//In the following we will demonstrate several popular use cases.
		Img<FloatType> image = t4_HandlingDimensionalityExample.get3dImageWithPattern();

		enlargingImagesWithArtificialOutsideSurroundings(image);
		multipleViews(image);
		limitingViews(image);
		axesAndDimensions(image);
		addingIterability(image);
		addingBounds(image);

		//RealRandomAccessibles...
		fromIntegerToRealDomainUsingInterpolation();
		fromPointCloudToRealDomainUsingInterpolation();
	}
}
