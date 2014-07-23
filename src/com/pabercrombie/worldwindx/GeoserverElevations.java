package com.pabercrombie.worldwindx;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.*;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;

import gov.nasa.worldwindx.examples.ApplicationTemplate;

import javax.swing.*;
import java.net.URI;

/**
 * Example of how to configure an elevation model for the GeoServer WMS.
 *
 * @author Parker Abercrombie
 */
public class GeoServerElevations extends ApplicationTemplate
{
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        public AppFrame()
        {
            // Connect to the elevation server on a thread other than the event-dispatch thread to avoid freezing the UI.
            Thread t = new Thread(new Runnable()
            {
                public void run()
                {
                    importElevations();
                }
            });
            t.start();
        }

        protected void importElevations()
        {
            try
            {
                // Parse the capabilities document from the WMS
                final String GET_CAPABILITIES_URL = "http://localhost:8080/geoserver/wms?request=getCapabilities";
                WMSCapabilities caps = WMSCapabilities.retrieve(new URI(GET_CAPABILITIES_URL));
                caps.parse();

                // Configure parameters for the Spearfish elevation model.
                AVList params = new AVListImpl();
                params.setValue(AVKey.LAYER_NAMES, "sf:sfdem");           // Layer name configured in GeoServer
                params.setValue(AVKey.IMAGE_FORMAT, "application/bil32"); // Data format to request
                params.setValue(AVKey.BYTE_ORDER, AVKey.BIG_ENDIAN);      // Byte order of BIL files returned by server
                params.setValue(AVKey.MISSING_DATA_SIGNAL, -9.999999933815813E36); // Missing data flag

                Factory factory = (Factory) WorldWind.createConfigurationComponent(AVKey.ELEVATION_MODEL_FACTORY);
                final ElevationModel spearfish = (ElevationModel) factory.createFromConfigSource(caps, params);

                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        // Get the WorldWindow's current elevation model.
                        Globe globe = AppFrame.this.getWwd().getModel().getGlobe();

                        // Replace elevation model with imported elevations. This makes the elevation 0 everywhere
                        // except in the region imported, so it is easy to tell that the elevations are being pulled
                        // from GeoServer. For production use create a CompoundElevationModel and add the new elevations
                        // to the compound model.
                        globe.setElevationModel(spearfish);

                        // Example of how to add the elevation model to a CompoundElevationModel
//                        if (currentElevationModel instanceof CompoundElevationModel)
//                            ((CompoundElevationModel) currentElevationModel).addElevationModel(elevationModel);
//                        else
//                            globe.setElevationModel(elevationModel);


                        // Set the view to look at the imported elevations.
                        Position spearfishSouthDakota = Position.fromDegrees(44.4709, -103.6812, 10000);
                        getWwd().getView().setEyePosition(spearfishSouthDakota);
                    }
                });
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
    {
        ApplicationTemplate.start("GeoServer Elevation Example", GeoServerElevations.AppFrame.class);
    }
}
