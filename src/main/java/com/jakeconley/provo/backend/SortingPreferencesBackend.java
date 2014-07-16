package com.jakeconley.provo.backend;

import java.util.List;
import java.util.Set;

public class SortingPreferencesBackend
{
    public static Set<String> FetchPublicItemGroups() throws Exception
    {
        Yaml itemgroups = new Yaml("plugins/Provo/sorting/itemgroups.yml");
        itemgroups.LoadWithDefault("/itemgroups.yml");
        return itemgroups.get().getKeys(false);
    }
    public static List<String> FetchPublicItemGroup(String name) throws Exception
    {
        Yaml itemgroups = new Yaml("plugins/Provo/sorting/itemgroups.yml");
        itemgroups.LoadWithDefault("/itemgroups.yml");
        return itemgroups.get().getStringList(name);
    }
}
