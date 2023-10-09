package net.imglib2.brno_learnathon.s3_study_imglib2;

public class t1_Views {
	public static void main(String[] args) {
		//Views & Interpolation,
		//(fasten image processing with ROIS... via Views.Interval())


		//Views - abstract image with on-demand, on-the-fly computed values,
		//		 typically constructed over some "normal image",

		//		 they basically hide away the "technicalities" of
		//		 the on-the-fly computing allowing the programmer to focus
		//		 on the actual work with the image, getting a pixel
		//		 value should be done "simply like always" - convenience

		//Popular use-cases:

		//- extending the image with certain boundary while preserving
		//its iterability domain... sweeps over the original image but
		//can fetch pixels from outside

		//example that interval and getting values is not firmly connected:
		//
		//expands .. enlarges the Interval, does not remove the Interval
		//extend .. remove Interval, making the source potential of infinite spatial extent
		//          leaving the caller to wrap with Views.Interval to reintroduce it

		//- multiple views on the same image for parallel sweeping

		//- limiting the view, setting up a ROI, or removing/permuting the dimensions

		//- turing RAI into II (adding iterability)

		//explore yourself
		//what's the difference between expand and extend?
	}
}
