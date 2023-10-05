package net.imglib2.brno_learnathon.s1_study_imglib2_basics;

import net.imglib2.brno_learnathon.scaffold.LearnathonHelpers;
import net.imglib2.img.Img;
import net.imglib2.img.planar.PlanarImgs;
import net.imglib2.type.BooleanType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.logic.BoolType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.Unsigned12BitType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

public class t2_PixelTypes {

	public static void onTypesHierarchy() {
		//ImgLib2 utilizes a wealth of pixel types, and a hierarchy of them:

		//Examples of types that represent the elementary computing types,
		//like byte, short, int, float... and their unsigned flavours
		ByteType byteType = new ByteType();
		UnsignedIntType unsignedIntType = new UnsignedIntType();
		DoubleType doubleType = new DoubleType();
		//
		//Btw, the signed and unsigned variants of otherwise the same type
		//are different only at compile time because they are simply of different types.
		//For example, ByteType and UnsignedByteType are always 8 bits of a memory
		//and CPU treats both the same. Not to mention that Java language does not
		//recognize, e.g., an "unsigned byte"...

		//Also specific (microscopy) types of not bytes-multiple sizes, yet packed in memory...
		BitType bitType = new BitType();
		BoolType boolType = new BoolType();
		Unsigned12BitType unsigned12BitType = new Unsigned12BitType(); //optical microscope camera type :)

		//Take a look on type hierarchy in your IDE, e.g., Ctrl+H in IntelliJ, and you
		//would see why the following constructs are legal:
		NumericType<ByteType> n1 = byteType;
		NumericType<UnsignedIntType> n2 = unsignedIntType;
		NumericType<?> n3 = doubleType;

		//But also:
		RealType<ByteType> r1 = byteType;
		RealType<UnsignedIntType> r2 = unsignedIntType;
		RealType<?> realType = doubleType;

		//And even:
		IntegerType<ByteType> i1 = byteType;
		IntegerType<UnsignedIntType> integerType = unsignedIntType;
		//
		// IntegerType<?> i3 = doubleType; //but not this one!
		integerRealIntermezzo();

		//TODO: some conclusion to realize that types shares the same backbone
	}

	public static void integerRealIntermezzo() {
		//IntegerType extends RealType, it offers all what real numbers
		//can offer functionality-wise (!) (which is, btw, quite the opposite
		//to the mathematical, set theory-oriented, sense of their relationship)
		//plus it can provide an integer number (again, as a new functionality,
		//not increasing the domain of values that are possible to hold):
		IntType integerType = new IntType();
		DoubleType realType = new DoubleType();

		integerType.getRealFloat();
		integerType.getInteger();

		realType.getRealFloat();
		//realType.getInteger(); //ain't existing
		//one has to take care of it on your own, taking the "risk of rounding":
		int myIntegerFromPossiblyReal = (int)realType.getRealFloat();

		//but IntegerType accepts real inputs, but in order to withstand its name
		//it applies Util.round() (our own Util from the net.imglib2.util package)
		integerType.setReal(5.5);
		System.out.println("5.5 stored and retrieved from IntegerType is "+integerType.getInteger());
	}

	public static void noteOnGenerics() {
		//While Java generics allow to avoid repetitive blocks of code (one block for one particular pixel type),
		//the type represented in a generic, e.g. <T>, is visible/available only during the compile time.
		//At runtime, the T in <T> is replaced with Object, the basis of any Java non-elementary type.
		//In order to "preserve" the information about pixel type into runtime, one needs to maintain/keep
		//an existing object of that type. This is a strategy adopted, for example, in our type-safe loader:
		Img<ByteType> img1 = LearnathonHelpers.openImageResource("blobs.tif", new ByteType());

		//Alternatively, one hides, encodes essentially, the type information into the method's definition itself:
		Img<FloatType> img2 = PlanarImgs.floats(6, 4, 3);

		//This is again the first strategy:
		Img<ByteType> img3 = img2.factory().imgFactory(new ByteType()).create(7, 8, 9);
		//Here, an image of the same backend was wanted, but pixel type and image size was provided different.
	}

	public static void main(String[] args) {
		onTypesHierarchy();
		noteOnGenerics();
	}
}