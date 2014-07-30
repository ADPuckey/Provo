package com.jakeconley.provo.notifications;

import com.jakeconley.provo.utils.Utils;
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
    private long Timestamp;
    private boolean AutoDelete;
    
    public String getId(){ return Id; }
    public String getOrigin(){ return Origin; }
    public List<String> getText(){ return Text; }
    public Importance getImportance(){ return Importance; }
    public long getTimestamp(){ return Timestamp; }
    public boolean isAutoDelete(){ return AutoDelete; }
    public Notification addText(String line){ Text.add(line); return this; }
    public Notification setAutoDelete(boolean value){ AutoDelete = value; return this; }
    public Notification removeTimestamp(){ Timestamp = TIMESTAMP_DEFAULT; return this; }
    Notification setTimestamp(long value){ Timestamp = value; return this; }
    
    private Notification(String _Id, String _Origin, List<String> _Text, Importance _Importance, long _Timestamp)
    {
        this.Id = _Id;
        this.Origin = _Origin;
        this.Text = _Text;
        this.Importance = _Importance;
        this.Timestamp = _Timestamp;
    }
    /**
     * @param _Id The ID of the notification being stored
     * @param _Origin The name of the feature or plugin sending it (e.g. "Mail" or "Essentials")
     * @param _Text A list of messages to be sent to the player
     * @param _Importance The importance of the message
     */
    public Notification(String _Id, String _Origin, List<String> _Text, Importance _Importance){ this(_Id, _Origin, _Text, _Importance, (new Date()).getTime()); }
    /**
     * @param _Id The ID of the notification being stored
     * @param _Origin The name of the feature or plugin sending it (e.g. "Mail" or "Essentials")
     * @param _Importance The importance of the message
     */
    public Notification(String _Id, String _Origin, Importance _Importance){ this(_Id, _Origin, new LinkedList<String>(), _Importance); }
    
    public String CalcTimeOffset(){ return Utils.TimeOffsetMilliseconds(Timestamp); }
    
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
            sb.append(ChatColor.RESET);
            sb.append(s);
            ret.add(sb.toString());
        }
        return ret;
    }
}
