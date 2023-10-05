package net.imglib2.brno_learnathon.scaffold;

import ij.IJ;
import ij.ImagePlus;
import java.io.File;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

public class LearnathonHelpers
{
	/**
	 * Open an image from the given resource.
	 * <p>
	 * It is not checked, that the resource image is of the expected type {@code T}
	 * which might lead to problems later. To verify the type early on, it is
	 * recommended to use the {@link #openImageResource(String, NumericType)}
	 * method instead.
	 *
	 * @param resourceName the name of the image resource
	 * @return the loaded image
	 * @param <T> pixel type of the image
	 */
	public static < T extends NumericType< T > & NativeType< T > > Img< T > openImageResource( String resourceName )
	{
		final ImagePlus imp = IJ.openImage( getResourcePath( resourceName ) );
//		return ImageJFunctions.wrap( imp );
		return ImagePlusAdapter.wrapImgPlus( imp );
	}

	/**
	 * Open an image from the given resource.
	 * <p>
	 * The resource is expected to contain an image of the specified {@code
	 * type}. If the actual image type does not match, an exception is thrown.
	 *
	 * @param resourceName the name of the image resource
	 * @param expectedType expected type of the image
	 * @return the loaded image
	 * @param <T> pixel type of the image
	 */
	public static < T extends NumericType< T > & NativeType< T > > Img< T > openImageResource( String resourceName, T expectedType )
	{
		final ImagePlus imp = IJ.openImage( getResourcePath( resourceName ) );
		switch ( imp.getType() )
		{
		case ImagePlus.GRAY8:
			if ( !( expectedType instanceof UnsignedByteType ) )
				throw new RuntimeException( "trying to open \"" + resourceName + "\" as " + expectedType.getClass().getSimpleName() + ", but the image is UnsignedByteType" );
			break;
		case ImagePlus.GRAY16:
			if ( !( expectedType instanceof UnsignedShortType ) )
				throw new RuntimeException( "trying to open \"" + resourceName + "\" as " + expectedType.getClass().getSimpleName() + ", but the image is UnsignedShortType" );
			break;
		case ImagePlus.GRAY32:
			if ( !( expectedType instanceof FloatType ) )
				throw new RuntimeException( "trying to open \"" + resourceName + "\" as " + expectedType.getClass().getSimpleName() + ", but the image is FloatType" );
			break;
		case ImagePlus.COLOR_RGB:
			if ( !( expectedType instanceof ARGBType ) )
				throw new RuntimeException( "trying to open \"" + resourceName + "\" as " + expectedType.getClass().getSimpleName() + ", but the image is FloatType" );
			break;
		default:
		case ImagePlus.COLOR_256:
			throw new RuntimeException( "trying to open \"" + resourceName + "\" as " + expectedType.getClass().getSimpleName() + ", but the image is ARGBType" );
		}
//		return ImageJFunctions.wrap( imp );
		return ImagePlusAdapter.wrapImgPlus( imp );
	}

	private static String getResourcePath( String resourceName )
	{
		try
		{
			return new File( LearnathonHelpers.class.getResource( resourceName ).toURI() ).getAbsolutePath();
		}
		catch ( Exception e )
		{
			throw new RuntimeException( "Error Getting Image: " + resourceName, e );
		}
	}
}
