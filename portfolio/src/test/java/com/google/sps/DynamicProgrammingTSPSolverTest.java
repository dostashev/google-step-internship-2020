package com.google.sps;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Random;

import com.google.maps.model.Distance;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.LatLng;
import com.google.sps.services.DistanceMatrixProvider;
import com.google.sps.services.DynamicProgrammingTSPSolver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class DynamicProgrammingTSPSolverTest {

    private MockDistanceMatrixProvider distanceMatrixProvider;
    private DynamicProgrammingTSPSolver solver;

    @Before
    public void setUp() {
        distanceMatrixProvider = new MockDistanceMatrixProvider();
        solver = new DynamicProgrammingTSPSolver(distanceMatrixProvider);
    }

    @Test
    public void emptyPointsList() {
        List<Integer> result = solver.solveTSP(new LatLng[0]);

        assertEquals(0, result.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooManyPoints() {
        solver.solveTSP(new LatLng[DynamicProgrammingTSPSolver.MAX_POINTS + 1]);
    }

    @Test
    public void maxAmountOfPoints() {
        solver.solveTSP(new LatLng[DynamicProgrammingTSPSolver.MAX_POINTS]);
    }

    @Test
    public void testGraphWithSmallPath() {
        distanceMatrixProvider.mode = MockDistanceMatrixProvider.Mode.preset;

        List<Integer> result = solver.solveTSP(new LatLng[4]);

        Integer[] expected = {0, 1, 2, 3};

        assertArrayEquals(expected, result.toArray());
    }
}

class MockDistanceMatrixProvider implements DistanceMatrixProvider {

    public enum Mode { preset, random }

    public Mode mode = Mode.random;

    @Override
    public DistanceMatrix getDistanceMatrix(LatLng[] points) {

        switch (mode) {
            case preset:
                return getPresetMatrix();
            case random:
                return getRandomMatrix(points.length);
        }

        return null;
    }

    private DistanceMatrix getPresetMatrix() {
        long[][] distances = {
            {0, 1, 100, 100},
            {100, 0, 1, 100},
            {100, 100, 0, 1},
            {100, 100, 100, 0},
        };

        return convertToDistanceMatrix(distances);
    }

    private DistanceMatrix getRandomMatrix(int numberOfPoints) {

        Random rng = new Random(1337);

        int MAX_DISTANCE = 1000000;

        long[][] distances = new long[numberOfPoints][numberOfPoints];

        for (int i = 0; i < numberOfPoints; ++i) {

            for (int j = 0; j < numberOfPoints; ++j)
                distances[i][j] = rng.nextInt(MAX_DISTANCE);

            distances[i][i] = 0;
        }

        return convertToDistanceMatrix(distances);
    }

    DistanceMatrix convertToDistanceMatrix(long[][] distances) {

        int numberOfPoints = distances.length;

        String[] points = new String[numberOfPoints];

        for (int i = 0; i < numberOfPoints; ++i)
            points[i] = new Integer(i).toString();

        DistanceMatrix matrix = new DistanceMatrix(
            points,
            points,
            new DistanceMatrixRow[numberOfPoints]
        );

        for (int i = 0; i < numberOfPoints; ++i) {
            matrix.rows[i] = new DistanceMatrixRow();
            matrix.rows[i].elements = new DistanceMatrixElement[numberOfPoints];

            for (int j = 0; j < numberOfPoints; ++j) {
                matrix.rows[i].elements[j] = new DistanceMatrixElement();
                matrix.rows[i].elements[j].distance = new Distance();
                matrix.rows[i].elements[j].distance.inMeters = distances[i][j];
            }
        }

        return matrix;
    }
}
