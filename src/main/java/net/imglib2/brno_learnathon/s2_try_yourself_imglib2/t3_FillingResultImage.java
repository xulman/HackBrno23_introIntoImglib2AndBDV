package net.imglib2.brno_learnathon.s2_try_yourself_imglib2;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class t3_FillingResultImage {
	private static
	boolean areSizesTheSame(final RandomAccessibleInterval<?> firstImg,
	                        final RandomAccessibleInterval<?> secondImg) {
		if (firstImg.numDimensions() != secondImg.numDimensions()) return false;
		for (int n = 0; n < firstImg.numDimensions(); ++n)
			if (firstImg.dimension(n) != secondImg.dimension(n)) return false;
		return true;
	}

	//intentionally extends FloatType not to spoil solution, replace with something reasonable
	public static <T extends FloatType>
	void pixelWiseSqrt1(final RandomAccessibleInterval<T> input,
	                    final RandomAccessibleInterval<T> output) {
		if (!areSizesTheSame(input,output))
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
		if (!areSizesTheSame(input,output))
			throw new IllegalArgumentException("The given input and output images are not of the same size.");

		final RandomAccess<T> ra_in = input.randomAccess();
		//since both are RAIs, we convert the *output* (as this is the one that
		//needs to be filled) into II and use it for driving the iteration
		final Cursor<T> lc_out = Views.iterable(output).cursor();

		while (lc_out.hasNext()) {
			lc_out.next();
			ra_in.setPosition( lc_out ); //variable dimensionality covered here
			lc_out.get().setReal( Math.sqrt(ra_in.get().get()) );
		}
	}

	public static <T extends FloatType>
	void pixelWiseSqrt3(final RandomAccessibleInterval<T> input,
	                    final RandomAccessibleInterval<T> output) {
		if (!areSizesTheSame(input,output))
			throw new IllegalArgumentException("The given input and output images are not of the same size.");

		final RandomAccess<T> ra_in = input.randomAccess();
		//since both are RAIs, we convert the *output* (as this is the one that
		//needs to be filled) into II and use it for driving the iteration
		final Cursor<T> lc_out = Views.iterable(output).localizingCursor();

		while (lc_out.hasNext()) {
			lc_out.next();
			ra_in.setPosition( lc_out ); //variable dimensionality covered here
			lc_out.get().setReal( Math.sqrt(ra_in.get().get()) );
		}
	}

	public static <T extends FloatType>
	Img<T> cloneImage(final Img<T> input) {
		return input.factory().create( input );
	}

	public static <T extends FloatType>
	RandomAccessibleInterval<T> pixelWiseCloneThenSqrt1(final Img<T> input,
	                                                    final RandomAccessibleInterval<T> output) {
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
	RandomAccessibleInterval<T> pixelWiseCloneThenSqrt2(final Img<T> input,
	                                                    final RandomAccessibleInterval<T> output) {
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
	RandomAccessibleInterval<T> pixelWiseCloneThenSqrt3(final Img<T> input,
	                                                    final RandomAccessibleInterval<T> output) {
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

		final Img<T> cloned = cloneImage(input);

		t = tic();
		pixelWiseCloneThenSqrt1(input, cloned);
		tac(t, "  RAI,new, okay loop with localizing cursor");

		t = tic();
		pixelWiseCloneThenSqrt2(input, cloned);
		tac(t, "  RAI,new, II-II loop with non-localizing cursors");

		t = tic();
		pixelWiseCloneThenSqrt3(input, cloned);
		tac(t, "  RAI,new, II-II loop with non-localizing cursors, flatIterable");
	}

	public static void main(String[] args) {
		Img<FloatType> arrayImg = ArrayImgs.floats(1024,1024,100);
		Img<FloatType> cellImg = new CellImgFactory<>(new FloatType(), 100,100,50).create(1024,1024,100);

		doExperiment(arrayImg, cellImg);
		doExperiment(cellImg, arrayImg);
	}

	private static long tic() {
		return System.currentTimeMillis();
	}
	private static double tac(long tic, final String msg) {
		double timeSpan = (double)(System.currentTimeMillis() - tic) / 1000.0;
		System.out.println(msg+" took "+timeSpan+" millis");
		return timeSpan;
	}
}
