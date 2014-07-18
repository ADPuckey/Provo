package com.jakeconley.provo.yaml;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class Yaml extends YamlConfiguration
{
    /**
     * Get a list of maps as a list of ConfigurationSections.  Note that this will change markup.  Call DesectionalizeMapList to undo this.
     */
    public List<ConfigurationSection> SectionalizeMapList(String path)
    {
        List<Map<?,?>> list = this.getMapList(path);
        if(list == null) return null;
        
        List<ConfigurationSection> ret = new LinkedList<>();
        
        for(int i = 0; i < list.size(); i++)
        {
            Map<?,?> curmap = list.get(i);
            
            final String secpath = path + "." + Integer.toString(i);
            if(this.getConfigurationSection(secpath) != null)
            {
                ret.add(this.getConfigurationSection(secpath));
                continue;
            }
            ConfigurationSection section = this.createSection(secpath);
            
            this.convertMapsToSections(curmap, section);            
            ret.add(section);
        }
        
        return ret;
    }
    public List<Map<String, Object>> DesectionalizeMapList(String path)
    {
        ConfigurationSection main = this.getConfigurationSection(path);
        if(main == null) return null;
        
        List<Map<String,Object>> ret = new LinkedList<>();
        
        for(String s : main.getKeys(false))
        {
            ConfigurationSection sub = main.getConfigurationSection(s);
            Map<String,Object> nmap = new HashMap<>();
            for(String subkey : sub.getKeys(false)){ nmap.put(subkey,  sub.get(subkey)); }
            ret.add(nmap);
        }
        
        this.set(path, ret);
        
        return ret;
    }
}
