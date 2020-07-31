package com.google.sps.servlets;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;
import com.google.sps.services.DynamicProgrammingTSPSolver;
import com.google.sps.services.RouteProvider;
import com.google.sps.services.RouteProviderImpl;
import com.google.sps.services.TSPSolver;

@WebServlet("/solve-tsp")
public class TravellingSalesmanServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        LatLng[] points = gson.fromJson(request.getReader(), LatLng[].class);

        List<Integer> order = solver.solveTSP(points);

        LatLng[] orderedPoints = order.stream()
            .map(index -> points[index])
            .toArray(LatLng[]::new);

        EncodedPolyline polyline = routeProvider.getRouteThroughPoints(orderedPoints);

        response.getWriter().print(polyline.getEncodedPath());
    }

    private final Gson gson = new Gson();
    private final TSPSolver solver = new DynamicProgrammingTSPSolver();
    private final RouteProvider routeProvider = new RouteProviderImpl();
}
