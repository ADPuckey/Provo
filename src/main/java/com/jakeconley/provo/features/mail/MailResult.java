package com.jakeconley.provo.features.mail;

import com.jakeconley.provo.utils.Utils;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.ChatColor;

public class MailResult
{
    private final List<Mail> Mails;
    private final Set<UUID> Senders;
    
    public List<Mail> getMails(){ return Mails; }
    public void addMail(Mail mail){ Mails.add(mail); }
    public void addSender(UUID sender){ Senders.add(sender); }
    
    public MailResult()
    {
        Mails = new LinkedList<>();
        Senders = new HashSet<>();
    }
    
    public List<String> MakePlayerStrings()
    {
        List<String> ret = new LinkedList<>();
        
        Map<UUID, String> uuid_to_sendername = Utils.ResolveNames(new LinkedList(Senders));
        
        for(Mail mail : Mails)
        {
            StringBuilder sb = new StringBuilder();
            String sender = uuid_to_sendername.get(mail.getSenderUUID());
            
            sb.append(ChatColor.AQUA).append('[').append(Utils.TimeOffsetMilliseconds(mail.getTimestamp())).append("]");
            if(sender != null) sb.append(ChatColor.YELLOW).append(" From ").append(sender).append(": ").append(ChatColor.RESET);
            else sb.append(ChatColor.RED).append(" [Error fetching sender name] ").append(ChatColor.RESET);
            sb.append(mail.getMessage());
            
            ret.add(sb.toString());
        }
        
        return ret;
    }
}