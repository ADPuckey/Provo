package com.jakeconley.provo.notifications;

import com.jakeconley.provo.utils.Utils;
import java.util.Date;
import org.bukkit.ChatColor;

public class Notification
{
    public static enum Importance{ DISPLAY_ON_JOIN, NOT_IMPORTANT }
    
    public static final long TIMESTAMP_DEFAULT = 0x0;

    private final String Id;
    private final String Text;
    private final Importance Importance;
    private final long Timestamp;
    
    public String getId(){ return Id; }
    public String getText(){ return Text; }
    public Importance getImportance(){ return Importance; }
    public long getTimestamp(){ return Timestamp; }
    
    Notification(String _Id, String _Text, Importance _Importance, long _Timestamp)
    {
        this.Id = _Id;
        this.Text = _Text;
        this.Importance = _Importance;
        this.Timestamp = _Timestamp;
    }
    public Notification(String _Id, String _Text, Importance _Importance){ this(_Id, _Text, _Importance, (new Date()).getTime()); }
    
    public String CalcTimeOffset()
    {
        if(Timestamp == TIMESTAMP_DEFAULT) return null;
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
    
    @Override
    public String toString(){ return "NOTIFICATION{" + Importance.toString() + " " + Timestamp + "} \"" + Text + "\""; }
    public String toPlayerFriendlyString()
    {
        String ret = new String();
        String time = CalcTimeOffset();
        if(time != null){ ret += ChatColor.GREEN + "[" + time + "] " + ChatColor.RESET; }
        else{ ret += ChatColor.GREEN + "[ ] " + ChatColor.WHITE; }
        ret += Text;
        return ret;
    }
}
