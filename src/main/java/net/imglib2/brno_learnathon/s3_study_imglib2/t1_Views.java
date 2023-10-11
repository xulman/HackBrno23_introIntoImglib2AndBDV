package net.imglib2.brno_learnathon.s3_study_imglib2;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
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

import java.io.IOException;
import java.util.Arrays;

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
				.flatIterationOrder() //assures that the scanning order is always the "natural one"
				.forEachPixel((r, b,f) -> r.setReal( (f.getRealDouble()-b.getRealDouble())/2.0 ));
		ImageJFunctions.show(resultImage, "horizontal central difference with LoopBuilder ("+resultImage.getClass().getSimpleName()+")");
	}

	public static <T extends RealType<T>>
	void addingIterability(final RandomAccessibleInterval<T> image) {
		//It was suggested earlier in this tutorial that methods should ideally
		//be requesting images using the most light-weight possible types, that is,
		//types that offer just the required property/behaviour and minimum beyond it.
		//
		//It was also noted that it usually resorts to requesting RAIs, instead of
		//full 'Img', sacrificing the iterability. If it turns out that the iterability
		//could be "good to have back", there exists Views solution for it:
		//
		//image.cursor(); //doesn't work, type-wise it is a pure RAI
		Views.iterable(image).cursor(); //now the iterability is back
		Views.flatIterable(image).cursor();
		//
		//where the former offers iterability that's natural for the respective 'image' backends,
		//the later restores always the "normal" iterability (scanning fully first dimension, and
		//only then the scanning is moved in the second dimension etc.).

		//Note that since Java offers reflection, it is possible to figure out during the runtime
		//what class a given object actually is. It may turn out that the input RAI is actually
		//stripped down 'Img' where the iterability was provided, and if that is the case this
		//Views method basically "only casts" back to 'Img'. So the Views.iterable() need not be
		//always an expensive operation....
	}

	public static <T extends RealType<T>>
	void limitingViews(final RandomAccessibleInterval<T> image) {
		//Limiting and extending the view range (Interval) onto the image is possible.
		//
		//In the former case, by setting up a ROI, one saves the number of visited pixels.
		//This can be useful when working with instance segmentation masks (if one remembers
		//bounding boxes around all labels).

		long firstSearchCnt = findValue(Views.iterable(image), 350);   //Views.iterable() in action here!
		long secondSearchCnt = findValue(Views.interval(image, new long[] {18,18,0}, new long[] {42,42,99}), 350);
		if (firstSearchCnt != secondSearchCnt) System.out.println("Shouldn't get here....");

		//btw, one can pack the aux arrays new long[] {...} as above into an Interval object:
		Interval roi = new FinalInterval(new long[] {18,18,0}, new long[] {42,42,99});
		long thirdSearchCnt = findValue(Views.interval(image, roi), 350);
		if (secondSearchCnt != thirdSearchCnt) System.out.println("Shouldn't get here....");
	}

	private static <T extends RealType<T>>
	long findValue(final IterableInterval<T> img, final double seekedValue) {
		long visitedPixelsCnt = 0;
		long pixelsOccupyingTheSeekedValue = 0;
		for (T p : img) {
			pixelsOccupyingTheSeekedValue += p.getRealDouble() == seekedValue ? 1 : 0;
			++visitedPixelsCnt;
		}
		System.out.println("Scanned over "+visitedPixelsCnt+" pixels.");
		return pixelsOccupyingTheSeekedValue;
	}

	public static <T extends RealType<T>>
	void axesAndDimensions(final RandomAccessibleInterval<T> image) {
		//The axes can be also permuted, and also the image dimensionality
		//can be decreased or increased.

		ImageJFunctions.show( Views.permute(image, 0,1),
				"Image with permuted order of axes (flipped x and y axes)");

		//becoming 2D
		ImageJFunctions.show( Views.hyperSlice(image, 2,0),
				"Image at z = 0, all other dimensions are preserved.");

		//becoming 4D
		ImageJFunctions.show( Views.addDimension(image, 0,5),
				"Image with new 4th dimension.");
		//However, no new data (content) is created in this View. Instead, the current
		//data is used at any coordinate in the new 4th dimension.
	}

	public static void main(String[] args) throws IOException {

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
		addingIterability(image);
		limitingViews(image);
		axesAndDimensions(image);
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
