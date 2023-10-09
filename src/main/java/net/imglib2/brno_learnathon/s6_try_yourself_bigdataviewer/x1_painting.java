package net.imglib2.brno_learnathon.s6_try_yourself_bigdataviewer;

import bdv.util.Bdv;
import bdv.util.BdvFunctions;
import bdv.viewer.ViewerPanel;
import java.io.IOException;
import net.imglib2.RandomAccess;
import net.imglib2.RealPositionable;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.brno_learnathon.scaffold.LearnathonHelpers;
import net.imglib2.img.Img;
import net.imglib2.position.transform.Round;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;
import org.scijava.ui.behaviour.DragBehaviour;
import org.scijava.ui.behaviour.io.InputTriggerConfig;
import org.scijava.ui.behaviour.util.Behaviours;

public class x1_painting
{
	public static void main( final String[] args ) throws IOException
	{
		final Img< UnsignedByteType > img = LearnathonHelpers.openImageResource( "/t1-head.tif", new UnsignedByteType() );
		final Bdv bdv = BdvFunctions.show( img, "img" );

		/*
		 * Install behaviour for painting into img with shortcut "D"
		 */

		final Behaviours behaviours = new Behaviours( new InputTriggerConfig() );
		behaviours.install( bdv.getBdvHandle().getTriggerbindings(), "paint" );
		behaviours.behaviour( new DragBehaviour()
		{
			final ViewerPanel viewer = bdv.getBdvHandle().getViewerPanel();
			final RandomAccess< Neighborhood< UnsignedByteType > > sphere = new HyperSphereShape( 3 ).neighborhoodsRandomAccessible( Views.extendZero( img ) ).randomAccess();
			final RealPositionable roundpos = new Round<>( sphere );

			void draw( final int x, final int y )
			{
				viewer.displayToGlobalCoordinates( x, y, roundpos );
				sphere.get().forEach( t -> t.set( 255 ) );
				viewer.requestRepaint();
			}

			@Override
			public void init( final int x, final int y )
			{
				draw( x, y );
			}

			@Override
			public void end( final int x, final int y )
			{}

			@Override
			public void drag( final int x, final int y )
			{
				draw( x, y );
			}
		}, "paint", "D" );
	}
}
