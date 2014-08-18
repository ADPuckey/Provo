package com.jakeconley.provo.utils.ghostblock;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.bukkit.entity.Player;

public class PlayerIterator implements Iterator<GhostBlock>
{
    private final GhostBlockManager gbm;
    private final Player player;
    
    private int currentindex = -1;
    private int nextindex = -1;
    
    public PlayerIterator(GhostBlockManager _gbm, Player _player)
    {
	gbm = _gbm;
	player = _player;
    }
    
    @Override
    public boolean hasNext()
    {
	if(gbm.Collection.isEmpty()) return false;
	
	for(int i = nextindex + 1; i < gbm.Collection.size(); i++)
	{
	    if(gbm.PlayerAccesses(player, gbm.Collection.get(i)))
	    {
		nextindex = i;
		return true;
	    }
	}
	
	
	return false;
    }
    
    @Override
    public GhostBlock next()
    {
	if(gbm.Collection.isEmpty()) throw new NoSuchElementException();
	
	if(nextindex > currentindex)
	{
	    if(nextindex > gbm.Collection.size()) throw new NoSuchElementException();
	    
	    currentindex = nextindex;
	    return gbm.Collection.get(nextindex);
	}
	
	for(int i = currentindex + 1; i < gbm.Collection.size(); i++)
	{
	    GhostBlock gb = gbm.Collection.get(i);
	    if(gbm.PlayerAccesses(player, gb))
	    {
		currentindex = i;
		return gb;
	    }
	}
	
	throw new NoSuchElementException();
    }
    
    @Override
    public void remove()
    {
	if(nextindex != currentindex) throw new IllegalStateException();
	
	if(gbm.Collection.size() >= currentindex) throw new NoSuchElementException();
	gbm.Collection.remove(currentindex);
	
	currentindex--;
    }
}