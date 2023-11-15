package net.imglib2.brno_learnathon.s5_study_bigdataviewer_basics;

import bdv.util.Bdv;
import bdv.util.BdvFunctions;
import bdv.util.BdvSource;
import java.util.Random;

import net.imglib2.RealRandomAccessible;
import net.imglib2.brno_learnathon.scaffold.LearnathonHelpers;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.position.FunctionRealRandomAccessible;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

public class t1_Vistools {
	//simple fly through vistools.... basically to inform they do exists
	//and that there is BDVfunctions and BDVOptions

	public static void main( String[] args )
	{
		// load example image (8-bit single channel 3D image, 256 x 256 x 129)
		final Img< UnsignedByteType > img = LearnathonHelpers.openImageResource( "/t1-head.tif" );

		// Show it in BigDataViewer
		Bdv bdv = BdvFunctions.show(img, "t1-head");

		RealRandomAccessible<UnsignedByteType> sphere =
		new FunctionRealRandomAccessible<>( 3, (pos, t) -> {
			double x = pos.getDoublePosition(0) - 128;
			double y = pos.getDoublePosition(1) - 128;
			double z = pos.getDoublePosition(2) - 64;
			t.set( Math.sqrt(x*x + y*y + z*z) < 30 ? 255 : 0);
		}, UnsignedByteType::new);

//		BdvFunctions.show(sphere, img, "sphere", Bdv.options().addTo(bdv));

		RealRandomAccessible<UnsignedByteType> interpolated = Views.interpolate(Views.extendBorder(img), new NLinearInterpolatorFactory<>());
		BdvFunctions.show(interpolated, Intervals.createMinSize(0,0,0, 20, 20,20), "interpolated", Bdv.options().addTo(bdv));
	}
}
