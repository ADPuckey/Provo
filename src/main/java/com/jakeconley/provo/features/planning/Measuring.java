package com.jakeconley.provo.features.planning;

import com.jakeconley.provo.utils.CoordinateAxis;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Measuring
{

    public static double Avg(double first, double second) throws MeasuringOverflowException
    {
        if(second > Double.MAX_VALUE - first) throw new MeasuringOverflowException();
        return ((second + first) / 2);
    }
    public static MeasuringResult Calculate(MeasuringState state, Player p) throws MeasuringOverflowException
    {
        // Flexibly coding it this way instead of hard coding for forwards compatibility.  I'm aware it looks goofy and inefficient but it's not lol
        if(state.getClickedLocations().size() != 2) throw new IllegalArgumentException("State doesn't have two corners!");
        
        // Assume only two blocks below
        Map<CoordinateAxis, Double> axis_lengths = new HashMap<>();
        Map<CoordinateAxis, Double> midpoint = new HashMap<>();
        for(CoordinateAxis axis : CoordinateAxis.values())
        {
            double first = axis.GetValueFromLocation(state.getClickedLocations().get(0));
            double second = axis.GetValueFromLocation(state.getClickedLocations().get(1));
            //if(first < Double.MIN_VALUE + second) throw new MeasuringOverflowException();
            double abs_dif = Math.abs(second - first);
            if(abs_dif != 0)
            {
                // CORRECTIVE ADDITION for block coordinates, to account for the southwest-northeast rule and the lost block
                axis_lengths.put(axis, abs_dif + 1);
                midpoint.put(axis, Avg(second, first) + .5);
            }
        }
        
        Map<CoordinateAxis[], Double> planar_areas = new HashMap<>();
        for(Map.Entry<CoordinateAxis, Double> first : axis_lengths.entrySet())
        {
            for(Map.Entry<CoordinateAxis, Double> second : axis_lengths.entrySet())
            {
                CoordinateAxis[] pair = { first.getKey(), second.getKey() };
                if(planar_areas.containsKey(pair)) continue;
                
                if(first.getValue() != 0 && second.getValue() > Double.MAX_VALUE / first.getValue()) throw new MeasuringOverflowException();                
                planar_areas.put(pair, first.getValue() * second.getValue());
            }
        }
        
        double squaresum = 0;
        double MAX_SQRT = Math.sqrt(Double.MAX_VALUE);
        for(Map.Entry<CoordinateAxis, Double> entry : axis_lengths.entrySet())
        {
            if(entry.getValue() > MAX_SQRT) throw new MeasuringOverflowException();
            double square = Math.pow(entry.getValue(), 2);
            if(square > Double.MAX_VALUE - squaresum) throw new MeasuringOverflowException();
            squaresum += square;
        }
        double diagonal = Math.sqrt(squaresum);
        
        double composition = 0;
        for(Map.Entry<CoordinateAxis, Double> entry : axis_lengths.entrySet())
        {
            if(composition != 0 && entry.getValue() > Double.MAX_VALUE / composition) throw new MeasuringOverflowException();
            if(composition == 0) composition = entry.getValue();
            else composition *= entry.getValue();
        }
        
        int edgesum = 0;
        int axis_multiplier = 2 * (axis_lengths.size() - 1);// lol
        if(axis_multiplier != 0)
        {
            for(Map.Entry<CoordinateAxis, Double> entry : axis_lengths.entrySet())
            {
                if(entry.getValue() > Double.MAX_VALUE / axis_multiplier) throw new MeasuringOverflowException();
                double addition = entry.getValue() * axis_multiplier;
                if(addition > Double.MAX_VALUE - edgesum) throw new MeasuringOverflowException();
                edgesum += addition;
            }
        }
        
        return new MeasuringResult(axis_lengths, planar_areas, diagonal, composition, edgesum, midpoint);
    }
    
    public static void FrontendExecute(MeasuringResult m, Player p)
    {
        p.sendMessage(ChatColor.GREEN + "Measurements:");
        p.sendMessage(ChatColor.AQUA + (m.getAxisLengths().size() == 1 ? "Distance: " : "Diagonal: ") + ChatColor.RESET + m.getDiagonal());
        
        StringBuilder sb_midpoint = new StringBuilder();
        sb_midpoint.append(ChatColor.AQUA).append("Midpoint: ");
        for(Map.Entry<CoordinateAxis, Double> entry : m.getMidpoint().entrySet()) sb_midpoint.append(ChatColor.YELLOW).append(entry.getKey().getName()).append(":").append(ChatColor.RESET).append(entry.getValue()).append(" ");
        p.sendMessage(sb_midpoint.toString());
        
        if(m.getAxisLengths().size() > 1)
        {
            
            StringBuilder sb_axes = new StringBuilder();
            sb_axes.append(ChatColor.AQUA).append("Axes: ");
            for(Map.Entry<CoordinateAxis, Double> entry : m.getAxisLengths().entrySet()) sb_axes.append(ChatColor.YELLOW).append(entry.getKey().toUserFriendlyString()).append(":").append(ChatColor.RESET).append(entry.getValue()).append(" ");
            p.sendMessage(sb_axes.toString());
            
            StringBuilder sb_contents = new StringBuilder();
            sb_contents.append(ChatColor.AQUA).append(m.getAxisLengths().size() == 2 ? "Planar" : "Spatial").append(" properties: ").append(ChatColor.RESET);
            // Terminology adjustments
            switch(m.getAxisLengths().size())
            {
                case 2: sb_contents.append(ChatColor.YELLOW).append("area:").append(ChatColor.RESET).append(Double.toString(m.getComposition())).append(ChatColor.YELLOW).append(" outer perimiter:").append(ChatColor.RESET).append(m.getEdgeLength()); break;
                case 3: sb_contents.append(ChatColor.YELLOW).append("volume:").append(ChatColor.RESET).append(Double.toString(m.getComposition())).append(ChatColor.YELLOW).append(" outer edge-length:").append(ChatColor.RESET).append(m.getEdgeLength()); break;
            }
            p.sendMessage(sb_contents.toString());
        }
    }
}
