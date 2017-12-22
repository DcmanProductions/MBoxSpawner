package com.dcman58.spawner;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomRecipe {
	
	public CustomRecipe(){
		ItemStack mobSpawner = new ItemStack(Material.MOB_SPAWNER,1);
		ItemMeta meta = mobSpawner.getItemMeta();
		
		meta.setDisplayName("DcCraft Mob Box");
		meta.setLore(Arrays.asList("Use ", "/mbox get","/mbox set <name>"));
		mobSpawner.setItemMeta(meta);
		ShapedRecipe msRecipe = new ShapedRecipe(mobSpawner);
		msRecipe.shape(
				"OOO",
				"ODO",
				"OOO");
		msRecipe.setIngredient('O', Material.OBSIDIAN);
		msRecipe.setIngredient('D', Material.DIAMOND);
		Bukkit.getServer().addRecipe(msRecipe);
	}
	
}
