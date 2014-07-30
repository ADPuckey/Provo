package com.jakeconley.provo.functions.planning;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;

public class MeasuringState
{
    private final List<Location> ClickedLocations = new LinkedList<>();
    private final World World;
    public World getWorld(){ return World; }
    public List<Location> getClickedLocations(){ return ClickedLocations; }
    public void addClickedLocation(Location value){ ClickedLocations.add(value); }
    
    public MeasuringState(World _World)
    {
        World = _World;
    }
}
