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
            Utils.LogException("loading YAML " + this.File.getPath(), e);
            return false;
        }
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
            Utils.LogException("saving config", e);
            return false;
        }
    }
}