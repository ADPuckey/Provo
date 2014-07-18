package com.jakeconley.provo.yaml;

import com.jakeconley.provo.utils.Utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.bukkit.configuration.InvalidConfigurationException;

public class YamlFile
{
    File File;
    Yaml Yaml;
    
    public File getFile(){ return File; }
    public Yaml get(){ return Yaml; }
    
    public YamlFile(String path)
    {
        File = new File(path);
        Yaml = new Yaml();
    }
    
    // Shamelessly stole this from a bukkit tutorial
    public static boolean CopyTo(InputStream in, File file) throws Exception
    {
        try(OutputStream out = new FileOutputStream(file))
        {
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
            throw e;
        }
    }
    
    public void Load() throws Exception
    {
        try{ Yaml.load(this.File); }
        catch(InvalidConfigurationException e)
        {
            Utils.Severe("Invalid configuration on file " + this.File.getName() + ".  Check your YAML.");
            throw e;
        }
        catch(Exception e)
        {
            Utils.Severe("Error loading YAML " + this.File.getName() + ": " + e.toString());
            throw e;
        }
    }
    /**
     * Load the file, and if it doesn't exist, copy it from a default path within the jar
     * @param defpath Path to copy from
     * @throws java.lang.Exception
     */
    public void LoadWithDefault(String defpath) throws Exception
    {
        if(!File.exists())
        {
            Utils.Warning("YAML " + File.getName() + " does not exist, attempting to create default...");
            CopyTo(this.getClass().getResourceAsStream(defpath), File);
            Utils.Info("Creation successful.");
        }
        
        this.Load();
    }
    public void LoadDefaultNew() throws Exception
    {
        try{ if(!File.exists()) File.createNewFile(); }
        catch(Exception e)
        {
            Utils.Severe("Could not create new file: " + e.toString());
            throw e;
        }
        
        this.Load();
    }
    
    public void SaveFile() throws Exception
    {
        Utils.Debug("SAVING YAML\r\n" + Yaml.saveToString());
        /*
        try{ Yaml.save(this.File); }
        catch(Exception e)
        {
            Utils.Severe("Error saving YAML " + File.getName() + ": " + e.toString());
            throw e;
        }
                */
    }
}