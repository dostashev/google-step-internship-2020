package com.google.sps.services;

import java.util.List;

import com.google.maps.model.LatLng;

public interface TSPSolver {

    /**
     * @return int[] representing the order in which the points should be visited
     */
    List<Integer> solveTSP(LatLng[] points) throws IllegalArgumentException;
}
