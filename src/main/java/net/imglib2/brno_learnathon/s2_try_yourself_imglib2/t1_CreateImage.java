package net.imglib2.brno_learnathon.s2_try_yourself_imglib2;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.img.planar.PlanarImgs;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.real.FloatType;

public class t1_CreateImage {

	static final float[] imageData6x5 = {
			25, 25, 25, 25, 25, 25,
			25, 10, 10, 10, 10, 25,
			25, 10, 50, 50, 10, 25,
			25, 10, 10, 10, 10, 25,
			25, 25, 25, 25, 25, 25
	};

	public static Img<FloatType> createArrayImgs() {
		//Try to create Array-backed image with the 'imageData6x5' pixel data
		return ArrayImgs.floats(imageData6x5, 6, 5);
	}

	public static Img<FloatType> createPlanarImgs() {
		//Try to create Planar(Planes)-backed image with the 'imageData6x5' pixel data
		Img<FloatType> img = PlanarImgs.floats( 6, 5);
		int offset = 0;
		for (FloatType p : img) p.set( imageData6x5[offset++] );
		return img;
	}

	public static Img<FloatType> createCellImgs() {
		//Try to create Cells-backed image with the 'imageData6x5' pixel data
		//PS: Unlike in the Array- and Planar-based images, here the "geometry
		//of the backend" is less fixed, it is more variable, and thus a separate
		//(factory) class was devised that is aware of the geometry and can create
		//new image objects that are based on this geometry. Look thus for a factory
		//class that builds images of the Cells-backend...
		CellImgFactory<FloatType> factory = new CellImgFactory<>(new FloatType(), 3, 5);
		Img<FloatType> img = factory.create(6, 5);

		//Attention! These two lines fill the image incorrectly!
		int offset = 0;
		for (FloatType p : img) p.set( imageData6x5[offset++] );

		//Correct version, one has to take care of fetching the right values
		//(but the solution is of a fixed dimensionality -- not a great karma in ImgLib2 world)
		Cursor<FloatType> c = img.localizingCursor();
		while (c.hasNext()) {
			c.next().set( imageData6x5[ c.getIntPosition(1)*6 + c.getIntPosition(0) ] );
		}

		//If one can afford it (meaning the data is "sufficiently small"),
		//wrapping data in ArrayImg, we can avoid the explicit coordinate calculations (and
		//fixing the dimensionality of the solution).
		Cursor<FloatType> c2 = img.localizingCursor();
		RandomAccess< FloatType > ra = ArrayImgs.floats( imageData6x5, 6, 5 ).randomAccess();
		while (c2.hasNext()) {
			//THIS IS A GREAT PATTERN
			//...'cause it does not require us to create an auxiliary array to store-and-forward coordinate
			c2.next().set( ra.setPositionAndGet( c2 ) );
		}

		//Other options include LoopBuilder -- a utility for synchronized (and convenient) sweeping over images.
		LoopBuilder.setImages( ArrayImgs.floats( imageData6x5, 6, 5 ), img )
				.forEachPixel( ( i, o ) -> o.set( i ) );

		return img;
	}

	public static void main(String[] args) {
		Img<FloatType> img;

		img = createArrayImgs();
		ImageJFunctions.show(img, "ArrayImg");

		img = createPlanarImgs();
		ImageJFunctions.show(img, "PlanarImg");

		img = createCellImgs();
		ImageJFunctions.show(img, "CellImg");
	}
}
