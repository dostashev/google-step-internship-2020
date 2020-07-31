package com.google.sps.services;

import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;

public interface DistanceMatrixProvider {

    DistanceMatrix getDistanceMatrix(LatLng[] points);
}
