package com.jakeconley.provo.utils;

import com.evilmidget38.NameFetcher;
import com.evilmidget38.UUIDFetcher;
import com.jakeconley.provo.Provo;
import static com.jakeconley.provo.notifications.Notification.TIMESTAMP_DEFAULT;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

public class Utils
{
    private static final Logger log = Logger.getLogger("");
    
    public static void LogException(String action, Exception e)
    {
        if(action != null) Severe("Error " + action + ":");
        e.printStackTrace();
    }
    public static void Info(String message){ log.info("[Provo] " + message); }
    public static void Warning(String message){ log.warning("[Provo] " + message); }
    public static void Severe(String message){ log.severe("[Provo] " + message); }
    public static void PrlnDebug(String message){ log.info("[Provo - DEBUG] " + message); }
    public static void Debug(String message){ if(Provo.Debug) PrlnDebug(message); }
    
    public static boolean LocationEquals(Location l1, Location l2)
    {
        if(l1.getX() != l2.getX()) return false;
        if(l1.getY() != l2.getY()) return false;
        if(l1.getZ() != l2.getZ()) return false; 
        if(l1.getPitch() != l2.getPitch()) return false;
        if(l1.getYaw() != l2.getYaw()) return false;
        if(l1.getWorld() != l2.getWorld()) return false; //if this fails, use world names
        
        return true;
    }
    
    public static Material GetMaterial(String s)
    {
        Material m = Material.matchMaterial(s);
        
        // MATERIAL FIXES GO HERE
        if(s.equalsIgnoreCase("carrot")) m = Material.CARROT_ITEM;
        if(s.equalsIgnoreCase("potato")) m = Material.POTATO_ITEM;
        if(s.equalsIgnoreCase("lead")) m = Material.LEASH;
        
        return m;
    }
    
    public static void ShowCommand(CommandSender cs, String command, String description){ cs.sendMessage(ChatColor.YELLOW + command + ChatColor.WHITE + ": " + description); }
    
    public static String DashUUID(String undashed)
    {
        String s = undashed;
        
        if(undashed.contains("-")) s = undashed.replaceAll("-", "");
        if(s.length() != 32) return null;
        
        return s.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
    }
    public static UUID ParseUUID(String uuid)
    {
        if(uuid == null) return null;
        try{ return UUID.fromString(uuid); }
        catch(IllegalArgumentException e){ return null; }
    }
    /**
     * Parse an integer from a string, without java's shitty exception rule.
     * @param integer The string to parse
     * @param def Default value
     * @return A parsed int instance
     */
    public static int ParseInt(String integer, int def)
    {
        try{ return Integer.parseInt(integer); }
        catch(NumberFormatException e){ return def; }
    }
    
    public static String TimeOffsetMilliseconds(long Timestamp)
    {
        long diff = (new Date()).getTime() - Timestamp;
        
        long SECOND = 1000;
        long MINUTE = SECOND * 60;
        long HOUR = MINUTE * 60;
        long DAY = HOUR * 24;
        long WEEK = DAY * 7;
        
        if(diff > WEEK) return (Long.toString((diff - (diff % WEEK)) / WEEK) + " week(s) ago");
        if(diff > DAY) return (Long.toString((diff - (diff % DAY)) / DAY) + " day(s) ago");
        if(diff > HOUR) return (Long.toString((diff - (diff%HOUR)) / HOUR) + " hour(s) ago");
        if(diff > MINUTE) return (Long.toString((diff - (diff%MINUTE)) / MINUTE) + " minute(s) ago");
        return (Long.toString((diff - (diff % SECOND)) / SECOND) + " second(s) ago");
    }
    
    public static UUID ResolveUUID(String name)
    {
        if(name == null) return null;
        UUIDFetcher fetcher = new UUIDFetcher(Arrays.asList(name));
        try
        {
            Map<String, UUID> response = fetcher.call();
            return response.get(name);
        }
        catch(Exception e)
        {
            Severe("Exception " + e.toString() + " while resolving UUID.");
            return null;
        }
    }
    public static Map<String, UUID> ResolveUUIDs(List<String> names)
    {
        UUIDFetcher fetcher = new UUIDFetcher(names);
        try{ return fetcher.call(); }
        catch(Exception e)
        {
            Severe("Exception " + e.toString() + " while resolving UUIDs");
            return new HashMap<>();
        }
    }
    public static String ResolveName(UUID uuid)
    {
        if(uuid == null) return null;
        NameFetcher fetcher = new NameFetcher(Arrays.asList(uuid));
        try
        {
            Map<UUID, String> response = fetcher.call();
            return response.get(uuid);
        }
        catch(Exception e)
        {
            Severe("Exception " + e.toString() + " while resolving name.");
            return null;
        }
    }
    public static Map<UUID, String> ResolveNames(List<UUID> uuids)
    {
        NameFetcher fetcher = new NameFetcher(uuids);
        try{ return fetcher.call(); }
        catch(Exception e)
        {
            Severe("Exception " + e.toString() + " while resolving names.");
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
