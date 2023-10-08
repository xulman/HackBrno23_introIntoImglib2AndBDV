package net.imglib2.brno_learnathon.s1_study_imglib2_basics;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.planar.PlanarImgs;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

import net.imagej.ImageJ;
import net.imglib2.img.display.imagej.ImageJFunctions;
import bdv.util.BdvFunctions;

import java.io.IOException;

public class t4_ShowAndSaveImage {
	public static RandomAccessibleInterval<UnsignedByteType> createImageWithPattern() {
		final Img<UnsignedByteType> gray8Image = PlanarImgs.unsignedBytes(200,200,3);
		gray8Image.forEach(p -> p.set(64));
		Views.interval(gray8Image, new long[] {40,40,1}, new long[] {159,159,1}).forEach(p -> p.set(128));
		Views.interval(gray8Image, new long[] {80,80,1}, new long[] {119,119,1}).forEach(p -> p.set(255));
		return gray8Image;
	}

	public static <T extends NumericType<T>>
	void displayImageDifferently(final RandomAccessibleInterval<T> img, final ImageJ sharedIJ2) {

		//Show our image using the traditional ImageJ way.
		ImageJFunctions.show(img, "Imglib2 image inside ImageJ1 window");

		//Show our image using the modern ImageJ2 way.
		sharedIJ2.ui().show("ImgLib2 image inside ImageJ2 window", img);

		//Show our image using the BigDataViewer.
		//Note that
		BdvFunctions.show(img, "Imglib2 image inside BigDataViewer window");
	}

	public static <T extends NativeType<T>>
	void saveImage(final RandomAccessibleInterval<T> img,
	               final String filename,
	               final ImageJ sharedIJ2) {
		try {
			sharedIJ2.io().save(img, filename);
			System.out.println("Saved the file "+filename);
		} catch (IOException e) {
			System.out.println("Failed saving the file "+filename+" because of: "+e.getMessage());
		}
	}

	public static void main(String[] args) {
		//This is a reference image to be displayed (with concentric rectangular stripes).
		final RandomAccessibleInterval<UnsignedByteType> gray8Image = createImageWithPattern();

		//Start and show the ImageJ main window.
		ImageJ ij = new ImageJ();
		ij.ui().showUI();

		displayImageDifferently(gray8Image, ij);

		//---------------->
		//Please, modify the path to suit your situation....
		final String filename = "/temp/squares.tif";
		//---------------->

		saveImage(gray8Image, filename, ij);
		//Note that the saved image is a 3D 8-bit image and the squares
		//are only in the middle z-slice, some basic image viewers may
		//show only gray nothing... (the first empty z-slice).
	}
}
