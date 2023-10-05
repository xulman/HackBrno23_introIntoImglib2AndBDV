package net.imglib2.brno_learnathon.s1_study_imglib2_basics;

import net.imglib2.brno_learnathon.scaffold.LearnathonHelpers;
import net.imglib2.brno_learnathon.scaffold.LoadAndDisplay;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.img.planar.PlanarImgs;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

public class t1_CreateImage {

	public static void createImages() {
		//To create an image, one has to specify everything, every detail.
		//Normally, since image is a collection of values (represented with given pixel type)
		//spread over a grid of certain size (the image size). In ImgLib2 we have to additionally
		//specify the image's storage backend:
		//
		//format:
		//   storage_backend.pixel_type(image_size)
		ArrayImgs.unsignedShorts(5, 5);
		PlanarImgs.floats(6, 4, 3);
		//
		//Notice "pixel resolution" is not specified.

		//If you aks IDE to infer type, this is how it can end up....
		ArrayImg<UnsignedShortType, ShortArray> img1 = ArrayImgs.unsignedShorts(5, 5);
		PlanarImg<FloatType, FloatArray> img2 = PlanarImgs.floats(6, 4, 3);

		//However, this is the wanted pattern.
		//Also, because interface Img<> is used instead of the class.
		Img<UnsignedShortType> img3 = ArrayImgs.unsignedShorts(5, 5);
		Img<FloatType> img4 = PlanarImgs.floats(6, 4, 3);

		//a minimalistic proof that something has been created...
		System.out.println("img3 is a "+img3.numDimensions()+"-dimensional image of pixel type "
				+img3.firstElement().getClass().getSimpleName());
	}

	public static <T extends NativeType<T> & NumericType<T>> Img<ByteType> loadImages() {
		//Images can of course be also loaded in. But ImgLib2 is outsourcing this task
		//to external libraries. Here, for example, our helper loader leverages this
		//task on ImageJ loading routine -- it calls internally IJ.openImage("path")
		Img<?> img1 = LearnathonHelpers.openImageResource("blobs.tif");

		//Notice that the program itself cannot know in advance what will be
		//the pixel type of the opened image, and we thus have to live with <?>.
		//
		//But that's too generic, we wouldn't be able to do much with such
		//a type (more on it in the next lesson), so we can at least assume the type
		//is "native"... which means "normal"... which means "store-/load-able".
		//Hmm... still it is not implying that we can do any math on the pixel values, we
		//need to make sure that the pixel type is "numeric". Should come as no surprise that
		//the most frequent pixel types are both numeric and native... just like our type T here:
		Img<T> img2 = LearnathonHelpers.openImageResource("blobs.tif");

		//Often, however, the programmer can assume and request certain type. Still, the loading
		//should be done in a failsafe way, i.e., by checking the expected type (see inside the method).
		Img<ByteType> img3 = LearnathonHelpers.openImageResource("blobs.tif", new ByteType());

		return img3;
	}

	public static void main(String[] args) {
		createImages();
		Img<ByteType> imgB = loadImages();

		//What good is to have an image if you cannot look at it?
		//For now only, let's ask ImageJ to display it using the following convenience call:
		ImageJFunctions.show(imgB);
	}
}