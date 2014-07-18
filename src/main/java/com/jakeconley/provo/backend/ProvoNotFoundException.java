package com.jakeconley.provo.backend;

public class ProvoNotFoundException extends Exception
{
    String Message;
    String FilePath = null;
    
    public ProvoNotFoundException(String message){ this.Message = message; }
    
    @Override public String toString()
    {
        String ret = Message;
        if(FilePath != null) ret += " (in file " + FilePath + ")";
        return ret;
    }
}
