package com.jakeconley.provo.utils;

import org.bukkit.Location;

public enum CoordinateAxis
{
    LONGITUDE('x', "Longitudinal"), LATITUDE('z', "Latitudinal"), ELEVATION('y', "Altitudinal");
    
    private final char Name;
    private final String Adjective;
    public char getName(){ return Name; }
    public String getAdjective(){ return Adjective; }

    CoordinateAxis(char _Name, String _Adjective){ Name = _Name; Adjective = _Adjective; }
    
    public static CoordinateAxis FromName(char name)
    {
        switch(name)
        {
            case 'x': return LONGITUDE;
            case 'z': return LATITUDE;
            case 'y': return ELEVATION;
            default: return null;
        }
    }
    
    public String toUserFriendlyString(){ return Adjective + "(" + Name + ")"; }
    
    public double GetValueFromLocation(Location l)
    {
        switch(this)
        {
            case LONGITUDE: return l.getX();
            case LATITUDE: return l.getZ();
            case ELEVATION: return l.getY();
            default: throw new NullPointerException();
        }
    }
}
