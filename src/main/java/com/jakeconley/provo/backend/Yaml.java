package com.jakeconley.provo.backend;

import com.jakeconley.provo.utils.Utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.bukkit.configuration.file.YamlConfiguration;

public class Yaml
{
    File File;
    YamlConfiguration Yaml;
    
    public File getFile(){ return File; }
    public YamlConfiguration get(){ return Yaml; }
    
    public Yaml(String path)
    {
        File = new File(path);
        Yaml = new YamlConfiguration();
    }
    
    // Shamelessly stole this from a bukkit tutorial
    public static boolean CopyTo(InputStream in, File file)
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
            Utils.LogException("while copying default YAML", e);
            return false;
        }
    }
    
    public boolean Load()
    {
        try
        {            
            Yaml.load(this.File);
            return true;
        }
        catch(Exception e)
        {
            Utils.LogException("loading YAML " + this.File.getName(), e);
            return false;
        }
    }
    /**
     * Load the file, and if it doesn't exist, copy it from a default path within the jar
     * @param defpath Path to copy from
     * @return True if successful
     */
    public boolean LoadWithDefault(String defpath)
    {
        if(!File.exists())
        {
            Utils.Warning("YAML " + File.getName() + " does not exist, attempting to create default...");
            if(!CopyTo(this.getClass().getResourceAsStream(defpath), File)) return false;
            Utils.Info("Creation successful.");
        }
        
        return this.Load();
    }
    
    public boolean SaveFile()
    {
        try
        {
            Yaml.save(this.File);
            return true;
        }
        catch(Exception e)
        {
            Utils.LogException("saving YAML " + File.getName(), e);
            return false;
        }
    }
}