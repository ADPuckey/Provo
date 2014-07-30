package com.jakeconley.provo.backend;

import com.jakeconley.provo.functions.mail.Mail;
import com.jakeconley.provo.functions.mail.MailResult;
import com.jakeconley.provo.utils.Utils;
import com.jakeconley.provo.yaml.YamlFile;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.ConfigurationSection;

public class MailBackend
{
    private YamlFile LoadMailFile(String uuid) throws Exception
    {
        YamlFile file = new YamlFile("plugins/Provo/mail/" + uuid + ".yml");
        file.LoadDefaultNew();
        return file;
    }
    
    private Mail MailFromFile(YamlFile mail, ConfigurationSection section, int index) throws ProvoFormatException, Exception
    {
        long timestamp = section.getLong("timestamp", 0x0);
        String sender_unparsed = section.getString("sender");
        String message = section.getString("message");
        
        if(timestamp == 0x0)
        {
            ProvoFormatException e = new ProvoFormatException("No timestamp set in index " + index);
            e.setType(ProvoFormatException.Type.BACKEND_FORMAT);
            e.setFilePath(mail.getFile().getPath());
            throw e;
        }
        if(sender_unparsed == null)
        {
            ProvoFormatException e = new ProvoFormatException("No sender set in index " + index);
            e.setType(ProvoFormatException.Type.BACKEND_FORMAT);
            e.setFilePath(mail.getFile().getPath());
            throw e;
        }
        if(message == null)
        {
            ProvoFormatException e = new ProvoFormatException("No message set in index " + index);
            e.setType(ProvoFormatException.Type.BACKEND_FORMAT);
            e.setFilePath(mail.getFile().getPath());
            throw e;
        }
        
        UUID sender = Utils.ParseUUID(sender_unparsed);
        if(sender == null)
        {
            ProvoFormatException e = new ProvoFormatException("No unable to parse UUID \"" + sender_unparsed + "\" in index " + index);
            e.setType(ProvoFormatException.Type.BACKEND_FORMAT);
            e.setFilePath(mail.getFile().getPath());
            throw e;
        }
        
        return new Mail(timestamp, sender, message);
    }    
    public MailResult LoadMail(String uuid) throws ProvoFormatException, Exception
    {
        YamlFile mail = LoadMailFile(uuid);
        MailResult ret = new MailResult();
        if(mail.get().getMapList("mail") == null) return ret;
        List<ConfigurationSection> maplist = mail.get().SectionalizeMapList("mail");
        
        for(int i = 0; i < maplist.size(); i++)
        {
            Mail mail_o = MailFromFile(mail, maplist.get(i), i);
            ret.addMail(mail_o);
            ret.addSender(mail_o.getSenderUUID());
        }
        
        return ret;
    }    
    public void SendMail(String uuid, Mail mail) throws Exception
    {
        YamlFile mailfile = LoadMailFile(uuid);
        List<Map<?, ?>> section = mailfile.get().getMapList("mail");
        if(section == null) section = new LinkedList<>();
        
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", mail.getTimestamp());
        map.put("message", mail.getMessage());
        map.put("sender", mail.getSenderUUID().toString());
        section.add(map);
        
        mailfile.get().set("mail", section);
        mailfile.SaveFile();
    }
    public void ClearMail(String uuid) throws Exception
    {
        YamlFile mail = LoadMailFile(uuid);
        mail.get().set("mail", null);
        mail.SaveFile();
    }
}
