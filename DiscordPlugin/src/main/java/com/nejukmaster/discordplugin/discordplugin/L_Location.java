package com.nejukmaster.discordplugin.discordplugin;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.World;

public class L_Location extends Location{
	
	public L_Location(World world, int x, int y, int z) {
		super(world, x, y, z);
	}
	
	public L_Location (Location l) {
		super(l.getWorld(),l.getBlockX(),l.getBlockY(),l.getBlockZ());
	}
	
	public boolean isBetween(Location s, Location e) {
		if(s == null || e == null) return false;
		
		World w = s.getWorld();
		
		int[][] mat = {{s.getBlockX(),e.getBlockX()},
						{s.getBlockY(),e.getBlockY()},
						{s.getBlockZ(),e.getBlockZ()}};
		for(int i = 0; i < 3; i ++) {
			Arrays.sort(mat[i]);
		}
		
		Location ns = new Location(w,mat[0][0],mat[1][0],mat[2][0]);
		Location ne = new Location(w,mat[0][1],mat[1][1],mat[2][1]);
		
		if(ns.getBlockX() <= this.getBlockX() && ns.getBlockY() <= this.getBlockY()
		   && ns.getBlockZ() <= this.getBlockZ() && this.getBlockX() <= ne.getBlockX()
		   && this.getBlockY() <= ne.getBlockY() && this.getBlockZ() <= ne.getBlockZ()) {
			return true;
		}
		else return false;
	}

}
