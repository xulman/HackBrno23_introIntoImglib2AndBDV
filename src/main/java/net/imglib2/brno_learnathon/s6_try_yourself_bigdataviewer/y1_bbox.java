package net.imglib2.brno_learnathon.s6_try_yourself_bigdataviewer;

import bdv.ui.UIUtils;
import bdv.util.*;
import bdv.viewer.ViewerPanel;

import java.awt.*;
import java.io.IOException;

import net.imglib2.RealPoint;
import net.imglib2.RealPositionable;
import net.imglib2.brno_learnathon.scaffold.LearnathonHelpers;
import net.imglib2.img.Img;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import org.scijava.ui.behaviour.ClickBehaviour;
import org.scijava.ui.behaviour.DragBehaviour;
import org.scijava.ui.behaviour.io.InputTriggerConfig;
import org.scijava.ui.behaviour.util.Behaviours;

public class y1_bbox
{
	public static void main( final String[] args ) throws IOException
	{
		final Img< UnsignedByteType > img = LearnathonHelpers.openImageResource( "/t1-head.tif", new UnsignedByteType() );
		final Bdv bdv = BdvFunctions.show( img, "img" );

		class BdvLineOverlay extends BdvOverlay {
			public void setStartOfLine(int x, int y) {
				sx = x;
				sy = y;
				canBeDisplayed = false;
			}

			public void setEndOfLine(int x, int y) {
				ex = x;
				ey = y;
				canBeDisplayed = true;
			}

			public void stopDrawingLine() {
				canBeDisplayed = false;
			}

			private int sx,sy; //starting coordinate of the line, the "first end"
			private int ex,ey; //ending coordinate of the line, the "second end"
			private boolean canBeDisplayed = false;

			private final BasicStroke stroke = new BasicStroke( 1.0f ); //lightweight I guess

			@Override
			protected void draw(Graphics2D g) {
				if (canBeDisplayed) {
					//final double uiScale = UIUtils.getUIScaleFactor( this );
					//final BasicStroke stroke = new BasicStroke( ( float ) uiScale );
					g.setStroke( stroke );
					g.setPaint( Color.GREEN );
					g.drawLine(sx,sy, ex,ey);
				}
			}
		}

		final BdvLineOverlay myLineOverlay = new BdvLineOverlay();
		final BdvOverlaySource<BdvOverlay> bdvOverlay = BdvFunctions.showOverlay(myLineOverlay, "SAMJ line", BdvOptions.options().addTo(bdv));

		//stops drawing the line as soon as viewport has changed
		//or, TODO, adjust the coordinates based on the new transform (but that's not what we want for SAMJ)
		bdv.getBdvHandle().getViewerPanel().renderTransformListeners().add(transform -> myLineOverlay.stopDrawingLine());

		/*
		 * Install behaviour for moving a line into img with shortcut "L"
		 */
		final Behaviours behaviours = new Behaviours( new InputTriggerConfig() );
		behaviours.install( bdv.getBdvHandle().getTriggerbindings(), "samj" );
		behaviours.behaviour( new DragBehaviour()
		{
			final ViewerPanel viewer = bdv.getBdvHandle().getViewerPanel();

/*
			void draw( final int x, final int y )
			{
				viewer.displayToGlobalCoordinates( x, y, roundpos );
				sphere.get().forEach( t -> t.set( 255 ) );
				viewer.requestRepaint();
			}
*/

			@Override
			public void init( final int x, final int y )
			{
				myLineOverlay.setStartOfLine(x,y);
			}

			@Override
			public void drag( final int x, final int y )
			{
				myLineOverlay.setEndOfLine(x,y);
			}

			@Override
			public void end( final int x, final int y )
			{
				myLineOverlay.setEndOfLine(x,y);
				System.out.println("TRIGGER AFTER A LINE WAS INSERTED");
			}
		}, "samj_line", "L" );

		behaviours.behaviour(new ClickBehaviour() {
			@Override
			public void click(int x, int y) {
				System.out.println("Going to add a new SAMJ view");
				final ViewerPanel viewerPanel = bdv.getBdvHandle().getViewerPanel();

				AXIS_VIEW viewDir = whatDimensionIsViewAlong( viewerPanel.state().getViewerTransform() );
				System.out.println("View: "+viewDir);

				if (viewDir != AXIS_VIEW.NONE_OF_XYZ) {
					final RealPositionable topLeftROI = new RealPoint(3);
					final RealPositionable bottomRightROI = new RealPoint(3);
					final Dimension displaySize = viewerPanel.getDisplayComponent().getSize();
					viewerPanel.displayToGlobalCoordinates(0,0, topLeftROI);
					viewerPanel.displayToGlobalCoordinates(displaySize.width,displaySize.height, bottomRightROI);
					System.out.println("image ROI: "+topLeftROI+" -> "+bottomRightROI);
				}
			}
		}, "samj_new_view", "A");
	}

	public enum AXIS_VIEW {
		ALONG_X,
		ALONG_Y,
		ALONG_Z,
		NONE_OF_XYZ
	}

	/**
	 * @param view
	 * @return -1 if the view is not along any axis, or 0,1,2 for x-,y-,z-axis
	 */
	public static AXIS_VIEW whatDimensionIsViewAlong(final AffineTransform3D view) {
		/* along x: 0,0,?    along y: ?,0,0    along z: ?,0,0
		 *          0,?,0             0,0,?             0,?,0
		 *          ?,0,0             0,?,0             0,0,?
		 */
		if ( isZeroElem(view, 0,0) ) {
			//candidate for "along x"
			if (isZeroElem(view, 0,1)
					&& isZeroElem(view, 1,0) && isZeroElem(view, 1,2)
					&& isZeroElem(view, 2,1) && isZeroElem(view, 2,2))
					return AXIS_VIEW.ALONG_X;
		} else if (isZeroElem(view, 0,1) && isZeroElem(view, 0,2)) {
			//candidate for "along y or z"
			if (isZeroElem(view, 1,1)) {
				//for "along y"
				if (isZeroElem(view, 1,0)
						&& isZeroElem(view, 2,0) && isZeroElem(view, 2,2))
						return AXIS_VIEW.ALONG_Y;
			} else {
				//for "along z"
				if (isZeroElem(view, 1,0) && isZeroElem(view, 1,2)
						&& isZeroElem(view, 2,0) && isZeroElem(view, 2,1))
						return AXIS_VIEW.ALONG_Z;
			}
		}
		return AXIS_VIEW.NONE_OF_XYZ;
	}

	public static boolean isZero(double val) {
		final double EPSILON = 0.00001;
		return -EPSILON < val && val < EPSILON;
	}
	public static boolean isZeroElem(final AffineTransform3D view, int row, int column) {
		return isZero( view.get(row,column) );
	}
}
