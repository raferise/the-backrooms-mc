package com.infinitymeme.thebackrooms;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.jitse.npclib.NPCLib;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.skin.Skin;

public class Main extends JavaPlugin implements Listener {
	
	public static final int START_SIZE = 16;
	
	private LinkedList<Player> isfalling;
	private LinkedList<Player> portalopen;
	Player iswatched;
	
	private NPCLib library;
	private Skin hskin;
	
	private boolean dosetup = false;
	private File configfile;
	private FileConfiguration bukkityml;
	
	@Override
	public void onEnable() {
		configfile = new File(getServer().getWorldContainer(), "bukkit.yml");
		bukkityml = YamlConfiguration.loadConfiguration(configfile);
		Object gen = bukkityml.get("worlds.world_nether.generator"); 
		if ((gen == null)||(!(gen.toString().equals("TheBackrooms")))) {
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
				System.out.println(ChatColor.RED+"SETUP ERROR: Nether generation not using THE BACKROOMS. Please type "+ChatColor.DARK_PURPLE+"brsetup"+ChatColor.RED+" in the console.");
				System.out.println(ChatColor.RED+"***WARNING, this will delete the current Nether FOREVER***");
			}},1);
			return;
		}
		Bukkit.getPluginManager().registerEvents(this, this);
		isfalling = new LinkedList<Player>();
		portalopen = new LinkedList<Player>();
		iswatched = null;
		library = new NPCLib(this);
		hskin = new Skin("eyJ0aW1lc3RhbXAiOjE1NTk3Njc1ODYyNzYsInByb2ZpbGVJZCI6ImY4NGM2YTc5MGE0ZTQ1ZTA4NzliY2Q0OWViZDRjNGUyIiwicHJvZmlsZU5hbWUiOiJIZXJvYnJpbmUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2M0YmJkNTA1MjI3YjJhMmRkYzFhYTgwM2RhMzI3ZDQ3ODRlMDA0YzRiNDA4N2VmODhiYTMxMDJkMmUyNGI1MDQifX19","whgWwWpVuo6XR/uluEZyp23pAaG2mMVQIDXygwzhLidp7wGEYJRK7LyPGSI91t9SwnCHBptifPCJPrn9KzqSWNq8mqOgfWEHxqw02KFGbmlOMr1KsPmawCFtdJZ2bgVcvgysyUYtZNCDSb6at/cXBac7IQaD3UM7pOdSsWy0P5REbel9FVPH7eEzGw4njX7BNcF2JIG6qiAwCGRSJCwghQQBpXQSxMkQ7QeTl/fT+EoRCgysdX6wre3xFaNmQsPq1JRZn7mqBykSFX/2ah6jN8Kfa7gBmDOoirpH0evRTZsLam5eUlRDyZEt3uUVrXz+4izfyfVX6m7k29eM0o+ULsF02oVS1efDp5zK2Flp54IyuXi6JbnTQPnVUQVDr4OoQIV6Fyo7zI8SdmjjlLZ5olD9C5Kn2IgkxafNpn5MRpgY5szOSNGPBqqZWf/MHeyOf6YOix6+lUojQAycnqymUe6XSYqUKlHpNHnu2EH5Ooxfo4J+1y8f58vha+FqCqgaprymgzHuoSUDLQkNpr4bMvP6SPBcgY4JU0qxV7a5oyXe2W6OPM0EtPgqugKnl+jmUauJ7RGdFKdTu4Njzkf2YEE1REjczD1q2NYLD6VxVbN5bwHpGa0guJuPs3KK71nf/BW+PIZ0RXxV6Ga6A0GnnW7ERcrK+CVqdv1j0mdZbH0=");
	}
	
	@Override
	public void onDisable() {
		if (dosetup) {
			System.out.println(ChatColor.YELLOW+"SETUP: Setting up THE BACKROOMS.");
			System.out.println(ChatColor.YELLOW+"SETUP: Deleting the nether...");
			World wo = Bukkit.getWorld("world");
			World wn = Bukkit.getWorld("world_nether");
			File nether = wn.getWorldFolder();
			File netherold = new File(wo.getWorldFolder(),"/DIM-1");
			Bukkit.getServer().unloadWorld(wn, false);
			if ((!deleteWorld(netherold))||(!(deleteWorld(nether)))) {
				System.out.println(ChatColor.RED+"ERROR: Couldn't delete the nether. Please do it manually.");
			} else {
				System.out.println(ChatColor.GREEN+"SUCCESS: Deleted the nether.");
			}
			bukkityml.set("worlds.world_nether.generator","TheBackrooms");
			System.out.println(ChatColor.YELLOW+"SETUP: Modifying bukkit.yml...");
			try {
				bukkityml.save(configfile);
				System.out.println(ChatColor.GREEN+"SUCCESS: Set generator of world_nether to TheBackrooms.");
			} catch (IOException e) {
				System.out.println(ChatColor.RED+"ERROR: Could not save bukkit.yml. Please manually add the following configuration to it:");
				System.out.println(ChatColor.AQUA+"worlds:");
				System.out.println(ChatColor.AQUA+"  world_nether:");
				System.out.println(ChatColor.AQUA+"    generator: TheBackrooms");
			}			
			System.out.println(ChatColor.DARK_GREEN+"SETUP: Setup finished. Please restart the server.");
		}
	}
	
	public boolean deleteWorld(File path) {
	      if(path.exists()) {
	          File files[] = path.listFiles();
	          for(int i=0; i<files.length; i++) {
	              if(files[i].isDirectory()) {
	                  deleteWorld(files[i]);
	              } else {
	                  files[i].delete();
	              }
	          }
	      }
	      return(path.delete());
	}

	
	
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
	    return new BackroomsChunkGen();
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		if (p.getWorld().getEnvironment().equals(Environment.NETHER)) {
			undoBlockbreak(p, b.getLocation(), b.getBlockData(), false);
			e.setDropItems(false);
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if (p.getWorld().getEnvironment().equals(Environment.NETHER)) {
			undoBlockbreak(p, e.getBlock().getLocation(), Material.AIR.createBlockData(), true);
		}
	}
	
	@EventHandler
	public void onDeath(EntityDamageEvent e) {
		if ((e.getEntityType().equals(EntityType.PLAYER))&&(e.getEntity().getWorld().getEnvironment().equals(Environment.NETHER))) {
			Player p = (Player)(e.getEntity());
			if (p.getHealth()-e.getFinalDamage() <= 0) {
				p.playEffect(EntityEffect.HURT);
				e.setCancelled(true);
				p.setHealth(0.1);
			}
		}
	}
	
	@EventHandler 
	public void onStep(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SPECTATOR)) {
			if (p.getWorld().getEnvironment().equals(Environment.NETHER)) {
				
				Location l = p.getLocation();
				l.setY(220);
				double ypos = p.getLocation().getY();
				if ((!isfalling.contains(p)&&(ypos < 220))) {
					isfalling.add(p);
					fallSentinel(p,p.getLocation());
				} else if ((ypos>225)&&(p.isOnGround())) {
					Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
						p.setVelocity(new Vector(0,-10,0));
					}},1);
					for (int x=-3; x<=3; x++) {
						for (int y=225; y<=227; y++) {
							for (int z=-3; z<=3; z++) {
								Location ll = p.getLocation();
								ll.setY(y);
								ll.add(x,0,z);
								Block bb = ll.getWorld().getBlockAt(ll);
								undoBlockbreak(p, ll, bb.getBlockData(), false);
								bb.setType(Material.AIR);
							}
						}
					}
				} else if (ypos<225){
					genLights(l,16,16);
				}
				
				if ((int)(Math.random()*250)==0) p.playSound(p.getLocation().add(Vector.getRandom().multiply(10).setY(220)), Sound.AMBIENT_CAVE, 100, 0);
				if (iswatched==null) { 
					Location predicted = p.getLocation().add(p.getLocation().getDirection().multiply(5));
					Location predicted2 = predicted.clone().add(p.getLocation().getDirection().multiply(0.5));
					Location predicted3 = predicted.clone().subtract(p.getLocation().getDirection());
					predicted.setY(220);
					predicted2.setY(220);
					Location spawnloc = predicted.clone();
					Location spawnloceye;
					int attempts = 0;
					do {
						if (attempts == 20) return;
						spawnloc.add((int)(Math.random()*4)-2,0,(int)(Math.random()*4)-2);
						spawnloceye = spawnloc.clone().add(0,1.62,0);
						attempts++;
					//conditions:
						//must not be closer than 5 blocks to predicted loc
						//must not be closer than 10 blocks to player loc
						//must not be filled
						//must be visible from predicted loc
						//must not be visible from player loc
						//must not be visible from 1 block after or before predicted
					} while ((spawnloc.distance(predicted)<5)||(!spawnloc.getWorld().getBlockAt(spawnloc).getType().equals(Material.AIR))||(!lineOfSight(predicted,spawnloceye)||(lineOfSight(predicted2,spawnloceye))||(lineOfSight(predicted3,spawnloceye))||(lineOfSight(p.getEyeLocation(),spawnloceye))));
					if ((int)(Math.random()*10)==0) {
						spawnloc.setDirection(homingvector(spawnloceye, predicted.add(0,1.62,0)));
						NPC herobrine = library.createNPC(hskin);
						herobrine.create(spawnloc);
						herobrine.show(p,false);
						HEWATCHES(p,herobrine,0);
						iswatched = p;
					}
					
				}
			} else if ((p.getWorld().getEnvironment().equals(Environment.NORMAL)&&(!portalopen.contains(p)))) {
				int zz=0;
				int xx=0;
				if ((p.getLocation().getX()-p.getLocation().getBlockX() >= 0.65)&&(p.getFacing().equals(BlockFace.WEST))) {
					xx=1;
				} else if ((p.getLocation().getX()-p.getLocation().getBlockX() <= 0.35)&&(p.getFacing().equals(BlockFace.EAST))) {
					xx=-1;
				} else if ((p.getLocation().getZ()-p.getLocation().getBlockZ() <= 0.35)&&(p.getFacing().equals(BlockFace.SOUTH))) {
					zz=-1;
				} else if ((p.getLocation().getZ()-p.getLocation().getBlockZ() >= 0.65)&&(p.getFacing().equals(BlockFace.NORTH))) {
					zz=1;
				}
				if (xx!=0) {
					for (int x=1; x<=2; x++) {
						for (int y=0; y<3; y++) {
							for (int z=-Math.abs(xx); z<=Math.abs(xx); z++) {
								Location l = new Location(p.getWorld(), p.getLocation().getBlockX()+(xx*x),p.getLocation().getBlockY()+y,p.getLocation().getBlockZ()+z);
								Block b = p.getWorld().getBlockAt(l);
								if (b.getType().equals(Material.AIR)||b.getType().equals(Material.CAVE_AIR)||b.getType().equals(Material.WATER)||b.getType().equals(Material.LAVA)) return;
							}
						}
					}
					Location room = new Location(p.getWorld(), p.getLocation().getBlockX()+(xx*(START_SIZE+2)),p.getLocation().getBlockY(),p.getLocation().getBlockZ());
					generateRoom(room, START_SIZE, START_SIZE);
					LinkedList<Location> locs = new LinkedList<Location>();
					LinkedList<BlockData> fill = new LinkedList<BlockData>();
					for (int y=0; y<3; y++) {
						for (int z=-Math.abs(xx); z<=Math.abs(xx); z++) {
							for (int x=-2; x<3; x++) {
								Location l = new Location(p.getWorld(), p.getLocation().getBlockX()+xx+x,p.getLocation().getBlockY()+y,p.getLocation().getBlockZ()+z);
								Block b = p.getWorld().getBlockAt(l);
								BlockData bd = b.getBlockData().clone();
								locs.add(l);
								fill.add(bd);
								b.setType(Material.AIR);
								if (!bd.getMaterial().equals(Material.AIR)) {
									FallingBlock fb = p.getWorld().spawnFallingBlock(l.add(0.5,0,0.5), bd);
									fb.setDropItem(false);
									fb.setGravity(false);
								}
							}
						}
					}
					Location ppp = p.getLocation();
					ppp.setPitch(0);
					portalopen.add(p);
					cleanupEntrance(p, 
							p.getEyeLocation().add(ppp.getDirection()),
							room,
							locs, fill);
				} else if (zz!=0) {
					for (int z=1; z<=2; z++) {
						for (int y=0; y<3; y++) {
							for (int x=-1; x<=1; x++) {
								Location l = new Location(p.getWorld(), p.getLocation().getBlockX()+x,p.getLocation().getBlockY()+y,p.getLocation().getBlockZ()+(zz*z));
								Block b = p.getWorld().getBlockAt(l);
								if (b.getType().equals(Material.AIR)||b.getType().equals(Material.CAVE_AIR)||b.getType().equals(Material.WATER)||b.getType().equals(Material.LAVA)) return;
							}
						}
					}
					Location room = new Location(p.getWorld(), p.getLocation().getBlockX(),p.getLocation().getBlockY(),p.getLocation().getBlockZ()+(zz*(START_SIZE+2)));
					generateRoom(room, START_SIZE, START_SIZE);
					LinkedList<Location> locs = new LinkedList<Location>();
					LinkedList<BlockData> fill = new LinkedList<BlockData>();
					for (int y=0; y<3; y++) {
						for (int x=-1; x<=1; x++) {
							for (int z=-2; z<3; z++) {
								Location l = new Location(p.getWorld(), p.getLocation().getBlockX()+x,p.getLocation().getBlockY()+y,p.getLocation().getBlockZ()+zz+z);
								Block b = p.getWorld().getBlockAt(l);
								BlockData bd = b.getBlockData().clone();
								locs.add(l);
								fill.add(bd);
								b.setType(Material.AIR);
								if (!bd.getMaterial().equals(Material.AIR)) {
									FallingBlock fb = p.getWorld().spawnFallingBlock(l.add(0.5,0,0.5), bd);
									fb.setDropItem(false);
									fb.setVelocity(new Vector(0,0,0));
									fb.setGravity(false);
								}
							}
						}
					}
					Location ppp = p.getLocation();
					ppp.setPitch(0);
					portalopen.add(p);
					cleanupEntrance(p, 
							p.getEyeLocation().add(ppp.getDirection()),
							room,
							locs, fill);
				}
			}
		}
	}
	
	public void cleanupEntrance(Player p, Location nosee, Location near, LinkedList<Location> locs, LinkedList<BlockData> fill) {
		if (p.getLocation().distance(near) < START_SIZE-1) {
			p.playSound(p.getLocation(), Sound.BLOCK_PORTAL_TRIGGER, 100, 0);
			World w = Bukkit.getWorld("world_nether");
			World wo = Bukkit.getWorld("world");
			w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
			w.setGameRule(GameRule.DO_FIRE_TICK, false);
			for (int x=-4; x<=4; x++) {
				for (int z=-4; z<=4; z++) {
					w.loadChunk(x, z, true);
				}
			}
			for (int i=0; i<locs.size(); i++) {
				wo.getBlockAt(locs.get(i)).setBlockData(fill.get(i));
			}
			
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1, false, false, false),true);
				Location tpto = new Location(w, p.getLocation().getX()-near.getX(), 220, p.getLocation().getZ()-near.getZ());
				tpto.setPitch(p.getLocation().getPitch());
				tpto.setYaw(p.getLocation().getYaw());
				p.teleport(tpto);
				p.setGameMode(GameMode.SURVIVAL);
				p.playSound(p.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 100, 0);
				portalopen.remove(p);
			}},150);
			
		} else if (p.getLocation().distance(near)<START_SIZE+5) {
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
				cleanupEntrance(p,nosee,near,locs,fill);
			}},1);
		} else {
			portalopen.remove(p);
		}
	}
	
	public void undoBlockbreak(Player p, Location l, BlockData bd, boolean useair) {
		if (!lookingAt(p, l, -0.342)) {
			if ((useair)||(!bd.getMaterial().equals(Material.AIR))) {
				Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
					l.getWorld().getBlockAt(l).setBlockData(bd);
				}},10);
			}
		} else {
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
				undoBlockbreak(p,l,bd,useair);
			}},1);
		}
	}
	
	public void fallSentinel(Player p, Location l) {
		if (p.getLocation().getY() < 200) {
			Vector v = p.getVelocity();
			Location to = p.getLocation();
			to.setY(600);
			p.teleport(to);
			p.setVelocity(v);
			isfalling.remove(p);
		} else {
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
				fallSentinel(p,l);
			}},1);
		}
	}
	
	public void HEWATCHES(Player p, NPC herobrine, int ticks) {
		Location hh = herobrine.getLocation();
		Location he = herobrine.getLocation().clone().add(0,1.62,0);
		if (lineOfSight(p.getEyeLocation(), he)) {
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
				if (lineOfSight(p.getEyeLocation(), he)&&(lookingAt(p, he, 0.4))) {
					p.playSound(he, Sound.ENTITY_ENDERMAN_STARE, 100, 0.6f);
					p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 500, 1, false, false, false), true);
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 500, 5, false, false, false), true);
					EYECONTACT(p, herobrine, 0);
				} else {
					herobrine.destroy();
					iswatched = null;
				}
				
			}},15);
		} else if ((ticks<400)&&(hh.distance(p.getLocation()) < 40)) {
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
				HEWATCHES(p,herobrine, ticks+1);
			}},1);
		} else {
			iswatched = null;
			herobrine.destroy();
		}
	}
	
	public void EYECONTACT(Player p, NPC herobrine, int ticks) {
		Location he = herobrine.getLocation().clone().add(0,1.62,0);
		if (lineOfSight(p.getEyeLocation(), he)&&(lookingAt(p, he, 0.4))) {
			if ((ticks > 160)||(p.getLocation().distance(herobrine.getLocation()) < 3)) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1, false, false, false),true);
				p.stopSound(Sound.ENTITY_ENDERMAN_STARE);
				p.removePotionEffect(PotionEffectType.CONFUSION);
				p.removePotionEffect(PotionEffectType.SLOW);
				iswatched = null;
				herobrine.destroy();
				p.playEffect(EntityEffect.HURT);
			} else {
				Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
					EYECONTACT(p, herobrine, ticks+1);
				}},1);
				p.spawnParticle(Particle.TOWN_AURA, he, 50, 3, 3, 3);
			}
		} else {
			p.stopSound(Sound.ENTITY_ENDERMAN_STARE);
			p.removePotionEffect(PotionEffectType.CONFUSION);
			p.removePotionEffect(PotionEffectType.SLOW);
			iswatched = null;
			herobrine.destroy();
		}
	}
	
	public Vector homingvector(Location l1, Location l2) {
		Vector v = l2.toVector().subtract(l1.toVector());
		v = v.multiply(1/(l1.toVector().distance(l2.toVector())));
		return v;
	}
	
	public boolean lineOfSight(Location pl, Location l) {
		pl = pl.clone();
		pl.setDirection(homingvector(pl, l));
		while (pl.distance(l) > 0.1) {
			if (pl.getWorld().getBlockAt(pl).getType().equals(Material.AIR)) pl.add(pl.getDirection().multiply(0.05));
			else return false;
		}
		return true;
	}
	
	public void genLights(Location l, int xwid, int zwid) {
		for (int x=-xwid; x<=xwid; x++) {
			for (int z=-zwid; z<=zwid; z++) {
				if ((Math.abs(l.getBlockX()+x)%8<3)&&(Math.abs(l.getBlockZ()+z)%4==0)) {
					l.getWorld().getBlockAt(l.clone().add(x,6,z)).setType(Material.SEA_LANTERN);
				}
			}
		}
	}
	
	public void generateRoom(Location l, int xwid, int zwid) {
		World w = Bukkit.getWorld("world_nether");
		for (int x=-4; x<=4; x++) {
			for (int z=-4; z<=4; z++) {
				w.loadChunk(x, z, true);
			}
		}
		for (int x=-xwid; x<=xwid; x++) {
			for (int z=-zwid; z<=zwid; z++) {
				l.getWorld().getBlockAt(l.clone().add(x,-1,z)).setType(Material.STRIPPED_BIRCH_WOOD);
				if ((Math.abs(l.getBlockX()+x)%8<3)&&(Math.abs(l.getBlockZ()+z)%4==0)) {
					l.getWorld().getBlockAt(l.clone().add(x,5,z)).setType(Material.GLASS);
					l.getWorld().getBlockAt(l.clone().add(x,6,z)).setType(Material.SEA_LANTERN);
				} else {
					l.getWorld().getBlockAt(l.clone().add(x,5,z)).setType(Material.SMOOTH_SANDSTONE);
				}
			}
		}
		for (int x=-xwid; x<=xwid; x++) {
			for (int z=-zwid; z<=zwid; z++) {
				if ((Math.abs(x) == xwid)||(Math.abs(z) == zwid)) {
					genwall(l.clone().add(x,0,z),6);
				}
			}
		}
		for (int x=-xwid+1; x<=xwid-1; x++) {
			for (int z=-zwid+1; z<=zwid-1; z++) {
				for (int y=0; y<5; y++) {
					Location to = l.clone().add(x,y,z);
					l.getWorld().getBlockAt(to).setBlockData(w.getBlockAt(x,220+y,z).getBlockData());
				}
			}
		}
		
	}

	
	public void genwall(Location l, int height) {
		l.getWorld().getBlockAt(l).setType(Material.CUT_SANDSTONE);
		for (int y=1; y<height; y++) {
			l.getWorld().getBlockAt(l.add(0,1,0)).setType(Material.SMOOTH_SANDSTONE);
		}
	}
	
	private boolean lookingAt(Player player, Location l, double tolerance)
	  {
	    Location eye = player.getEyeLocation();
	    Vector toLoc = l.toVector().subtract(eye.toVector());
	    double dot = toLoc.normalize().dot(eye.getDirection());
	   
	    return (dot > tolerance);
	  }
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals("brsetup")) {
			if (!(sender instanceof Player)) {
				dosetup = true;
				Bukkit.shutdown();
				return true;
			} else {
				sender.sendMessage(ChatColor.RED+"This command can only be used through the console.");
				return true;
			}
		}
		return false;
	}
}
