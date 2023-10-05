package sc.scijava.tutorials.scaffold;

import bdv.util.BdvFunctions;
import bvv.vistools.BvvFunctions;
import net.imagej.ImageJ;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class LoadAndDisplay
{

	public static void main( String[] args )
	{
		final Img< UnsignedByteType > img = LearnathonHelpers.openImageResource( "/t1-head.tif", new UnsignedByteType() );

		// Show img in IJ1
		//
		ImageJFunctions.show( img );

		// Show img in BigDataViewer
		//
		BdvFunctions.show( img, "image" );

		// Show img in BigVolumeViewer
		//
		// -- Nice effect to show that the API is exactly the same as BigDataViewer.
		//    But that's it more or less. I would maybe show this once, and then
		//    just use IJ1 or BDV for the tutorials.
		//
		BvvFunctions.show( img, "image" );

		// Show img in IJ2
		//
		// -- We cannot use the legacy ui, because the legacy-patcher doesn't work on more recent java versions.
		//    So, I'm not sure, whether it makes much sense to include this here?
		//
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();
		ij.ui().show( img );
	}
}
