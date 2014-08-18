package com.jakeconley.provo.utils.ghostblock;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class GhostBlockManager
{
    private final Set<Player> Players = new HashSet<>();
    final List<GhostBlock> Collection = new LinkedList<>();//package-visible for GhostBlockIterator
    
    public void addGhostBlock(GhostBlock gb){ Collection.add(gb); }
    
    public PlayerIterator PlayerIterator(Player p){ return new PlayerIterator(this, p); }
    
    public boolean PlayerHasBlocks(Player p)
    {
	return Players.contains(p);
    }
    public boolean PlayerAccesses(Player p, GhostBlock g)
    {
	return g.getPlayers().contains(p);
    }
    public Block GetByPlayerAndBlock(Player p, Block b)
    {
	for(GhostBlock g : Collection) if(g.getBlock().equals(b)) return b;
	return null;
    }
    
    public void RemoveAll()
    {
	for(GhostBlock block : Collection)block.Disable();	
	Collection.clear();
    }
    
    public void DisableAllForPlayer(Player p)
    {
	for(Iterator<GhostBlock> i = this.PlayerIterator(p); i.hasNext();)
	{
	    GhostBlock gb = i.next();
	    gb.Disable();
	}
    }
    public void RemoveAllForPlayer(Player p)
    {
	for(Iterator<GhostBlock> i = this.PlayerIterator(p); i.hasNext();)
	{
	    GhostBlock gb = i.next();
	    gb.Disable();
	    i.remove();
	}
    }
}