# Training material for ImgLib2 and BigDataViewer

This material was prepared as the learnathon part of the
[HackBrno23 -- an ImgLib2 and BDV hackathon](https://www.ceitec.eu/imglib2-and-bigdataviewer-hackathon-brno/a4534).

## Modules

### [ImgLib2 basics](src/main/java/net/imglib2/brno_learnathon/s1_study_imglib2_basics)

Exercises for this module can be found [here](src/main/java/net/imglib2/brno_learnathon/s2_try_yourself_imglib2).

* ImgLib2 doesn't view images as an array of 'pixels', but containers with addressable contents (either randomly or in order).
* Images can have different types of backends, depending on the data source and size.
* There is a plethora of pixel types that can be used to specify the type of the image content. An example would be `NumericType`, which can do arithmetic operations, or `RealType`, which encapsulates 'normal' scalar numbers.
* Sometimes, pixel types are recursive (e.g., `<T extends RealType<T>>`). This allows to retain the exact type information of the pixel type throughout the whole program, which is useful for some operations.
* Some classes have accompanying utility classes, denoted by an additional 's', that bundle static methods such as creation of images, conversion between types, etc. One example would be `ArrayImg` with the `ArrayImgs` utility class.


### [ImgLib2 views and converters](src/main/java/net/imglib2/brno_learnathon/s3_study_imglib2)

Exercises for this module can be found [here](src/main/java/net/imglib2/brno_learnathon/s4_try_yourself_imglib2).

* Views are a way to create a different interpretation of the data of some image. In particular, no data is copied.
* To ensure that operations on views have the correct iteration order and are as efficient as possible, it is best to use `LoopBuilder`.
* Converters are a way of filtering the data of an image by a custom supplied function on a per-pixel basis. They are evaluated on the fly.


### [BigDataViewer basics](src/main/java/net/imglib2/brno_learnathon/s5_study_bigdataviewer_basics)

Exercises for this module can be found [here](src/main/java/net/imglib2/brno_learnathon/s6_try_yourself_bigdataviewer).

* Most BigDataViewer functionality is accessible programmatically through methods on `Bdv` objects.
* One can add continuous objects (e.g., `RealRandomAccessible`) to discrete objects (e.g., `RandomAccessibleInterval`) to BigDataViewer, where the visualization is based on the current resolution level.
