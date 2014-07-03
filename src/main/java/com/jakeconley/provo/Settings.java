package com.jakeconley.provo;

import com.jakeconley.provo.utils.Utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.bukkit.configuration.file.YamlConfiguration;

public class Settings
{
    private File SettingsFile = new File("plugins/Provo/config.yml");
    public YamlConfiguration SettingsYaml = new YamlConfiguration();
    
    // Shamelessly stole this from a bukkit tutorial
    private boolean copy(InputStream in, File file)
    {
        try
        {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0)
            {
                out.write(buf,0,len);
            }
            out.close();
            in.close();
            return true;
        }
        catch (Exception e) 
        {
            Utils.LogException("while copying default config", e);
            return false;
        }
    }
    
    public boolean LoadFile()
    {
        if(!SettingsFile.exists())
        {
            Utils.Info("Could not find " + SettingsFile.getPath() + ", attempting to create...");
            if(!copy(this.getClass().getResourceAsStream("/config.yml"), SettingsFile))// Kinda hacky but whatevs
            {
                Utils.Severe("Error creating config.yml, aborting");
                return false;
            }
        }
        
        try
        {            
            SettingsYaml.load(SettingsFile);
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
            SettingsYaml.save(SettingsFile);
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
