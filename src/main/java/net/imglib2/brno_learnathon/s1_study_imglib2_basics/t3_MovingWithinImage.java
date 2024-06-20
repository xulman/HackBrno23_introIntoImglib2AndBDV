package net.imglib2.brno_learnathon.s1_study_imglib2_basics;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.planar.PlanarImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

public class t3_MovingWithinImage {

	public static <T extends RealType<T>>
	void readAndSetSomePixels(final Img<T> image) {
		//This is the most ideal setting:
		//This is a method that gets a "normal image" as a parameter
		//and does some work on its pixels. For example, it visits all
		//pixels and increases their value by 5:
		for (T pixel : image) pixel.setReal( pixel.getRealDouble() + 5 );
		//or
		image.forEach(pixel -> pixel.setReal( pixel.getRealDouble() + 5 ));
		//or, with an explicit iterating "cursor":
		Cursor<T> cursor = image.cursor();
		while (cursor.hasNext()) {
			T pixel = cursor.next();
			pixel.setReal( pixel.getRealDouble() + 5 );
		}
		//(Notice that the cursor requires using next() before any actual use after
		// the initialization)
		//
		//Also notice that we didn't have to even think about the actual dimensionality
		//of the image that is being iterated over. The cursor just visited all pixels in
		//the image and made sure no pixel was accessed twice; but(!) the order in
		//which the pixels were visited is not defined -- every image backend has
		//the choice to iterate in an order that's optimal for it
		moreOnIteratingUsingCursors(image);


		//On the other hand, to a) deal with the actual pixel positions explicitly
		//and b) to have a full control over which pixel will be accessed now,
		//some(!) images offer random position "accessors":
		RandomAccess<T> accessor = image.randomAccess();

		//The accessor by default points "to nowhere", to an illegal image position,
		//it must be always set before the actual use:
		int[] coordinate = new int[ image.numDimensions() ]; //Java autofills with zeros
		accessor.setPosition( coordinate );
		//
		//or set position in only one dimension (here the 3rd dimension):
		accessor.setPosition( coordinate[2], 2 );
		//
		//after positioned, follows the usual work with pixel type:
		accessor.get().getRealDouble();

		//This positions and fetches the pixel in one call, and then fetches the value:
		accessor.setPositionAndGet( coordinate ).getRealDouble();

		//Besides (re)setting the accessor position (as shown above),
		//one can ask to have it advanced along the given dimension.
		//But beware, no image bounds are checked.
		accessor.fwd(0);
		accessor.fwd(1);
		accessor.fwd(1);
		accessor.fwd(2);

		//Where is the accessor now?
		accessor.localize( coordinate );

		//This is another convenient shortcut, if one knows the dimensionality:
		if (image.numDimensions() == 5) accessor.setPositionAndGet(0,0,0,0,0);

		//Btw, there exists even a shorter shortcut ;)....:
		image.getAt( coordinate ).getRealDouble();
		//But it internally always(!) creates a new RandomAccessor,
		//so use this only "safely", e.g., not in loops.
	}

	public static <T extends RealType<T>>
	void moreOnIteratingUsingCursors(final Img<T> image) {
		//While we (normally) cannot influence the iteration order,
		//we still query the position where the cursor currently is.
		int[] coordinate = new int[ image.numDimensions() ];

		//There are in fact two cursors available:
		Cursor<T> fasterMovingCursor = image.cursor();
		Cursor<T> positionAwareCursor = image.localizingCursor();
		//They both visit the image, in the same order etc.
		//The only difference is that the latter is doing some extra
		//housekeeping (which takes a tiny bit more of time) to be able
		//to faster answer where it is currently positioned within the image.
		//The former can still tell, but the query is more expensive.
		int moves = 6;
		while (fasterMovingCursor.hasNext() && moves > 0) {
			fasterMovingCursor.next();
			moves--;
		}
		fasterMovingCursor.localize( coordinate );
		System.out.println(" (Normal) Cursor reports position: "+Util.printCoordinates(coordinate));
		//
		moves = 6;
		while (positionAwareCursor.hasNext() && moves > 0) {
			positionAwareCursor.next();
			moves--;
		}
		positionAwareCursor.localize( coordinate );
		System.out.println("localizingCursor reports position: "+Util.printCoordinates(coordinate));

		//If for whatever reason one needs to "bookmark" the current position
		//during the image sweeping, one can copy() the cursor and pass it to
		//a specialized related method -- the cursor also knows/remembers all
		//it needs in order to conduct the sweep, the reference on the original
		//image itself is not needed for the sweep.
		Cursor<T> anotherSuchCursor = positionAwareCursor.copy();
		continueScanningTheImageFromHere(anotherSuchCursor);
		//(good catch! The function doesn't really need a localizingCursor, I know...)
		//
		//Show that the original cursor hasn't changed but the copy did change:
		System.out.println("original Cursor reports position: "+Util.printCoordinates(positionAwareCursor));
		System.out.println("  cloned Cursor reports position: "+Util.printCoordinates(anotherSuchCursor));
		//
		//Btw, have you noticed the shortcut?   (Cursors implement also Localizable and there's
		//a print function for it, so we no longer need cursor.localize() and then print it)
	}

	public static <T extends RealType<T>>
	void continueScanningTheImageFromHere(final Cursor<T> cursor) {
		//Just run to the very end of the image.
		while (cursor.hasNext()) cursor.next();
	}

	public static <T extends RealType<T>>
	void canIterateOnlyOverTheImage(final IterableInterval<T> image) {
		Cursor<T> c = image.cursor();
		//RandomAccess<T> ra = image.randomAccess(); //impossible
	}

	public static <T extends RealType<T>>
	void canOnlyJumpRandomlyOverTheImage(final RandomAccessibleInterval<T> image) {
		//Cursor<T> c = image.cursor(); //impossible
		RandomAccess<T> ra = image.randomAccess();
	}

	public static void main(String[] args) {
		final Img<UnsignedShortType> gray16Image = PlanarImgs.unsignedShorts(200,200,3);
		readAndSetSomePixels( gray16Image );

		//Soooo, we saw the two fundamental access concepts:
		// "I am a passenger, the image backend drags me around the full image
		//  in a reasonable way, and my code is thus somewhat light-weight."
		// vs.
		// "I am active, I decide where to move in the image, and likely I don't
		//  even intend to iterate over the image, I just peek at (random) places."

		//When the programmer needs to use both of them at the same time,
		//the Img<T> interface is expected to be used. However, methods often
		//need only one of the two, most frequently probably the case of iterating
		//over the full image (or portion of it, will be shown later).
		//
		//This is reflected in the design of ImgLib2 as the Img<T> is in fact
		//a composition of RandomAccessibleInterval<T> and IterableInterval<T>.
		canIterateOnlyOverTheImage(gray16Image);
		canOnlyJumpRandomlyOverTheImage(gray16Image);
		//Notice the methods' signatures.

		//This is an important concept, that an image can offer
		//only "portion" of "being a normal image" when "normal"
		//means being of integer (discrete), regular grid-based geometry.

		//Developing a method that requires an image with "relaxed" requirements,
		//e.g., by alleviating the iterability when accepting only RandomAccessible
		//images, it opens up this method to more various (and exotic) backends.
		//
		//What can "more various backends" be? Imagine, as a simple example, one assigns an
		//interpolator (a piece of code that knows how to fetch off-grid pixels, pixels from
		//real valued coordinates) to an image and obtains this way an enhanced image that
		//knows its value for any real coordinate but looses (obvious, natural) iterability.
		//Such image basically emulates a continuous function......
		//....whose domain may remain bounded, or not.
		//
		//Sorry, it got complicated, was intentional, read on, this could come in handy:

		//In forums, in writings, one often finds RAI and II instead of their full names
		//(check their first letters...). But the full (and long) names are not here to annoy
		//the programmer. The names are here to help because they (and their combinations)
		//disclose access properties of images and data containers.
		//
		//It may be advantageous to step above (abstract from images) and think of
		//generic data containers with addressable content/elements such as image that is
		//made of pixels at coordinates, or cloud of points at coordinates, or a pure
		//storage device with sectors to hold the data etc. Note that the ImgLib2 and
		//affiliate libraries are dealing today with more than just pixels and images.

		//Iterable:
		//
		//Says only that all elements of the underlying object (again, be it
		//voxels in an image, or points of a point cloud) can be naturally visited, one by
		//one. Notice that (existing) elements are visited, it's not about visiting all
		//possible coordinates! It is very much reminiscent of the paradigm of a linked list data
		//structure, or tape (sequential) storage (that rewinds over empty sectors).
		//Anyway, such an object may be used in a for-each loop (e.g., "for px : image").

		//RandomAccessible:
		//
		//Says that one can work on (can access) available elements in
		//any order (incl. random order). This comes with the necessity to be able to "point
		//at the element", to address it. The parallel may be an array data structure, or
		//random access memory (RAM) chips where one _directly retrieves_ a value at random
		//position. Point cloud (whose points reside at arbitrary coordinates) is thus not
		//an instance of RandomAccessible (how would one directly request a Point without
		//knowing its coordinate and without scanning a list until the point is found?).
		//Images, on the other hand, are a great example because they typically assume some
		//underlying grid and with it comes an implicit addressing scheme. Needless to say,
		//the addressing scheme here recognizes only integer coordinates (how to read at 2.5
		//sector when a full sector is the minimum unit one can read/write?)
		//
		//The notion of being able to randomly access a unit of data is not purely only in
		//the freedom of the choice itself. Realizing what's the difference in what it takes
		//to access a fifth element in a LinkedList vs. ArrayList, that's also a very valid
		//view angle on the iterability (LinkedList) and random accessibility (ArrayList).

		//Real(spatialSomething):
		//
		//The ImgLib2 was designed around images, which were
		//understood as discrete samples (of some visual reality), and thus spatial data
		//(like coordinates, lengths etc.) were predominantly considered to reside in the
		//integer domain. The keyword "Real" is here often understood as a "preposition" (to
		//coordinates, lengths etc.) and flags that the real numbers domain is available
		//in that particular case. Notable examples are:
		//
		//RealRandomAccessible - Flags that the (implicit) addressing scheme (for images)
		//now accepts also real-valued coordinates. This typically comes in conjunction with
		//some interpolation scheme that is the workhorse to fetch the off-grid pixels. But
		//that's already an instance of a principle, in which the "Real"-part is referring
		//mainly to the ability to be able to always provide an on-demand, on-the-fly established
		//element and its value (recall the "2.5 sector" example: one usually cannot store
		//all data from all real-valued coordinates).
		//
		//RealInterval - upgrades the resolution of the Interval (see below) because here
		//the bounds are real numbers (not only integers).

		//Interval:
		//
		//Informs that it is possible to construct a (typically multi-dimensional)
		//bounding box around the content, and the Interval represents such box. The
		//Interval explains a particular span of coordinates, it defines only the limits of
		//the span, it does not promise availability of elements at all possible coordinates
		//within the Interval. Realize that since "normal image" spans a finite grid, the
		//image itself is thus also an Interval -- also notice the word Interval is part of
		//the RAI and II names.

		//Btw, did you notice that one can "easily emulate" iterability from
		//random accessibility, while the opposite direction is more difficult?
		//Besides, by adding computational constructions on top of the "normal images"
		//(such as in the above example with interpolation), it is easier to lose
		//iterability than random accessibility.
		//
		//In practice, we are making use of this observation a lot and work
		//mainly with RandomAccessibleInterval (instead of with full Img)
		//
		//(for the record: iterability can be "re-introduced" with Views.iterable())
		RandomAccessibleInterval<UnsignedShortType> rai = gray16Image;
		canIterateOnlyOverTheImage( Views.iterable(rai) );


		//There is a lot of interfaces in the ImgLib2. Allow us to mention two more, frequently
		//(but somewhat invisibly) used interfaces that are also very relevant to the topic
		//of moving within an image:

		//Localizable:
		//
		//It is a property of an object that the object can always provide its n-dimensional
		//integer coordinate. It knows where it is, can localize itself. Cursor and RandomAccess,
		//for example, implement Localizable.

		//Positionable, RealPositionable:
		//
		//A property of an object that it can be placed, positioned, assigned a coordinate,
		//be it integers-only or real coordinate. Cursor, since it cannot be user-side positioned,
		//is not an example of Positionable, but RandomAccess is.
	}
}
