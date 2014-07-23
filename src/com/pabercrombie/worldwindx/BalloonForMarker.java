package com.pabercrombie.worldwindx;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.MarkerLayer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.render.markers.*;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwindx.examples.util.BalloonController;
import gov.nasa.worldwindx.examples.util.HotSpotController;

import java.util.*;

/**
 * Example of how to display a balloon when a {@link Marker} is clicked.
 *
 * @author Parker Abercrombie
 */
public class BalloonForMarker extends ApplicationTemplate
{
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        protected HotSpotController hotSpotController;
        protected BalloonController balloonController;

        /**
         * Custom Marker class that captures the ID of the marker.
         */
        protected class MyMarker extends BasicMarker
        {
            protected int id;

            public MyMarker(Position position, MarkerAttributes attrs, int id)
            {
                super(position, attrs);
                this.id = id;
            }

            public int getId()
            {
                return this.id;
            }
        }

        /**
         * Controller to create a balloon when a markers is selected.
         */
        protected class MarkerBalloonController extends BalloonController
        {
            /** Layer to render the balloons. */
            protected RenderableLayer balloonLayer;

            public MarkerBalloonController(WorldWindow wwd)
            {
                super(wwd);
                this.balloonLayer = new RenderableLayer();
                wwd.getModel().getLayers().add(this.balloonLayer);
            }

            /**
             * Create a ballon for the marker that was selected.
             *
             * @param pickedObject PickedObject to inspect. May not be null.
             *
             * @return a balloon
             */
            @Override
            protected Balloon getBalloon(PickedObject pickedObject)
            {
                Object topObject = pickedObject.getObject();
                if (topObject instanceof MyMarker)
                {
                    Balloon balloon = this.createBalloon((MyMarker) topObject, pickedObject.getPosition());
                    this.balloonLayer.addRenderable(balloon);

                    return balloon;
                }

                return super.getBalloon(pickedObject);
            }

            /**
             * Create a balloon for a marker.
             */
            protected Balloon createBalloon(MyMarker marker, Position position)
            {
                String balloonText = "Marker " + marker.getId();
                GlobeBalloon balloon = new GlobeBrowserBalloon(balloonText, position);
                balloon.setAttributes(new BasicBalloonAttributes());
                balloon.setVisible(false);
                balloon.setAlwaysOnTop(true);
                return balloon;
            }

            /**
             * Remove the previously created balloon.
             */
            @Override
            protected void hideBalloon()
            {
                if (this.balloon != null)
                {
                    this.balloonLayer.removeRenderable(this.balloon);
                }
            }
        }

        public AppFrame()
        {
            super(true, true, false);

            // Add a controller to send input events to BrowserBalloons.
            this.hotSpotController = new HotSpotController(this.getWwd());
            // Add a controller to handle link and navigation events in BrowserBalloons.
            this.balloonController = new MarkerBalloonController(this.getWwd());

            MarkerAttributes attrs = new BasicMarkerAttributes(Material.WHITE, BasicMarkerShape.SPHERE, 1d, 10, 5);

            double lon = -110.0;
            double minLat = 20, maxLat = 60, latDelta = 2;

            RenderableLayer balloonLayer = new RenderableLayer();
            insertBeforePlacenames(this.getWwd(), balloonLayer);

            int i = 0;
            java.util.List<Marker> markers = new ArrayList<Marker>();
            for (double lat = minLat; lat <= maxLat; lat += latDelta)
            {
                Marker marker = new MyMarker(Position.fromDegrees(lat, lon, 0), attrs, i);
                markers.add(marker);
                i++;
            }

            MarkerLayer layer = new MarkerLayer();
            layer.setKeepSeparated(false);
            layer.setElevation(1000d);
            layer.setMarkers(markers);
            insertBeforePlacenames(this.getWwd(), layer);
        }
    }

    public static void main(String[] args)
    {
        ApplicationTemplate.start("Markers with Balloons", BalloonForMarker.AppFrame.class);
    }
}
