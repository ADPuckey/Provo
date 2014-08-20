package com.jakeconley.provo.features.monstertest;

import com.jakeconley.provo.utils.MinecraftConstants;
import com.jakeconley.provo.utils.ghostblock.GhostBlock;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class MonsterTester
{
    public final Player Player;
    public Location StartLocation;
    public Location EndLocation;

    public MonsterTester(Player player)
    {
	this.Player = player;
    }
    
    public void Backend()
    {
	World world = StartLocation.getWorld();
	int start_x = StartLocation.getBlockX();
	int start_z = StartLocation.getBlockZ();
	int start_y = StartLocation.getBlockY();
	int end_x   = EndLocation.getBlockX();
	int end_z   = EndLocation.getBlockZ();
	int end_y   = EndLocation.getBlockY();
	
	boolean norm_x =(start_x < end_x); boolean norm_z = (start_z < end_z); boolean norm_y = (start_y > end_y);
	
	int bound_big_x = (norm_x ? start_x : end_x);
	int bound_big_z = (norm_z ? start_z : end_z);
	int bound_big_y = (norm_y ? start_y : end_y);
	int bound_small_x = (norm_x ? end_x : start_x);
	int bound_small_z = (norm_z ? end_z : start_z);
	int bound_small_y = (norm_y ? end_y : start_y);
	
	List<GhostBlock> blocks = new LinkedList<>();
	
	for(int x = bound_small_x; x <= bound_big_x; x++)
	{
	    for(int z = bound_small_z; z <= bound_big_z; z++)
	    {
		// Executed for each XZ pair.
		Block next = world.getBlockAt(x, bound_small_y, z);
		Block cur = null;
		
		Block prev;		
		if(bound_small_y - 1 > 0) prev = world.getBlockAt(x, bound_small_y - 1, z);
		else prev = null;
		
		for(int y = bound_small_y; y <= bound_big_y; y++)
		{
		    cur = next;
		    if(cur == null || cur.getType() == Material.AIR)// This block is air
		    {
			prev = cur;
			continue;
		    }

		    int incr = y + 1;
		    if(incr > MinecraftConstants.MAX_HEIGHT) break;
		    next = world.getBlockAt(x, incr, z);
		    if(next != null && next.getType().isSolid())// Solid block above
		    {
			prev = cur;
			continue;
		    }
		    
		    if(prev != null && prev.getType() == Material.BEDROCK)// Bedrock below
		    {
			prev = cur;
			continue;
		    }
		    
		    if(cur.getLightLevel() <= MinecraftConstants.MONSTERSPAWN_MAX_LIGHT)
		    {
			GhostBlock block = new GhostBlock(cur);
			block.Enable(Material.IRON_BLOCK);//TODO: Replace with config
			blocks.add(block);
		    }
		    
		    prev = cur;
		    continue;
		}
	    }
	}
    }
}