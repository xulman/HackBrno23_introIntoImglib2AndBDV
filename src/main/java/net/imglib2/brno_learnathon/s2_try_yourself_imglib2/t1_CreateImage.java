package net.imglib2.brno_learnathon.s2_try_yourself_imglib2;

import net.imagej.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.cell.CellImg;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.img.planar.PlanarImgs;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.real.FloatType;

import java.io.IOException;

public class t1_CreateImage {

	/** This is supposed to represent a 2D image with 5 rows and 6 columns.
	 *  It could have been understood as 6x5x1 (or 6x5x1x1, or....) but we
	 *  wish to stay with just two (2) dimensions for now.
	 *
	 *  In the exercise, we will try to push this data into ImgLib2 images
	 *  implemented with various backends. It would go easy for some, and less
	 *  so for others....
	 */
	static final byte[] imageData6x5 = {
			25, 25, 25, 25, 25, 25,
			25, 10, 10, 10, 10, 25,
			25, 10, 50, 50, 10, 25,
			25, 10, 10, 10, 10, 25,
			25, 25, 25, 25, 25, 25
	};

	public static Img<ByteType> createArrayImgs() throws IOException {
		//Try to create Array-backed image with the 'imageData6x5' pixel data
		Img<ByteType> img = ArrayImgs.bytes(imageData6x5, 6, 5);
		//ImageJFunctions.show(img.copy(), "original image");

		ImageJ ij = new ImageJ();
		ij.ui().showUI();
		Img<ByteType> copyOfImg = img.copy();
		ij.ui().show("original image", copyOfImg );
		ij.io().save(copyOfImg, "/temp/original_image.tif");

		imageData6x5[0] = 127;
		ij.ui().show("modified image", img);
		ij.io().save(img, "/temp/modified_image.tif");
		return img;
	}

	public static Img<ByteType> createPlanarImgs() {
		//Try to create Planar(Planes)-backed image with the 'imageData6x5' pixel data
		Img<ByteType> img = PlanarImgs.bytes(6, 5);

		int offset = 0;
		for (ByteType px : img) {
			px.setInteger( imageData6x5[ offset++ ] );
		}

		return img;
	}

	public static Img<ByteType> createCellImgs() {
		//Try to create Cells-backed image with the 'imageData6x5' pixel data
		//PS: Unlike in the Array- and Planar-based images, here the "geometry
		//of the backend" is less fixed, it is more variable, and thus a separate
		//(factory) class was devised that is aware of the geometry and can create
		//new image objects that are based on this geometry. Look thus for a factory
		//class that builds images of the Cells-backend...
		CellImgFactory<ByteType> factory = new CellImgFactory<>(new ByteType(), 3, 5);
		Img<ByteType> cellImg = factory.create(6, 5);

		//Attention! These two lines fill the image incorrectly!
		int offset = 0;
		for (ByteType p : cellImg) p.set( imageData6x5[offset++] );

		//Correct version, one has to take care of fetching the right values
		//(but the solution is of a fixed dimensionality -- not a great karma in ImgLib2 world)
		Cursor<ByteType> c = cellImg.localizingCursor();
		while (c.hasNext()) {
			ByteType px = c.next();
			int x = c.getIntPosition(0);
			int y = c.getIntPosition(1);
			px.setInteger( imageData6x5[6*y + x] );
		}


		//If one can afford it (meaning the data is "sufficiently small"),
		//wrapping data in ArrayImg, we can avoid the explicit coordinate calculations (and
		//fixing the dimensionality of the solution).
		RandomAccess<ByteType> ra = ArrayImgs.bytes(imageData6x5, 6, 5).randomAccess();
		CellImg<FloatType, ?> cellImg2 = new CellImgFactory<>(new FloatType()).create(6, 5);
		Cursor<FloatType> c2 = cellImg2.localizingCursor();
		while (c2.hasNext()) {
			//...'cause it does not require us to create an auxiliary array to store-and-forward coordinate

			c2.fwd();
			ra.setPosition( c2 ); //plan A
			//
			c2.localize( ra ); //plan B
			//A.equals(B)


			c2.get().setInteger( ra.get().getInteger() ); //plan C
			//THIS IS A GREAT PATTERN
			c2.get().set( ra.get() ); //plan D
			//C.equals(D)
		}

		//Other options include LoopBuilder -- a utility for synchronized (and convenient) sweeping over images.

		return cellImg;
	}

	public static void main(String[] args) throws IOException {
		Img<ByteType> img;

		//img = createArrayImgs();
		//ImageJFunctions.show(img, "ArrayImg");

		img = createPlanarImgs();
		ImageJFunctions.show(img, "PlanarImg");

		img = createCellImgs();
		ImageJFunctions.show(img, "CellImg");
	}
}
