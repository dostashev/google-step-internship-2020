package com.google.sps.services;

import java.io.IOException;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;
import com.google.sps.Config;

public class DistanceMatrixProviderImpl implements DistanceMatrixProvider {

    @Override
    public DistanceMatrix getDistanceMatrix(LatLng[] points) {
        try {
            return DistanceMatrixApi.newRequest(context)
                .origins(points)
                .destinations(points)
                .await();
        } catch (ApiException | InterruptedException | IOException e) {
            return null;
        }
    }

    private static final GeoApiContext context = new GeoApiContext.Builder().apiKey(Config.GMAPS_API_KEY).build();
}
