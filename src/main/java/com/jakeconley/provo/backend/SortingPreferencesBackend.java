package com.jakeconley.provo.backend;

import java.util.List;

public class SortingPreferencesBackend
{
    public static List<String> FetchPublicItemGroups() throws Exception
    {
        Yaml itemgroups = new Yaml("plugins/Provo/sorting/itemgroups.yml");
        itemgroups.LoadWithDefault("/itemgroups.yml");
        return itemgroups.get().getStringList("");
    }
}
