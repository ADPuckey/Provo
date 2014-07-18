package com.jakeconley.provo.backend;

public class ProvoFormatException extends Exception
{
    private final String Message;
    private String Path = null;
    public String getFilePath(){ return Path; }
    public void setFilePath(String value){ Path = value; }
    public ProvoFormatException(String _message){ Message = _message; }
    @Override public String toString()
    {
        String ret = "Invalid YML format: " + Message;
        if(Path != null) ret += " in file " + Path;
        return ret;
    }
}