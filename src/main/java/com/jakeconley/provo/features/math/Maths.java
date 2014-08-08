package com.jakeconley.provo.features.math;

import com.jakeconley.miscellanea.util.Tree;
import com.jakeconley.provo.Provo;
import com.jakeconley.provo.utils.Utils;
import java.util.LinkedList;
import java.util.List;

public class Maths
{
    private static enum CharType{ WORD, NUMBER, WHITESPACE, OTHER }
    private static CharType analyzechar(char c)
    {
        switch(c)
        {
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                return CharType.WORD;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '-':
            case '.':
                return CharType.NUMBER;
            case ' ':
            case '\r':
            case '\n':
            case '\t':
                return CharType.WHITESPACE;
            default: return CharType.OTHER;
        }
    }

    private static enum Lex{ NUMBER, OPERATOR, OTHER }
    
    private static enum OperatorSyntax
    {
        BINARY(3), UNARY_PRECEDES(2), UNARY_POSTCEDES(2), SOLITARY(1), UNKNOWN(0);
        public final int Length;
        OperatorSyntax(int _Length){ Length = _Length; }
    }
    private static enum Operations{ UNDEFINED, EXPONENTIATION, MULTIPLICATION, ADDITION }
    private static enum Operator
    {        
        ADD("+", OperatorSyntax.BINARY, Operations.ADDITION),
        SUBTRACT("-", OperatorSyntax.BINARY, Operations.ADDITION),
        MULTIPLY("*", OperatorSyntax.BINARY, Operations.MULTIPLICATION),
        DIVIDE("/", OperatorSyntax.BINARY, Operations.MULTIPLICATION),
        GROUP_OPEN ("(", OperatorSyntax.SOLITARY),
        GROUP_CLOSE(")", OperatorSyntax.SOLITARY),
        SQUARE_ROOT("sqrt", OperatorSyntax.UNARY_PRECEDES, Operations.EXPONENTIATION),
        EXPONENT("^", OperatorSyntax.BINARY, Operations.EXPONENTIATION),
        UNKNOWN(null, OperatorSyntax.UNKNOWN);
        
        public final String Text;
        public final OperatorSyntax Syntax;
        public Operations Operation = Operations.UNDEFINED;
        
        Operator(String text, OperatorSyntax syntax)
        {
            this.Text = text;
            this.Syntax = syntax;
        }
        Operator(String text, OperatorSyntax syntax, Operations operation)
        {
            this(text, syntax);
            this.Operation = operation;
        }
        
        public static Operator FromString(String s)
        { 
            for(Operator t : Operator.values()) { if(t != UNKNOWN && t.Text.equals(s)) return t; }
            return UNKNOWN;
        }
    }
    private static class Symbol
    {
        public Lex Type;
        public Operator OperatorType;
        public String String;
        public double Value;
        public Symbol(Lex _Type, String _String)
        {
            this.Type = _Type;
            this.String = _String;
        }
        
        @Override
        public String toString(){ return "{" + Type.toString() + " " + (String != null ? String : "null") + "}"; }
        
        public static Symbol FromString(String s)
        {
            try
            {
                double val = Double.parseDouble(s);
                Symbol ret = new Symbol(Lex.NUMBER, s);
                ret.Value = val;
                return ret;
            }
            catch(NumberFormatException e) { }
            
            Operator token = Operator.FromString(s);
            if(token != Operator.UNKNOWN)
            {
                Symbol ret = new Symbol(Lex.OPERATOR, s);
                ret.OperatorType = token;
                return ret;
            }
            
            return new Symbol(Lex.OTHER, s);
        }
    }
    private static class MathNode
    {
        public boolean IsCalculable = false;
        public Symbol Symbol = null;
        public Operations Operation = Operations.UNDEFINED;
        
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("[MathNode ");
            builder.append((IsCalculable ? "calculable" : "!calculable "));
            builder.append(" Operations.");
            builder.append(Operation.toString());
            builder.append(": ");
            if(Symbol == null) builder.append("<PLACEHOLDER>");
            else builder.append(Symbol.toString());
            builder.append(" ]");
            return builder.toString();
        }
    }
    
    private static void GroupSymbols(Tree.Node<MathNode> node, List<Symbol> symbols) throws MathSyntaxException
    {
        int prev_i = 0;
        int group_open_count = 0;
        for(int i = 0; i < symbols.size(); i++)
        {
            Symbol symbol = symbols.get(i);
            if(symbol.OperatorType == Operator.GROUP_OPEN)
            {
                if(group_open_count == 0)
                {
                    if(i > prev_i)
                    {
                        for(Symbol s : symbols.subList(prev_i, i))
                        {
                            MathNode child_val = new MathNode();
                            child_val.Symbol = s;
                            node.addChild(child_val);
                        }
                    }
                    prev_i = i + 1;
                }
                group_open_count++;
            }
            if(symbol.OperatorType == Operator.GROUP_CLOSE)
            {
                group_open_count--;
                if(group_open_count < 0) throw new MathSyntaxException(MathSyntaxException.Types.UNEXPECTED_SYMBOL, symbol.String);
                
                if(group_open_count == 0)
                {
                    if(i > prev_i)
                    {
                        if(Provo.Debug)
                        {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Recursing to ");
                            for(Symbol s : symbols.subList(prev_i, i)) sb.append(s.toString());
                            Utils.PrlnDebug(sb.toString());
                        }
                        
                        // Add implicit multiplication
                        if(node.getChildren().size() - 1 >= 0 && node.getChildren().get(node.getChildren().size() - 1).getValue().Symbol.Type == Lex.NUMBER)
                        {
                            // Hardcoded this for no apparent reason :\\
                            Tree.Node<MathNode> newoperator = new Tree.Node<>(new MathNode());
                            newoperator.getValue().Symbol = new Symbol(Lex.OPERATOR, "*");
                            newoperator.getValue().Symbol.OperatorType = Operator.MULTIPLY;
                            node.getChildren().add(node.getChildren().size(), newoperator);
                        }
                        GroupSymbols(node.addChild(new MathNode()), symbols.subList(prev_i, i));//placeholder, sublist
                    }

                    prev_i = i + 1;
                }
            }
        }
        
        //uh why is this a conditional?
        //I had this as < for some reason
        if(prev_i <= (symbols.size() - 1))
        {
            for(Symbol s : symbols.subList(prev_i, symbols.size()))
            {
                MathNode child_val = new MathNode();
                child_val.Symbol = s;
                node.addChild(child_val);
            }
        }
    }
    private static double Evaluate(Tree.Node<MathNode> node) throws MathSyntaxException
    {
        if(node.getChildren().size() == 1)
        {
            Utils.Debug("Single child:");
            if(node.getChildren().get(0).getValue().Symbol == null) return Evaluate(node.getChildren().get(0));
            if(node.getChildren().get(0).getValue().Symbol.Type == Lex.NUMBER) return node.getChildren().get(0).getValue().Symbol.Value;
            else throw new MathSyntaxException(MathSyntaxException.Types.UNEXPECTED_SYMBOL, node.getValue().Symbol.String);
        }

        boolean was_operated = false;
        for(Operations operation : Operations.values())
        {
            Utils.Debug("For opereration " + operation.toString());
            for(int i = node.getChildren().size() - 1; i >= 0; i--)
            {//backwards
                if(node.getChildren().get(i).getValue().Symbol == null)
                {
                    double res = Evaluate(node.getChildren().get(i));
                    Symbol s = new Symbol(Lex.NUMBER, null);
                    s.Value = res;
                    node.getChildren().get(i).getValue().Symbol = s;
                    Utils.Debug("Evaluated children as " + res);
                    continue;
                }
                
                if(node.getChildren().get(i).getValue().IsCalculable) continue;
                Symbol current = node.getChildren().get(i).getValue().Symbol;
                if(current.Type != Lex.OPERATOR) continue;
                if(current.OperatorType.Operation != operation) continue;
                
                Symbol before = ((i - 1 >= 0) ? node.getChildren().get(i - 1).getValue().Symbol : null);
                Symbol after = ((i + 1) < node.getChildren().size() ? node.getChildren().get(i + 1).getValue().Symbol : null);
                Utils.Debug("BEFORE: " + (before != null ? before.String : "null"));
                Utils.Debug("AFTER:  " + (after != null ? after.String : "null"));
                double res;
                
                switch(current.OperatorType.Syntax)
                {
                    case BINARY:
                        if(before == null || after == null) throw new MathSyntaxException(MathSyntaxException.Types.OPERATOR_OUT_OF_PLACE, current.String);
                        if(before.Type != Lex.NUMBER || after.Type != Lex.NUMBER) throw new MathSyntaxException(MathSyntaxException.Types.EXPECTED_NUMBER, current.String);
                        
                        switch(current.OperatorType)
                        {
                            case ADD: res = before.Value + after.Value; break;
                            case SUBTRACT: res = before.Value - after.Value; break;
                            case MULTIPLY: res = before.Value * after.Value; break;
                            case DIVIDE: res = before.Value / after.Value; break;
                            case EXPONENT: res = Math.pow(before.Value, after.Value); break;
                            default: throw new UnsupportedOperationException("Invalid operator type " + current.OperatorType.toString() + " in case BINARY");
                        }
                        
                        current.OperatorType = Operator.UNKNOWN;
                        current.Value = res;
                        current.Type = Lex.NUMBER;
                        node.getChildren().remove(i + 1);
                        node.getChildren().remove(i - 1);
                        break;
                    case UNARY_PRECEDES:
                        if(after == null) throw new MathSyntaxException(MathSyntaxException.Types.OPERATOR_OUT_OF_PLACE, current.String);
                        if(after.Type != Lex.NUMBER) throw new MathSyntaxException(MathSyntaxException.Types.EXPECTED_NUMBER, current.String);
                        
                        switch(current.OperatorType)
                        {
                            case SQUARE_ROOT: res = Math.sqrt(after.Value); break;
                            default: throw new UnsupportedOperationException("Invalid operator type " + current.OperatorType.toString() + " in case UNARY_PRECEDES");
                        }
                    
                        current.OperatorType = Operator.UNKNOWN;
                        current.Value = res;
                        current.Type = Lex.NUMBER;
                        node.getChildren().remove(i + 1);
                        break;
                    default: throw new UnsupportedOperationException("Invalid syntax type " + current.OperatorType.Syntax.toString());
                }
                
                was_operated = true;
                i = node.getChildren().size() - 1;
                node.getChildren().get(i).getValue().IsCalculable = true;
            }
        }
        if(!was_operated) throw new MathSyntaxException(MathSyntaxException.Types.EXPECTED_OPERATOR, null);
        
        Symbol ret = node.getChildren().get(0).getValue().Symbol;
        return ret.Value;
    }
    public static double Calculate(String input) throws MathSyntaxException
    {
        input = input.toLowerCase();
        Tree<MathNode> tree = new Tree<>(new MathNode());

        List<Symbol> parsed = new LinkedList<>();
        
        char c_str [] = input.toCharArray();
        CharType prev_char_type = CharType.OTHER; int prev_char_i = 0;
        for(int i = 0; i < c_str.length; i++)
        {
            CharType cur_type = analyzechar(c_str[i]);
            
            if(i == 0)
            {
                prev_char_type = cur_type;
                continue;
            }
            
            // If type change, unrecognized character type, or - sign after uninitialized, or - sign not after space
            // Those latter two are to allow calculations like "3-2" without any spaces
            if(cur_type != prev_char_type || cur_type == CharType.OTHER || (prev_char_type == CharType.NUMBER && c_str[i] == '-' && prev_char_i != i) || (i > 1 && c_str[i - 1] == '-' && c_str[i - 2] != ' '))
            {
                String sub = input.substring(prev_char_i, i);
                if(prev_char_type != CharType.WHITESPACE) parsed.add(Symbol.FromString(sub));
                prev_char_i = i;
                prev_char_type = cur_type;
            }
        }
        parsed.add(Symbol.FromString(input.substring(prev_char_i, input.length())));
        
        for(Symbol s : parsed){ Utils.Debug(s.toString()); }
        
        GroupSymbols(tree.getRoot(), parsed);// Order of operations \o/ grouping first
        if(Provo.Debug)
        {
            tree.print();
            Utils.Debug(Double.toString(Evaluate(tree.getRoot())));
        }
        return Evaluate(tree.getRoot());
    }
}
