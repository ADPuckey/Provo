package com.jakeconley.provo.yaml;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class Yaml extends YamlConfiguration
{
    /**
     * Get a list of maps as a list of ConfigurationSections.  NOTE:  WILL OVERWRITE FILE.  DO NOT DO THIS AND THEN SAVE.  ONLY FOR LOADING
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
}
