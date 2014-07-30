package com.jakeconley.provo.bukkit;
 
import com.jakeconley.provo.Provo;
import com.jakeconley.provo.functions.math.MathSyntaxException;
import com.jakeconley.provo.functions.math.Maths;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
 
public class CommandsMath implements CommandExecutor
{
    private final Provo plugin;
    public CommandsMath(Provo parent){ plugin = parent; }
 
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        Player player = null;
        String player_uuid = null;
        if(sender instanceof Player)
        {
            player = (Player) sender;
            player_uuid = player.getUniqueId().toString();
        }
        // Start
 if(label.equalsIgnoreCase("math"))
{
    if(args.length == 0)
    {
        sender.sendMessage(ChatColor.YELLOW + "Type in a math query to execute!  For more info, see /math help.");
        return true;
    }
    
    if(args.length == 1 && args[0].equalsIgnoreCase("help"))
    {
        sender.sendMessage(Provo.COMMAND_TRADEMARK);
        sender.sendMessage("/math supports standard four-function operators (+ for addition, - for subtraction, * for multiplication, / for division)");
        sender.sendMessage("Also provided is an exponentiation operator (^) as well as a \"sqrt\" keyword.  Use sqrt before a number to return its square root.");
        sender.sendMessage("The plugin will try to make a \"-\" in front of a number a negative sign; to force it to be a subtraction operator simply put a space between the - and the number.");
        sender.sendMessage("The standard PEMDAS order of operations is followed.");
        sender.sendMessage(ChatColor.YELLOW + "Type /math <query> to calculate something!");
        return true;
    }
    
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < args.length; i++)
    {
        if(i != 0) sb.append(" ");
        sb.append(args[i]);
    }
    
    try{ sender.sendMessage(Double.toString(Maths.Calculate(sb.toString()))); }
    catch(MathSyntaxException e){ Messages.ReportMathSyntaxException(sender, e); }
    catch(Exception e){ Messages.ReportException(sender, e); }
    return true;
}
        // End
        return true;
    }
}