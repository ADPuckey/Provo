package com.jakeconley.provo.backend;

import com.jakeconley.provo.functions.sorting.PreferencesClass;
import com.jakeconley.provo.functions.sorting.PreferencesRule;
import java.util.List;
import java.util.Set;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

public class SortingPreferencesBackend
{
    public Set<String> FetchPublicItemGroups() throws Exception
    {
        Yaml itemgroups = new Yaml("plugins/Provo/sorting/itemgroups.yml");
        itemgroups.LoadWithDefault("/itemgroups.yml");
        return itemgroups.get().getKeys(false);
    }
    public List<String> FetchPublicItemGroup(String name) throws Exception
    {
        Yaml itemgroups = new Yaml("plugins/Provo/sorting/itemgroups.yml");
        itemgroups.LoadWithDefault("/itemgroups.yml");
        return itemgroups.get().getStringList(name);
    }
    
    public ConfigurationSection PreferencesRuleToYAML(Configuration parent, PreferencesRule rule)
    {
    }
    
    public Set<String> FetchPlayerPreferences(String uuid) throws Exception
    {
        Yaml y = new Yaml("plugins/Provo/sorting/player_preferences/" + uuid + ".yml");
        y.LoadDefaultNew();
        return y.get().getKeys(false);
    }
    
    public PreferencesClass FetchPlayerPreferencesClass(String uuid, String name) throws Exception
    {
        Yaml y = new Yaml("plugins/Provo/sorting/player_preferences/" + uuid);
        y.LoadDefaultNew();
        
        ConfigurationSection pclass = y.get().getConfigurationSection(name);
        if(pclass == null) return null;
        
        
    }
    public void WritePreferencesClass(String uuid, PreferencesClass v) throws Exception
    {
        Yaml y = new Yaml("plugins/Provo/sorting/player_preferences/" + uuid);
        y.LoadDefaultNew();
    }
}
