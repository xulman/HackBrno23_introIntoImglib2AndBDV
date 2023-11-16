package net.imglib2.brno_learnathon.s1_study_imglib2_basics;

import net.imglib2.brno_learnathon.scaffold.LearnathonHelpers;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.planar.PlanarImgs;
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
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import org.checkerframework.common.returnsreceiver.qual.This;

public class t2_PixelTypes {

	public static void onTypesHierarchy() {
		//ImgLib2 utilizes a wealth of pixel types, and a hierarchy of them:

		//Examples of pixel types that represent the elementary computing types,
		//like byte, short, int, float... and their unsigned flavours:
		ByteType byteType = new ByteType();
		UnsignedIntType unsignedIntType = new UnsignedIntType();
		DoubleType doubleType = new DoubleType();
		//
		//Btw, the signed and unsigned variants of otherwise the same pixel type
		//are different only at compile time because they are simply of different types.
		//For example, ByteType and UnsignedByteType are always 8 bits of a memory
		//and CPU treats both the same. Not to mention that Java language does not
		//recognize, e.g., an "unsigned byte"...

		//Also specific (microscopy) pixel types of not bytes-multiple sizes
		//(btw, they are usually nicely packed in the memory to not waste space):
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

		//The hierarchy of backbone types is the following:
		//NumericType
		//  ComplexType
		//    RealType
		//      IntegerType, BooleanType
		//
		//where NumericType, RealType (and also often NativeType) belong
		//among the most frequently used "super types". They are nearly
		//exclusively used in generics:
		exampleMethodWithGenericTypes(byteType, unsignedIntType);
		exampleMethodWithGenericTypes(ArrayImgs.floats(10,10));

		//Notice the decoration of the above methods:
		// <T extends RealType<T>>
		//
		//There are two interesting moments:
		//
		//The first one was discussed just above: The wealth of operations
		//of what one can do on the type T is determined/comes from the
		//backbone type RealType, which indeed is rather a powerful one.

		//The second one is partly touched here:
		noteOnGenerics();
		//The "mighty" RealType also provides methods that are desired to return
		//the type T itself (which in this scenario is currently substituted
		//with the RealType). In order to achieve that, the information about
		//the current T needs to be passed inside into RealType, and so the
		//RealType needed to become RealType<T>.
	}

	public static void integerRealIntermezzo() {
		//IntegerType extends RealType: it offers everything which real numbers can
		//offer functionality-wise (!)
		//
		//This may seem to be the opposite to the mathematical, set theory
		//oriented, sense of their relationship. But "extends" here does not
		//mean that IntegerType is a super-set of RealType. It's actually the
		//inverse, "extends" here means "subset": Every IntegerType is a
		//RealType (and there are other RealTypes which are not IntegerType).
		//
		//IntegerType extends RealType means that functionality is added:
		//IntegerType can provide its value as an integer number, which a
		//general RealType cannot. (Again, this means extending with new
		//functionality, not increasing the domain of values that are possible
		//to hold.)
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
		//
		//The type inheritance hierarchy only makes sense in the "read"
		//direction. Expect "write" to be lossy.
		integerType.setReal(5.5);
		System.out.println("5.5 stored and retrieved from IntegerType is "+integerType.getInteger());
	}

	public static <TA extends RealType<TA>, TB extends RealType<TB>>
	void exampleMethodWithGenericTypes(final TA oneType, final TB secondType) {
		//Here, two possibly different types are taken together.
		//The method's signature basically only requires that both input types
		//could be treated as real-domain scalar values, allowing for, e.g.,
		//mathematical operations to be carried on them:
		double sum = oneType.getRealDouble() + secondType.getRealDouble();

		//But not only the math operations can be realized on the types,
		//this command grabs a new independent piece of a memory for the
		//same type (and holds initially the same value):
		TA anotherInstanceOfTheSameType = oneType.copy();

		//When further manipulating the type value, there are several shortcuts:
		anotherInstanceOfTheSameType.setOne();
		anotherInstanceOfTheSameType.setZero();

		//... including direct basic math short-operations on the same types:
		oneType.add( anotherInstanceOfTheSameType );
	}

	public static <T extends RealType<T>>
	void exampleMethodWithGenericTypes(final Img<T> img) {
		//In this example, the backbone RealType is used to offer the same
		//work over images that need not be always of the same pixel type.

		//For example, a sum of pixels values of this image can be then
		//implemented, for example, in this particular way:
		double sum = 0;
		for (T pixel : img) {
			sum += pixel.getRealDouble();
		}
		System.out.println("The sum over all pixels is: "+sum);
	}

	public static void noteOnGenerics() {
		//While Java generics allow to avoid repetitive blocks of code (one block for one particular pixel type),
		//the type represented in a generic, e.g. <T>, is visible/available only during the compile time.
		//At runtime, the T in <T> is replaced with Object (this is called type erasure), the basis of any Java
		//non-elementary type. In order to "preserve" the information about pixel type into runtime, one needs to
		//maintain/keep an existing object of that type. This is a strategy adopted, for example, in our type-safe loader:
		Img<UnsignedByteType> img1 = LearnathonHelpers.openImageResource("/blobs.tif", new UnsignedByteType());

		//Alternatively, one hides, encodes essentially, the type information into the method's definition itself:
		Img<FloatType> img2 = PlanarImgs.floats(6, 4, 3);
		Img<UnsignedShortType> img3 = PlanarImgs.unsignedShorts(6, 4, 3);

		//This is again the first strategy:
		Img<ByteType> img4 = img2.factory().imgFactory(new ByteType()).create(7, 8, 9);
		//Here, an image of the same backend was wanted, but pixel type and image size was provided different.
	}

	public static void main(String[] args) {
		onTypesHierarchy();
	}
}