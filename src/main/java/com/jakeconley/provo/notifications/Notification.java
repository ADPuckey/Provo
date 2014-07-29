package com.jakeconley.provo.notifications;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.ChatColor;

public class Notification
{
    public static enum Importance{ DISPLAY_ON_JOIN, NOT_IMPORTANT, IMPORTANT }
    
    public static final long TIMESTAMP_DEFAULT = 0x0;
    public static final boolean AUTODELETE_DEFAULT = true;

    private final String Id;
    private final List<String> Text;
    private final String Origin;
    private final Importance Importance;
    private final long Timestamp;
    private boolean AutoDelete;
    
    public String getId(){ return Id; }
    public String getOrigin(){ return Origin; }
    public List<String> getText(){ return Text; }
    public Importance getImportance(){ return Importance; }
    public long getTimestamp(){ return Timestamp; }
    public boolean isAutoDelete(){ return AutoDelete; }
    public Notification addText(String line){ Text.add(line); return this; }
    public Notification setAutoDelete(boolean value){ AutoDelete = value; return this; }
    
    Notification(String _Id, String _Origin, List<String> _Text, Importance _Importance, long _Timestamp)
    {
        this.Id = _Id;
        this.Origin = _Origin;
        this.Text = _Text;
        this.Importance = _Importance;
        this.Timestamp = _Timestamp;
    }
    public Notification(String _Id, String _Origin, List<String> _Text, Importance _Importance){ this(_Id, _Origin, _Text, _Importance, (new Date()).getTime()); }
    public Notification(String _Id, String _Origin, Importance _Importance){ this(_Id, _Origin, new LinkedList<String>(), _Importance); }
    
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
    public List<String> toPlayerFriendlyStringList()
    {
        List<String> ret = new LinkedList<>();
        String time = CalcTimeOffset();
        for(String s : Text)
        {
            StringBuilder sb = new StringBuilder();
            if(Importance == Importance.IMPORTANT) sb.append(ChatColor.RED).append("[!] ");
            if(Origin != null) sb.append(ChatColor.GREEN).append("[").append(Origin).append("] ");
            if(time != null){ sb.append(ChatColor.GREEN).append("[").append(time).append("] ");}
            else{ sb.append(ChatColor.GREEN).append("[ ] "); }
            sb.append(ChatColor.RESET);
            sb.append(s);
            ret.add(sb.toString());
        }
        return ret;
    }
}
