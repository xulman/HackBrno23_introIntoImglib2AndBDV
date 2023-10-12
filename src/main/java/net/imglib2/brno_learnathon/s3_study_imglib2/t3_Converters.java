package net.imglib2.brno_learnathon.s3_study_imglib2;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.brno_learnathon.scaffold.LearnathonHelpers;
import net.imglib2.converter.Converters;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class t3_Converters {

	/**
	 * Show how to use virtual converters to transform types (read-only) and
	 * perform pixel-wise computation, a cosine of the square root in this case
	 *
	 * @param img
	 */
	public static < T extends RealType< T > > void displayCosine( final RandomAccessibleInterval< T > img )
	{
		// First we virtually convert to the image to FloatType.
		//
		// "Virtually" means that the conversion is done on-the-fly when a pixel
		// is accessed. So there is no memory required to store the converted
		// image.
		//
		// The following does the conversion by hand:
		// We specify
		//   - the source img,
		//   - a Converter (which is just a BiConsumer, but was there before
		//     BiConsumer was added to Java). The converter takes two Types,
		//     the input and the output.
		//   - an instance of the output type.
		RandomAccessibleInterval< FloatType > floatImg = Converters.convert( img, ( i, o ) -> o.set( i.getRealFloat() ), new FloatType() );
		// Converting between RealTypes is very common, so there is a shortcut
		// for this. We could have equivalently done:
		// RandomAccessibleInterval< FloatType > converted = RealTypeConverters.convert( img, new FloatType() );

		// Now, use another converter to apply the function
		//   y = cos(sqrt(x))
		// to the pixel values. The output type should be DoubleType.
		RandomAccessibleInterval< DoubleType > result =
				Converters.convert( floatImg, ( i, o ) -> o.set( Math.cos( Math.sqrt( i.get() ) ) ), new DoubleType() );

		ImageJFunctions.show( result );
	}

	public static void main(String[] args) {

		// The displayCosine() method works on all RealTypes.
		//
		// Let's test it on the UnsignedByteType blobs.tif image
		RandomAccessibleInterval< UnsignedByteType > blobs = LearnathonHelpers.openImageResource( "/blobs.tif", new UnsignedByteType() );
		displayCosine( blobs );

		// The clown.png image is ARGBType.
		RandomAccessibleInterval< ARGBType > clown = LearnathonHelpers.openImageResource( "/clown.png", new ARGBType() );
		// ARGBType is not a RealType. (It's not Comparable, it cannot be read as a double value, etc).
		// So we cannot apply displayCosine() directly.
		//   displayCosine( clown );
		// does not compile.
		//
		// We can however use another converter to transform the image into a suitable type.
		// For example, we can extract the green channel as an UnsignedByteType image:
		final RandomAccessibleInterval< UnsignedByteType > green =
				Converters.convert( clown, ( i, o ) -> o.set( ARGBType.green( i.get() ) ), new UnsignedByteType() );
		displayCosine( green );

		// Converters are read-only, e.g., writing to the "green" image above
		// will not change "clown". For that, we can use writable converters:
		writeConverter1(clown);
	}


	/**
	 * Using a writable converter to set all red values of an ARGB image to 0
	 *
	 * @param img
	 */
	public static void writeConverter1(final RandomAccessibleInterval< ARGBType > img) {

		ImageJFunctions.show(img).setTitle("original");

		// Write-conversion is trickier than read-only, and only works for
		// certain NativeTypes. Some useful pre-defined write-converters are
		// provided in the Converters utility class. This extracts the red
		// channel (channel index 1) of img as a writable UnsignedByteType image:
		final RandomAccessibleInterval< UnsignedByteType > redChannel =
				Converters.argbChannel( img, 1 );

		// Writing to redChannel changes the img values!
		Views.iterable( redChannel ).forEach( red -> red.set( 0 ) );

		// We have to display img again to see the changes.
		ImageJFunctions.show(img).setTitle("red=0");

		// To create a custom write-conversion, one needs to define a
		// SamplerConverter<A,B>. If you are interested, have a look at the
		// implementation of Converters.argbChannel(...)
	}
}
