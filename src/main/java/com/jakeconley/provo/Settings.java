package com.jakeconley.provo;

import com.jakeconley.provo.utils.Utils;
import com.jakeconley.provo.yaml.YamlFile;
import java.io.File;

public class Settings
{
    private YamlFile Settings = new YamlFile("plugins/Provo/config.yml");
    
    public int Sorting_MaxClasses = -1;
    public int Sorting_MaxRulesPerClass = -1;
    public boolean Sorting_MRPC_IncludeHotbar = false;
    
    public void AnalyzeConfig()
    {
        Sorting_MaxClasses          = Settings.get().getInt("sorting.max-classes", -1);
        Sorting_MaxRulesPerClass    = Settings.get().getInt("sorting.max-rules-per-class", -1);
        Sorting_MRPC_IncludeHotbar  = Settings.get().getBoolean("sorting.max-rules-per-class_include-hotbar", false);
    }
    public boolean LoadFile(boolean analyze)
    {        
        try
        {            
            Settings.LoadWithDefault("/config.yml");
            if(analyze) this.AnalyzeConfig();
            return true;
        }
        catch(Exception e)
        {
            Utils.LogException("reading config", e);
            return false;
        }
    }
    public boolean SaveFile()
    {
        try
        {
            Settings.SaveFile();
            return true;
        }
        catch(Exception e)
        {
            Utils.LogException("saving config", e);
            return false;
        }
    }
    
    // TODO:  Analyze
}
