package com.infinitymeme.thebackrooms;

import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class BackroomsChunkGen extends ChunkGenerator {

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
    	
        ChunkData c = createChunkData(world);
        for (int x=0; x<16; x++) {
			for (int z=0; z<16; z++) {
				c.setBlock(x,219,z, Material.STRIPPED_BIRCH_WOOD);
				if ((Math.abs((chunkX*16)+x)%8<3)&&(Math.abs((chunkZ*16)+z)%4==0)) {
					c.setBlock(x,225,z,Material.GLASS);
//					c.setBlock(x,226,z,Material.SEA_LANTERN);
					c.setBlock(x,227,z,Material.SMOOTH_SANDSTONE);
				} else {
					c.setBlock(x,225,z,Material.SMOOTH_SANDSTONE);
					c.setBlock(x,226,z,Material.SMOOTH_SANDSTONE);
					c.setBlock(x,227,z,Material.SMOOTH_SANDSTONE);
				}
				for (int y=0; y<5; y++) {
					c.setBlock(x,y,z, Material.AIR);
				}
			}
		}
		for (int x=0; x<16; x++) {
			for (int z=0; z<16; z++) {
				if ((Math.abs((chunkX*16)+x)%8 == 5)&&(Math.abs((chunkZ*16)+z)%8 == 2)) {
					int xh = 4+((int)(Math.random()*2));
					int zh = 4+((int)(Math.random()*2));
					int wx, wz;
					for (wx=-1*(2+((int)(Math.random()*3))); wx<(2+((int)(1+Math.random()*3))); wx++) {
						c.setBlock(x+wx, 220, z, Material.CUT_SANDSTONE);
						for (int y=1; y<xh; y++) {
							c.setBlock(x+wx, 220+y, z, Material.SMOOTH_SANDSTONE);
						}
					}
					for (wz=-1*(2+((int)(Math.random()*4))); wz<(2+((int)(Math.random()*4))); wz++) {
						c.setBlock(x, 220, z+wz, Material.CUT_SANDSTONE);
						for (int y=1; y<zh; y++) {
							c.setBlock(x, 220+y, z+wz, Material.SMOOTH_SANDSTONE);
						}
					}
				}
			}
		}
        return c;
    }
}