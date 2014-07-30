package com.jakeconley.provo.notifications;

import com.jakeconley.provo.backend.ProvoFormatException;
import com.jakeconley.provo.notifications.Notification.Importance;
import com.jakeconley.provo.yaml.YamlFile;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public class NotificationsBackend
{
    public YamlFile LoadPlayerNotificationsFile(String uuid) throws Exception
    {
        YamlFile file = new YamlFile("plugins/Provo/notifications/" + uuid + ".yml");
        file.LoadDefaultNew();
        return file;
    }
    
    private Notification NotificationFromFile(YamlFile notifications, String id) throws ProvoFormatException, Exception
    {
	ConfigurationSection section = notifications.get().getConfigurationSection(id);
	if(section == null) return null;

	List<String> text = section.getStringList("text");
	if(text == null)
	{
	    ProvoFormatException e = new ProvoFormatException("No text set in notification " + id);
	    e.setFilePath(notifications.getFile().getPath());
	    e.setType(ProvoFormatException.Type.BACKEND_FORMAT);
	    throw e;
	}

	String origin = section.getString("origin");
	if(origin == null)
	{
	    ProvoFormatException e = new ProvoFormatException("No origin set in notification " + id);
	    e.setFilePath(notifications.getFile().getPath());
	    e.setType(ProvoFormatException.Type.BACKEND_FORMAT);
	    throw e;
	}

	Importance importance = Importance.DISPLAY_ON_JOIN;
	try
	{
	    String str = section.getString("importance");
	    if(str == null) 
	    {
		ProvoFormatException e = new ProvoFormatException("No importance set in notification " + id);
		e.setFilePath(notifications.getFile().getPath());
		throw e;
	    }
	    importance = Importance.valueOf(str);
	}
	catch(IllegalArgumentException exc)
	{
	    ProvoFormatException e = new ProvoFormatException("Invalid importance in notification " + id);
	    e.setFilePath(notifications.getFile().getPath());
	    e.setType(ProvoFormatException.Type.BACKEND_FORMAT);
	    throw e;
	}

	long timestamp = section.getLong("timestamp", Notification.TIMESTAMP_DEFAULT);

	Notification ret = new Notification(id, origin, text, importance);
        ret.setTimestamp(timestamp);
	ret.setAutoDelete(section.getBoolean("autodelete", Notification.AUTODELETE_DEFAULT));
	return ret;
    }
    
    public Notification GetNotification(String uuid, String id) throws ProvoFormatException, Exception
    {
        YamlFile notifications = LoadPlayerNotificationsFile(uuid);
	return NotificationFromFile(notifications, id);
    }
    /**
     * Get the list of notifications waiting to be viewed by a player.
     * @param uuid The UUID of the player whose notifications are to be fetched
     * @return List of notifications
     * @throws ProvoFormatException Invalid formatting in the YAML file
     * @throws Exception Other unspecified exception
     */
    public List<Notification> GetNotifications(String uuid) throws ProvoFormatException, Exception
    {
        YamlFile notifications = LoadPlayerNotificationsFile(uuid);
        List<Notification> ret = new LinkedList<>();        
        for(String id : notifications.get().getKeys(false)) ret.add(NotificationFromFile(notifications, id));
        return ret;
    }
    
    /**
     * See if a notification with an id exists
     * @param uuid The UUID of the player whose file is to be checked
     * @param notification_id The ID of the notification to verify
     * @return True if exists, otherwise false
     * @throws Exception Unspecified exception
     */
    public boolean NotificationExists(String uuid, String notification_id) throws Exception
    {
        YamlFile notifications = LoadPlayerNotificationsFile(uuid);
        return (notifications.get().getConfigurationSection(notification_id) != null);
    }
    
    /**
     * Create a notification for a player.
     * @param uuid The uuid of the player for whom the notification is intended
     * @param n The Notification object to write
     * @throws Exception Unspecified exception
     */
    public void WriteNotification(String uuid, Notification n) throws Exception
    {
        YamlFile notifications = LoadPlayerNotificationsFile(uuid);
        ConfigurationSection section = notifications.get().getConfigurationSection(n.getId());
        if(section == null) section = notifications.get().createSection(n.getId());
        section.set("text", n.getText());
        section.set("origin", n.getOrigin());
        section.set("importance", n.getImportance().toString());
        if(n.getTimestamp() != Notification.TIMESTAMP_DEFAULT) section.set("timestamp", n.getTimestamp());
        if(n.isAutoDelete() != Notification.AUTODELETE_DEFAULT) section.set("autodelete", n.isAutoDelete());
        notifications.SaveFile();
    }
    
    /**
     * Update an existing notification for a player.  Will return false if no such notification exists.
     * @param uuid The UUID of the player who owns the notification.
     * @param id The ID of the notification to update
     * @param values A map of the values to add or change
     * @return True if it was updated, false if not.
     * @throws Exception Unspecified exception
     */
    public boolean UpdateNotification(String uuid, String id, Map<String, Object> values) throws Exception
    {
        YamlFile notifications = LoadPlayerNotificationsFile(uuid);
        ConfigurationSection section = notifications.get().getConfigurationSection(id);
        if(section == null) return false;
        
        for(Map.Entry<String, Object> entry : values.entrySet()){ section.set(entry.getKey(), entry.getValue()); }
        return true;
    }
    
    /**
     * Clear all of the notifications of a player.  To be used when they are all read.
     * @param uuid The UUID of the player whose notifications are to be cleared
     * @throws Exception Unspecified exception
     */
    public void ClearNotifications(String uuid) throws Exception
    {
        PrintWriter writer = new PrintWriter(LoadPlayerNotificationsFile(uuid).getFile());
        writer.close();
    }
    
    /**
     * Delete a player's notification.
     * @param uuid The UUID of the player whose notification is to be deleted
     * @param notification_id The ID of the notification to delete
     * @throws Exception Unspecified exception
     */
    public void DeleteNotification(String uuid, String notification_id) throws Exception
    {
        YamlFile file = LoadPlayerNotificationsFile(uuid);
        file.get().set(notification_id, null);
        file.SaveFile();
    }
    /**
     * Delete a list of notifications.
     * @param uuid The UUID of the player whose notifications are to be deleted
     * @param toDelete The list of notification IDs to delete
     * @throws Exception Unspecified exception
     */
    public void DeleteNotificationList(String uuid, List<String> toDelete) throws Exception
    {
        YamlFile file = LoadPlayerNotificationsFile(uuid);
        for(String s : toDelete) file.get().set(s, null);
        file.SaveFile();
    }
}
