package com.jakeconley.provo.backend;

import com.jakeconley.provo.functions.sorting.PreferencesClass;
import com.jakeconley.provo.functions.sorting.PreferencesRule;
import com.jakeconley.provo.utils.Utils;
import com.jakeconley.provo.utils.inventory.InventoryCoords;
import com.jakeconley.provo.utils.inventory.InventoryRange;
import com.jakeconley.provo.utils.inventory.InventoryType;
import com.jakeconley.provo.yaml.YamlFile;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;

public class SortingPreferencesBackend
{
    public YamlFile LoadPublicItemGroupsYaml() throws Exception
    {
        YamlFile itemgroups = new YamlFile("plugins/Provo/sorting/itemgroups.yml");
        itemgroups.LoadWithDefault("/sorting_defaults/itemgroups.yml");
        return itemgroups;
    }
    public YamlFile LoadPlayerClassesYaml(String uuid) throws Exception
    {        
        YamlFile y = new YamlFile("plugins/Provo/sorting/player_classes/" + uuid + ".yml");
        y.LoadWithDefault("/sorting_defaults/player_class.yml");
        return y;
    }
    
    
    public Set<String> FetchPublicItemGroups() throws Exception
    {
        return LoadPublicItemGroupsYaml().get().getKeys(false);
    }
    public List<String> FetchPublicItemGroup(String name) throws Exception
    {
        return LoadPublicItemGroupsYaml().get().getStringList(name);
    }
    
    public PreferencesRule PreferencesRuleFromYaml(ConfigurationSection section, InventoryType it) throws ProvoFormatException
    {
        InventoryCoords area_ini = InventoryCoords.FromString(section.getString("area_ini"), it);
        InventoryCoords area_fin = InventoryCoords.FromString(section.getString("area_fin"), it);
        if(area_ini == null) throw new ProvoFormatException("Invalid " + section.getCurrentPath() + ".area_ini");
        if(area_fin == null) throw new ProvoFormatException("Invalid " + section.getCurrentPath() + ".area_fin");
        
        InventoryRange.Type rangetype;
        try{ rangetype = InventoryRange.Type.valueOf(section.getString("area_type")); }
        catch(Exception e){ throw new ProvoFormatException("Invalid " + section.getCurrentPath() + ".area_type"); }
        
        InventoryRange range = new InventoryRange(area_ini, area_fin, rangetype);
        return new PreferencesRule(section.getInt("priority", 1), range, section.getString("type", "any"));
    }
    public Map<String, Object> PreferencesRuleToYaml(PreferencesRule pref)
    {
        Map<String, Object> ret = new HashMap<>();
        ret.put("area_ini", pref.getTargetArea().getStart().toString());
        ret.put("area_fin", pref.getTargetArea().getEnd().toString());
        ret.put("area_type", pref.getTargetArea().getType().toString());
        if(pref.getPriority() != 1) ret.put("priority", pref.getPriority());
        ret.put("type", pref.getType());
        return ret;
    }
    
    public Set<String> FetchPlayerPreferencesClasses(String uuid) throws Exception
    {
        return LoadPlayerClassesYaml(uuid).get().getKeys(false);
    }    
    public PreferencesClass FetchPlayerPreferencesClass(String uuid, String name) throws ProvoFormatException, Exception
    {
        YamlFile y = LoadPlayerClassesYaml(uuid);        
        if(y.get().getConfigurationSection(name) == null) return null;
                
        InventoryType inventorytype;
        try{ inventorytype = InventoryType.valueOf(y.get().getString(name + ".type", null)); }
        catch(Exception e)
        {
            ProvoFormatException ex = new ProvoFormatException("Invalid class " + name + ".type");
            ex.setFilePath(y.getFile().getPath());
            throw ex;
        }
        
        // Sectionalize and parse preferences rules
        List<PreferencesRule> rules = new LinkedList<>();
        try{ for(ConfigurationSection i : y.get().SectionalizeMapList(name + ".rules")){ rules.add(PreferencesRuleFromYaml(i, inventorytype)); } }
        catch(ProvoFormatException e){ e.setFilePath(y.getFile().getPath()); throw e; }
        
        y.get().DesectionalizeMapList(name + ".rules");
        
        // Inheritance
        String inheritee = y.get().getString(name + ".inherits");
        PreferencesClass inheriteeclass = null;
        if(inheritee != null)
        {
            inheriteeclass = FetchPlayerPreferencesClass(uuid, inheritee);
            if(inheriteeclass == null)
            {
                Utils.Warning("Couldn't find inherited class " + inheritee + " in file " + y.getFile().getPath());
                Utils.Warning("Deleting inheritance and continuing...");
                y.get().set(name + ".inherits", null);
                y.SaveFile();
            }
            else
            {          
                int highest = 0;
                // Calculate highest priority, then subtract that from all rules to make inherited priorities negative
                for(PreferencesRule rule : inheriteeclass.getRules()){ if(rule.getPriority() > highest) highest = rule.getPriority(); }
                for(PreferencesRule rule : inheriteeclass.getRules())
                {
                    // Generate the new rule and add it to the rules
                    PreferencesRule newrule = new PreferencesRule(rule.getPriority(), rule.getTargetArea(), rule.getType());
                    rule.setPriority(rule.getPriority() - highest - 1);
                    rule.setInherited(true);
                    rule.setInheritedFrom(inheritee);
                    rules.add(rule);
                }
            }
        }
        
        PreferencesClass ret = new PreferencesClass(name, inventorytype, rules);
        if(inheriteeclass != null) ret.setInheritee(inheritee);
        return ret;
    }
    public void WritePreferencesClass(String uuid, PreferencesClass pclass) throws Exception
    {
        YamlFile y = LoadPlayerClassesYaml(uuid);        
        if(y.get().getConfigurationSection(pclass.getName()) != null) return;//if already exists
        
        ConfigurationSection newsection = y.get().createSection(pclass.getName());
        newsection.set("type", pclass.getTargetType().toString());        
        if(pclass.getInheritee() != null) newsection.set("inherits", pclass.getInheritee());
        
        List<Map<String, Object>> yaml_rulelist = new LinkedList<>();
        for(PreferencesRule rule : pclass.getRules()){ yaml_rulelist.add(PreferencesRuleToYaml(rule)); }
        newsection.set("rules", yaml_rulelist);
        
        y.SaveFile();
    }
    public void DeletePreferencesClass(String uuid, String name) throws Exception
    {
        YamlFile y = LoadPlayerClassesYaml(uuid);
        y.get().set(name, null);
        y.SaveFile();
    }
    
    public void WritePreferencesRule(String uuid, String classname, PreferencesRule rule) throws Exception
    {
        YamlFile y = LoadPlayerClassesYaml(uuid);
        if(y.get().getConfigurationSection(classname) == null)
        {
            ProvoNotFoundException e = new ProvoNotFoundException("class " + classname);
            e.FilePath = y.getFile().getPath();
            throw e;
        }
        
        List<Map<?,?>> maplist = y.get().getMapList(classname + ".rules");
        maplist.add(PreferencesRuleToYaml(rule));
        y.get().set(classname + ".rules", maplist);
        
        y.SaveFile();
    }
    /**
     * Delete a preferencesrule by its numeric index
     * @param uuid UUID of the owner of the rule
     * @param classname Class the rule is in
     * @param ruleindex Index of the rule
     * @return True if successful, false if no such rule was found
     * @throws ProvoNotFoundException If the class is not found
     * @throws Exception Miscellaneous error
     */
    public boolean DeletePreferencesRule(String uuid, String classname, int ruleindex) throws Exception
    {
        YamlFile y = LoadPlayerClassesYaml(uuid);  
        if(y.get().getConfigurationSection(classname) == null)
        {
            ProvoNotFoundException e = new ProvoNotFoundException("class " + classname);
            e.FilePath = y.getFile().getPath();
            throw e;
        }
        
        final String RULES = classname + ".rules";
        y.get().SectionalizeMapList(RULES);
        ConfigurationSection rules = y.get().getConfigurationSection(RULES);
        if(rules.getConfigurationSection(Integer.toString(ruleindex)) == null) return false;
        rules.set(Integer.toString(ruleindex), null);
        y.get().DesectionalizeMapList(RULES);
        
        y.SaveFile();
        return true;
    }
}
