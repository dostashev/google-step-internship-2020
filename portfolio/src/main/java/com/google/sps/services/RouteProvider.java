package com.google.sps.services;

import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;

public interface RouteProvider {

    EncodedPolyline getRouteThroughPoints(LatLng[] points);
}
