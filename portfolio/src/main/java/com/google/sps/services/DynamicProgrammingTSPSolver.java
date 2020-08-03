package com.google.sps.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;

public class DynamicProgrammingTSPSolver implements TSPSolver {

    public static int MAX_POINTS = 10;

    public DynamicProgrammingTSPSolver() {
        distanceMatrixProvider = new DistanceMatrixProviderImpl();
    }

    public DynamicProgrammingTSPSolver(DistanceMatrixProvider distanceMatrixProvider) {
        this.distanceMatrixProvider = distanceMatrixProvider;
    }

    @Override
    public List<Integer> solveTSP(LatLng[] points) throws IllegalArgumentException {

        if (points.length > MAX_POINTS) {
            throw new IllegalArgumentException("Too many points provided");
        }

        if (points.length == 0) {
            return new ArrayList<Integer>();
        }

        DistanceMatrix matrix = distanceMatrixProvider.getDistanceMatrix(points);

        // Solve by Dynamic Programming

        // dp[p][mask] - shortest path length through a subset of points represented by mask ending in point p.
        long[][] dp = new long[points.length][1 << points.length];

        // Initialize all values with infinity
        Arrays.stream(dp).forEach(row -> Arrays.fill(row, infinity));

        // Shortest path length through a set of one point is zero
        for (int startPoint = 0; startPoint < points.length; ++startPoint)
            dp[startPoint][1 << startPoint] = 0;

        // Iterate through all possible non-empty subsets
        for (int mask = 1; mask < (1 << points.length); ++mask) {
            // Iterate through all possible end points
            for (int endPoint = 0; endPoint < points.length; ++endPoint) {
                // If end point doesn't belong to current subset, skip it
                if (((mask >> endPoint) & 1) != 1) continue;

                // Try to add a new point to the subset
                for (int newEnd = 0; newEnd < points.length; ++newEnd) {
                    // If a point already belongs to the subset, skip it
                    if (((mask >> newEnd) & 1) == 1) continue;

                    // Else try to improve the dp value for subset united with the new point

                    long distanceToNewEnd = matrix.rows[endPoint].elements[newEnd].distance.inMeters;

                    dp[newEnd][mask | (1 << newEnd)] = Math.min(
                        dp[newEnd][mask | (1 << newEnd)],
                        dp[endPoint][mask] + distanceToNewEnd
                    );
                }
            }
        }

        List<Integer> path = new ArrayList<Integer>();

        int currentEnd = 0;
        int currentMask = (1 << points.length) - 1;

        // Find the best possible end of the path
        for (int point = 0; point < points.length; ++point)
            if (dp[point][currentMask] < dp[currentEnd][currentMask])
                currentEnd = point;

        // Recover the path from dp values
        while (true) {
            // Add current end to path
            path.add(0, currentEnd);

            // Stop if reached subset of one point
            if (currentMask == (1 << currentEnd))
                break;

            // Find the point before current end
            for (int prevEnd = 0; prevEnd < points.length; ++prevEnd) {
                // Test that current dp state was relaxed from a state with end equal prevEnd
                if (
                    dp[currentEnd][currentMask] == dp[prevEnd][currentMask ^ (1 << currentEnd)] +
                    matrix.rows[prevEnd].elements[currentEnd].distance.inMeters
                ) {
                    // Found previous end, go to next step
                    currentMask ^= 1 << currentEnd;
                    currentEnd = prevEnd;
                    break;
                }
            }
        }

        return path;
    }

    private static final long infinity = (long) 1e18;

    private final DistanceMatrixProvider distanceMatrixProvider;
}
