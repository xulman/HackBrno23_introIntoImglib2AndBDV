package net.imglib2.brno_learnathon.s3_study_imglib2;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.brno_learnathon.s2_try_yourself_imglib2.t4_HandlingDimensionalityExample;
import net.imglib2.img.Img;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.view.ExtendedRandomAccessibleInterval;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

import java.util.Arrays;
import java.util.List;

public class t1_Views {
	public static <T extends RealType<T>>
	void enlargingImagesWithArtificialOutsideSurroundings(final RandomAccessibleInterval<T> image) {
		//For the use of case of computing convolution, one typically need to
		//extend the image with certain boundary while preserving its iterability domain,
		//the original Interval... The convolution outer-loop sweeps over the original
		//image but it can fetch pixels from outside the image

		//For example, this enlarges the original image by 50 pixels on each side
		//in each dimension (that is by 100 pixels along each dimension), and the
		//new area contains mirrored boundary image data:
		//(this here only specifies the boundary extension per dimension)
		long[] borderSizes = new long[image.numDimensions()];
		Arrays.setAll(borderSizes, arrayIndex -> 50);
		//
		//(the actual extension is built here....)
		IntervalView<T> mirroredImage = Views.expandBorder(image, borderSizes);
		ImageJFunctions.show(mirroredImage, "Mirrored image with 50px wide boundary");

		//FYI, the step above is in fact doing two operations....
		noteOnTheExpandVsExtend(image);
	}

	public static <T extends RealType<T>>
	void noteOnTheExpandVsExtend(final RandomAccessibleInterval<T> image) {
		//Notice there are similarly named methods Views.expandFoo() and Views.extendFoo(),
		//what's the difference?

		//The extendFoo() variants indeed build the extra boundary zone around the input image,
		//                turning the image into being infinitely large, notice the methods don't
		//                need any "boundary size" parameters; one typically needs to wrap such
		//                images with some Interval again

		//The expandFoo() variants conduct the two steps in one call, they build the boundary
		//                extended image and "intervalize" it... and require boundary size
		//                specification parameters therefore

		ExtendedRandomAccessibleInterval<T, RandomAccessibleInterval<T>> erai = Views.extendBorder(image);
		//...but(!) the extension is infinite (despite the name!), so one has to
		//re-introduce Interval to make it bounded again, like "normal" images are:
		IntervalView<T> mirroredImage = Views.interval(erai, Intervals.expand(image, 50));
		ImageJFunctions.show(mirroredImage, "Mirrored image with 50x wide boundary - created step by step");

		//the "shortcut" equivalent:
		mirroredImage = Views.interval(
				Views.extendBorder(image),
				Intervals.expand(image, 50)
		);

		//still it can be useful to have "just" a RandomAccessible
		//(in contrast to RandomAccessibleInterval)

		//Btw, Intervals is another utility class... for manipulating Interval
	}

	public static <T extends RealType<T>>
	void multipleViews(final Img<T> image) {
		//It is of course perfectly legal to set up several Views over the same
		//input image. And since they are still Views, meaning, they are fetching values
		//from the (same) source _on-the-fly_, so if the source content is changed,
		//the change should be immediately visible in all views, no need to visit all
		//of them and notify them somehow.

		//Favourite use-case for this is when one needs to sweep over small number
		//of pixels arranged in a fixed spatial pattern, like e.g. horizontal central
		//difference:  ([x-h,...] - [x+h,...]) / 2h where 'h'

		//Prepare output memory (image):
		Img<T> resultImage = image.factory().create(image);
		Cursor<T> res = resultImage.cursor();

		//Extend boundaries to make sure input images can provide values just "everywhere"
		ExtendedRandomAccessibleInterval<T, RandomAccessibleInterval<T>> extendedImage = Views.extendBorder(image);
		//This is, btw, a RandomAccessible (infinite extent) image, without na Interval (we've talked about that)....
		//
		//...over which we would create two constructions with same-sized Intervals but
		//shifted - positioned slightly off w.r.t. the original image (Interval)
		IntervalView<T> backwardShiftedImage = Views.interval(extendedImage, Intervals.translate(image, -1, 0));
		IntervalView<T> forwardShiftedImage = Views.interval(extendedImage, Intervals.translate(image, +1, 0));

		//could have also been: Views.interval(...).cursor()
		Cursor<T> back = backwardShiftedImage.cursor();
		Cursor<T> forw = forwardShiftedImage.cursor();
		while (res.hasNext()) {
			res.next().setReal((forw.next().getRealDouble() - back.next().getRealDouble()) / 2.0);
		}
		ImageJFunctions.show(resultImage, "horizontal central difference ("+resultImage.getClass().getSimpleName()+")");

		//btw, beware! that for CellImgs such looping wraps at cell boundaries...
		//...and not only at the very ends of the image
		LoopBuilder.setImages(resultImage, backwardShiftedImage,forwardShiftedImage)
				.forEachPixel((r, b,f) -> r.setReal( (f.getRealDouble()-b.getRealDouble())/2.0 ));
		ImageJFunctions.show(resultImage, "horizontal central difference with LoopBuilder ("+resultImage.getClass().getSimpleName()+")");
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
		//tools in the hands of image processing programmer, where a tool = one individual View.

		//The governing idea of the Views is basically, considering now a single View on a given
		//input image, to provide a fake, made-up image that shows certain modified version
		//of the input image. The input image is never modified, and the provided made-up image
		//is computed on-demand and on-the-fly.
		//
		//Another strong design motif is that the Views can be chained. This allows for the
		//building of sophisticated constructs over original "normal" images. These constructs
		//are, again, evaluated only on on-demand basis and shall not be proactively computed, and
		//thus memorized on some discrete grids, which would have lead to accuracy and information loss.
		//
		//Especially, however, the Views can masquerade (alleviate) the usual "technicalities"
		//present in image processing pipelines (such as treating boundary conditions within the
		//main for loop), enabling the programmer to focus on the image processing itself.

		//Examples of Views are reduction of the input image dimensionality, flipping axes,
		//extending images with "procedurally generated outside surroundings", or various
		//image transforms. A complete list of them is best obtained using the autocompletion
		//functionality of most IDEs, just type 'Views.' and study the pop-up list of available
		//methods...

		//In the following we will demonstrate several popular use cases.
		final Img<FloatType> image = t4_HandlingDimensionalityExample.get3dImageWithPattern();

		enlargingImagesWithArtificialOutsideSurroundings(image);
		multipleViews(image);
		multipleViews( cloneAsCellImg(image) );
		limitingViews(image);
		axesAndDimensions(image);
		addingIterability(image);
		addingBounds(image);
	}

	private static <T extends RealType<T> & NativeType<T>>
	Img<T> cloneAsCellImg(final Img<T> img) {
		int[] cellDims = new int[img.numDimensions()];
		Arrays.setAll(cellDims, index -> 25);

		Img<T> cellImg = new CellImgFactory<>(img.firstElement(), cellDims).create(img);
		LoopBuilder.setImages(img,cellImg).forEachPixel((i,o) -> o.setReal(i.getRealDouble()));
		return cellImg;
	}
}
