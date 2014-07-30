package com.jakeconley.provo.backend;

public class ProvoFormatException extends Exception
{
    public static enum Origin{ PUBLIC, PRIVATE }
    public static enum Type{ UNSPECIFIED, MUTUAL_INHERITANCE, BACKEND_FORMAT }
    
    private final String Message;
    private String Path = null;
    private Type Type;
    private Origin Origin;
    private boolean Fixed;
    public String getFilePath(){ return Path; }
    public void setFilePath(String value){ Path = value; }
    public Type getType(){ return Type; }
    public void setType(Type _Type){ this.Type = _Type; }
    public Origin getOrigin(){ return Origin; }
    public void setOrigin(Origin _Origin){ this.Origin = _Origin; }
    public boolean isFixed(){ return Fixed; }
    public void setFixed(boolean _fixed){ Fixed = _fixed; }
    
    
    public ProvoFormatException(String _message)
    {
        Message = _message;
        Type = Type.UNSPECIFIED;
        Origin = Origin.PRIVATE;
        Fixed = false;
    }
    
    @Override public String toString()
    {
        String ret;
        if(Message != null) ret = "Invalid YML format: " + Message;
        else ret = "Bad formatting, error type " + Type.toString();
        if(Path != null) ret += " in file " + Path;
        return ret;
    }
}