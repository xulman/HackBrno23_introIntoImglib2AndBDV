package net.imglib2.brno_learnathon.s2_try_yourself_imglib2;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.img.planar.PlanarImgs;
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

		Cursor<FloatType> c = img.localizingCursor();
		while (c.hasNext()) {
			c.next().set( imageData6x5[ c.getIntPosition(1)*6 + c.getIntPosition(0) ] );
		}
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
