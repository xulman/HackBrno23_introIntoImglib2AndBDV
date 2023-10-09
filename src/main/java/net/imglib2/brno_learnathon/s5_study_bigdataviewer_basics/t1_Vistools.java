package net.imglib2.brno_learnathon.s5_study_bigdataviewer_basics;

import bdv.util.Bdv;
import bdv.util.BdvFunctions;
import bdv.util.BdvSource;
import java.util.Random;
import net.imglib2.brno_learnathon.scaffold.LearnathonHelpers;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class t1_Vistools {
	//simple fly through vistools.... basically to inform they do exists
	//and that there is BDVfunctions and BDVOptions

	public static void main( String[] args )
	{
		// load example image (8-bit single channel 3D image, 256 x 256 x 129)
		final Img< UnsignedByteType > img = LearnathonHelpers.openImageResource( "/t1-head.tif" );

		// Show it in BigDataViewer
		Bdv bdv = BdvFunctions.show(img, "t1-head");

		// All BdvFunctions methods will return some instance of Bdv which is a
		// handle to the BDV instance and can be used for example to close the
		// BDV window using bdv.close().
		//
		// Another use is to add more stuff to the same window by specifying an
		// additional option Bdv.options().addTo(bdv).
		//
		// Here we add a random 3D ARGB image:
		Random random = new Random();
		Img< ARGBType > img2 = ArrayImgs.argbs(100, 100, 100);
		img2.forEach(t -> t.set(random.nextInt()));
		BdvSource img2Source = BdvFunctions.show( img2, "random pixels",
				Bdv.options().addTo( bdv ) );

		// More specifically, the return value of most BdvFunctions methods is BdvSource.
		// BdvSource extends Bdv, so it is also a handle to the BDV instance.
		// However, it is also a handle to the particular content added, or
		// rather its representation inside BDV.
		// We can set visibility, colors, brightness range, etc. For example:
		img2Source.setDisplayRange( 0, 512 );
		// We can also use the handle to remove data from a BDV window:
		// img2Source.removeFromBdv();

		// Via bdv.getBdvHandle() you can get access to the BDV ViewerPanel and
		// SetupAssignments allowing more fine-grained manipulations of BDV state.
	}
}
