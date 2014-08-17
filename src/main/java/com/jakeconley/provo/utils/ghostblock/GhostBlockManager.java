package com.jakeconley.provo.utils.ghostblock;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GhostBlockManager
{
    private final Map<String, List<GhostBlock>> Collection = new HashMap<>();
    
    public Map<String, List<GhostBlock>> getCollection(){ return Collection; }
    
    public List<GhostBlock> getGhostBlocks(String id){ return Collection.get(id); }
    public List<GhostBlock> addGhostBlocks(String id, List<GhostBlock> existing){ return Collection.put(id, existing); }
    public List<GhostBlock> addGhostBlocks(String id){ return Collection.put(id, new LinkedList<GhostBlock>()); }
    
    public void RemoveAll()
    {
	for(Map.Entry<String, List<GhostBlock>> entry : Collection.entrySet())
	{
	    for(GhostBlock block : entry.getValue()) block.Disable();
	}
	
	Collection.clear();
    }
    
    public void DisableAllForId(String id)
    {
	for(GhostBlock block : Collection.get(id)) block.Disable();
    }
    public void RemoveAllForId(String id)
    {
	DisableAllForId(id);
	Collection.remove(id);
    }
}