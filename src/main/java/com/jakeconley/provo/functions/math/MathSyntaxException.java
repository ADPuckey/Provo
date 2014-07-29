package com.jakeconley.provo.functions.math;

public class MathSyntaxException extends Exception
{
    public static enum Types{ UNEXPECTED_SYMBOL, EXPECTED_NUMBER, EXPECTED_OPERATOR, OPERATOR_OUT_OF_PLACE, UNSPECIFIED }
    private final Types Type;
    private final String Offender;
    public Types getType(){ return Type; }
    public String getOffender(){ return Offender; }
    
    public MathSyntaxException(Types _Type, String _Offender)
    {
        this.Type = _Type;
        this.Offender = _Offender;
    }
    
    @Override
    public String toString(){ return Type.toString() + " " + Offender; }
}
