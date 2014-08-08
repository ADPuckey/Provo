package com.jakeconley.provo.features.measuring;

import com.jakeconley.provo.utils.CoordinateAxis;
import java.util.Map;

public class MeasuringResult
{
    private final Map<CoordinateAxis, Double> AxisLengths;
    private final Map<CoordinateAxis[], Double> PlanarAreas;
    private final double Diagonal;// Distance between clicked blocks
    private final double Composition;// Area, volume
    private final int EdgeSum;// Perimeter or 3d edge length
    private final Map<CoordinateAxis, Double> Midpoint;// {x, y, z}

    public Map<CoordinateAxis, Double> getAxisLengths() { return AxisLengths; }
    public Map<CoordinateAxis[], Double> getPlanarAreas() { return PlanarAreas; }
    public double getDiagonal() { return Diagonal; }
    public double getComposition() { return Composition; }
    public int getEdgeLength() { return EdgeSum; }
    public Map<CoordinateAxis, Double> getMidpoint(){ return Midpoint; }

    public MeasuringResult(Map<CoordinateAxis, Double> AxisLengths, Map<CoordinateAxis[], Double> PlanarAreas, double Diagonal, double Composition, int EdgeLength, Map<CoordinateAxis, Double> Midpoint)
    {
        this.AxisLengths = AxisLengths;
        this.PlanarAreas = PlanarAreas;
        this.Diagonal = Diagonal;
        this.Composition = Composition;
        this.EdgeSum = EdgeLength;
        this.Midpoint = Midpoint;
    }
}
