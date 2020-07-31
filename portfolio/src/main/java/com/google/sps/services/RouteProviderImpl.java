package com.google.sps.services;

import java.io.IOException;
import java.util.Arrays;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;
import com.google.sps.Config;

public class RouteProviderImpl implements RouteProvider {

    @Override
    public EncodedPolyline getRouteThroughPoints(LatLng[] points) {

        if (points.length == 0) {
            return new EncodedPolyline();
        }

        DirectionsResult result;
        try {
            result = DirectionsApi.newRequest(context)
                .origin(points[0])
                .waypoints(Arrays.copyOfRange(points, 1, points.length - 1))
                .destination(points[points.length - 1])
                .await();
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
            return null;
        }

        return result.routes[0].overviewPolyline;
    }

    private static final GeoApiContext context = new GeoApiContext.Builder().apiKey(Config.GMAPS_API_KEY).build();
}
