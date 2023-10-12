package net.imglib2.brno_learnathon.s2_try_yourself_imglib2;

import net.imagej.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

public class t3_FillingResultImage {

	//intentionally extends FloatType not to spoil solution, replace with something reasonable
	public static <T extends FloatType>
	void pixelWiseSqrt1(final RandomAccessibleInterval<T> input,
	                    final RandomAccessibleInterval<T> output) {
		if (!Intervals.equalDimensions(input,output))
			throw new IllegalArgumentException("The given input and output images are not of the same size.");

		//since both are RAIs, we decide to iterate explicitly on our own
		if (input.numDimensions() != 3)
			throw new IllegalArgumentException("Unsupported dimensionality :(");

		final long pos[] = new long[3];
		final RandomAccess<T> ra_in = input.randomAccess();
		final RandomAccess<T> ra_out = output.randomAccess();

		for (long z = 0; z < output.dimension(2); ++z)
			for (long y = 0; y < output.dimension(1); ++y)
				for (long x = 0; x < output.dimension(0); ++x) {
					pos[0] = x;
					pos[1] = y;
					pos[2] = z; //or set positions per-axes in between the for-loops
					ra_in.setPosition( pos );
					ra_out.setPosition( pos );
					ra_out.get().setReal( Math.sqrt(ra_in.get().get()) );
				}
	}

	public static <T extends FloatType>
	void pixelWiseSqrt2(final RandomAccessibleInterval<T> input,
	                    final RandomAccessibleInterval<T> output) {
		if (!Intervals.equalDimensions(input,output))
			throw new IllegalArgumentException("The given input and output images are not of the same size.");

		final RandomAccess<T> ra_in = input.randomAccess();
		//since both are RAIs, we convert the *output* (as this is the one that
		//needs to be filled) into II and use it for driving the iteration
		final Cursor<T> lc_out = Views.iterable(output).cursor();

		.....
		}
	}

	public static <T extends FloatType>
	void pixelWiseSqrt3(final RandomAccessibleInterval<T> input,
	                    final RandomAccessibleInterval<T> output) {
		if (!Intervals.equalDimensions(input,output))
			throw new IllegalArgumentException("The given input and output images are not of the same size.");

		final RandomAccess<T> ra_in = input.randomAccess();
		//since both are RAIs, we convert the *output* (as this is the one that
		//needs to be filled) into II and use it for driving the iteration
		final Cursor<T> lc_out = Views.iterable(output).localizingCursor();

		.....
		}
	}

	public static <T extends FloatType>
	void pixelWiseSqrt4(final RandomAccessibleInterval<T> input,
	                    final RandomAccessibleInterval<T> output) {
		if (!Intervals.equalDimensions(input,output))
			throw new IllegalArgumentException("The given input and output images are not of the same size.");

		//since both are RAIs, we convert the *output* (as this is the one that
		//needs to be filled) into II and use it for driving the iteration
		final Cursor<T> lc_out = Views.iterable(output).localizingCursor();

		while (lc_out.hasNext()) {
			lc_out.next();
			T pixel = input.getAt(lc_out); //taking getAt() shortcut instead of using a proper accessor
			pixel.setReal( Math.sqrt(pixel.get()) );
		}
	}

	//One interesting observation is that for the above we never need the
	//Interval of the input. So these methods could be generalized further by
	//taking RandomAccessible<T> input. They would then also work for copying
	//regions out of larger (potentially unbounded) input images.

	public static <T extends FloatType>
	RandomAccessibleInterval<T> pixelWiseCloneThenSqrt1(final Img<T> input) {
		RandomAccessibleInterval<T> output = input.factory().create( input );

		final RandomAccess<T> ra_in = input.randomAccess();
		//the output is created by cloning the input (which guarantees the same backend),
		//and make it II and again use it for driving the iteration
		final Cursor<T> lc_out = Views.iterable(output).localizingCursor();

		while (lc_out.hasNext()) {
			lc_out.next();
			ra_in.setPosition( lc_out ); //variable dimensionality covered here
			lc_out.get().setReal( Math.sqrt(ra_in.get().get()) );
		}

		return output;
	}

	public static <T extends FloatType>
	RandomAccessibleInterval<T> pixelWiseCloneThenSqrt2(final Img<T> input) {
		RandomAccessibleInterval<T> output = input.factory().create( input );

		//the output is created by cloning the input (which guarantees the same backend),
		//and them both II and sweep them in parallel
		final Cursor<T> c_in = Views.iterable(input).cursor();
		final Cursor<T> c_out = Views.iterable(output).cursor();

		while (c_out.hasNext()) {
			c_in.next().setReal( Math.sqrt(c_out.next().get()) );
			//variable dimensionality no problem...
		}

		return output;
	}

	public static <T extends FloatType>
	RandomAccessibleInterval<T> pixelWiseCloneThenSqrt3(final Img<T> input) {
		RandomAccessibleInterval<T> output = input.factory().create( input );

		//the output is created by cloning the input (which guarantees the same backend),
		//and them both II and sweep them in parallel
		final Cursor<T> c_in = Views.flatIterable(input).cursor();
		final Cursor<T> c_out = Views.flatIterable(output).cursor();

		while (c_out.hasNext()) {
			c_in.next().setReal( Math.sqrt(c_out.next().get()) );
			//variable dimensionality no problem...
		}

		return output;
	}

	public static <T extends FloatType>
	void doExperiment(final Img<T> input,
	                  final RandomAccessibleInterval<T> output) {

		System.out.println("Starting a new batch of experiments:");
		input.forEach(p -> p.setReal(121));

		long t = tic();
		pixelWiseSqrt1(input, output);
		tac(t, "  RAI,RAI, fixed-dim loop without any cursors");

		t = tic();
		pixelWiseSqrt2(input, output);
		tac(t, "  RAI,RAI, okay loop with non-localizing cursor");

		t = tic();
		pixelWiseSqrt3(input, output);
		tac(t, "  RAI,RAI, okay loop with localizing cursor");

		t = tic();
		pixelWiseSqrt4(input, output);
		tac(t, "  RAI,RAI, okay loop with localizing cursor and getAt() instead of accessor");

		t = tic();
		pixelWiseCloneThenSqrt1(input);
		tac(t, "  RAI,new, okay loop with localizing cursor");

		t = tic();
		pixelWiseCloneThenSqrt2(input);
		tac(t, "  RAI,new, II-II loop with non-localizing cursors");

		t = tic();
		pixelWiseCloneThenSqrt3(input);
		tac(t, "  RAI,new, II-II loop with non-localizing cursors, flatIterable");
	}

	public static <T extends FloatType>
	void visualizeIterabilityOrder(final IterableInterval<T> image) {
		float counter = 0;
		for (FloatType p : image) p.setReal( counter++ );
		//Cursor<T> c = image.cursor();
		//while (c.hasNext()) c.next().setReal( counter++ );
	}

	public static <T extends FloatType>
	void illustrateBackends() {
		//Let's fill the images and show them to see the differences in iteration orders:
		Img<FloatType> arrayImg = ArrayImgs.floats(512,256);
		Img<FloatType> cellImg = new CellImgFactory<>(new FloatType(), 100,100).create(512,256);
		Img<FloatType> cellImgFI = cellImg.factory().create( cellImg );

		ImageJ ij = new ImageJ();
		ij.ui().showUI();

		visualizeIterabilityOrder(arrayImg);
		ij.ui().show("ArrayImg: Natural order of filling", arrayImg);

		visualizeIterabilityOrder(cellImg);
		ij.ui().show("CellImg: Natural order of filling", cellImg);

		visualizeIterabilityOrder( Views.flatIterable(cellImgFI) );
		ij.ui().show("CellImg: flatIterable order of filling", cellImgFI);
	}

	public static void main(String[] args) {
		Img<FloatType> arrayImg = ArrayImgs.floats(1024,1024,100);
		Img<FloatType> cellImg = new CellImgFactory<>(new FloatType(), 100,100,50).create(1024,1024,100);

		doExperiment(arrayImg, cellImg);
		doExperiment(cellImg, arrayImg);

		//Looking at the reported times, we can notice the following:
		//
		//The flatIterable version naturally brings no delay on ArrayImg because
		//flat iteration is the "native" order of ArrayImg.
		//
		//With CellImg, the flatIterable version brings noticeable delay on
		//CellImg. The "native" iteration order of CellImg is visiting all
		//pixels in the first cell, then all pixels in the second cell, and so
		//on. To impose flat iteration order, a RandomAccess is scanned over the
		//image, requiring to keep track of coordinates when (frequently)
		//crossing cell boundaries, which is expensive. Also, switching between
		//Cells frequently is less suited to utilizing processor caches.

		//Let's illustrate the iteration orders:
		illustrateBackends();
	}

	private static long tic() {
		return System.currentTimeMillis();
	}
	private static double tac(long tic, final String msg) {
		double timeSpan = (double)(System.currentTimeMillis() - tic) / 1000.0;
		System.out.println(msg+" took "+timeSpan+" seconds");
		return timeSpan;
	}
}
