package com.dcman58.spawner;

import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

public class MonsterBoxBlockListener implements org.bukkit.event.Listener {
	MonsterBox plugin;
	public ConcurrentHashMap<Integer, String> intmobs = new ConcurrentHashMap();
	public ConcurrentHashMap<String, Integer> stringmobs = new ConcurrentHashMap();

	public MonsterBoxBlockListener(MonsterBox plugin) {
		this.plugin = plugin;
		CreatureTypes[] mobs = CreatureTypes.values();
		CreatureTypes[] arrayOfCreatureTypes1;
		int j = (arrayOfCreatureTypes1 = mobs).length;
		for (int i = 0; i < j; i++) {
			CreatureTypes mob = arrayOfCreatureTypes1[i];
			this.intmobs.put(new Integer(mob.id), mob.toString());
			this.stringmobs.put(mob.toString(), new Integer(mob.id));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		event.getPlayer().getItemInHand();
		if ((!event.isCancelled()) && (event.getBlock().getType() == Material.MOB_SPAWNER)) {
			ItemStack is = event.getPlayer().getItemInHand();

			boolean nodrops = false;
			if (this.plugin.disabledspawnerlocs.containsKey(this.plugin.locationBuilder(event.getBlock().getLocation()))) {
				this.plugin.removeDisabledSpawner(event.getBlock());
				nodrops = true;
			}
			if ((this.plugin.needssilktouch) && (!itemHasSilkTouch(is))) {
				return;
			}
			try {
				CreatureSpawner theSpawner = (CreatureSpawner) event.getBlock().getState();
				String monster = (String) this.intmobs.get(new Integer(theSpawner.getSpawnedType().getTypeId()));
				if ((this.plugin.hasPermissions(event.getPlayer(), "monsterbox.drops")) || (this.plugin.hasPermissions(event.getPlayer(), "monsterbox.dropegg"))) {
					if (nodrops) {
						event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You just broke an " + ChatColor.RED + "unset" + ChatColor.DARK_GREEN + " spawner.");
					} else {
						event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You just broke a " + ChatColor.RED + monster.toLowerCase() + ChatColor.DARK_GREEN + " spawner.");
					}
				}

				boolean mcmmofix = false;
				if (this.plugin.hasPermissions(event.getPlayer(), "monsterbox.drops")) {
					ItemStack mobstack = new ItemStack(Material.MOB_SPAWNER, 1);
					event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), mobstack);
					mcmmofix = true;
				}
				if ((!nodrops) && (this.stringmobs.containsKey(monster)) && (this.plugin.hasPermissions(event.getPlayer(), "monsterbox.dropegg." + monster.toLowerCase()))) {
					ItemStack eggstack = new ItemStack(383, 1, theSpawner.getSpawnedType().getTypeId());
					event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), eggstack);
					mcmmofix = true;
				}

				if (mcmmofix) {
					event.getBlock().setType(Material.AIR);
					event.setExpToDrop(0);
				}
			} catch (Exception localException) {
			}
		}
	}

	private boolean itemHasSilkTouch(ItemStack is) {
		if ((is != null) && (is.containsEnchantment(org.bukkit.enchantments.Enchantment.SILK_TOUCH))) {
			return true;
		}
		return false;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		if ((!event.isCancelled()) && (event.getBlockPlaced().getType() == Material.MOB_SPAWNER)) {
			if (this.plugin.hasPermissions(event.getPlayer(), "monsterbox.place")) {
				this.plugin.addDisabledSpawner(event.getBlockPlaced());

			} else {

				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.DARK_RED + "You don't have permission to place a monster spawner.");
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void explosion(EntityExplodeEvent event) {
		if (event.isCancelled()) {
			return;
		}
		for (Block block : event.blockList()) {
			if ((block.getType() == Material.MOB_SPAWNER) && (this.plugin.disabledspawnerlocs.containsKey(this.plugin.locationBuilder(block.getLocation())))) {
				this.plugin.removeDisabledSpawner(block);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void mobSpawn(CreatureSpawnEvent event) {
		if ((!event.isCancelled()) && (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) && (!this.plugin.canSpawnMob(event.getLocation(), event.getEntityType()))) {
			event.setCancelled(true);
		}
	}
}