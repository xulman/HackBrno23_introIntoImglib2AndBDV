package net.imglib2.brno_learnathon.s3_study_imglib2;

import bdv.util.Bdv;
import bdv.util.BdvFunctions;
import bdv.util.BdvStackSource;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.brno_learnathon.s2_try_yourself_imglib2.t4_HandlingDimensionalityExample;
import net.imglib2.img.Img;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class t2_RealViews {
	//take i2k2020/intro/Task9_RealViews

	public static <T extends RealType<T>>
	void introduceInterpolation(final RandomAccessibleInterval<T> image) {
		//example how to get from integer-domain to a real-domain,
		//that calls for extending first (issues at the original boundary)
		RealRandomAccessible<T> interpolatedImage = Views.interpolate(
				Views.extendZero(image),
				new NLinearInterpolatorFactory<>()
			);

		BdvStackSource<T> bdv = BdvFunctions.show(image, "Original integer-grid image");
		BdvFunctions.show(
				interpolatedImage,   //source data
				image,               //interval around it
				"Interpolated real-grid image", Bdv.options().addTo( bdv.getBdvHandle() ));
	}

	//example how to obtain an affine transformed version of the image (rotate, scale)

	public static void main(String[] args) {
		final Img<FloatType> image = t4_HandlingDimensionalityExample.get3dImageWithPattern();

		introduceInterpolation(image);
	}
}
