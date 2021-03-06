package com.dcman58.spawner;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MonsterBoxCommands
  implements CommandExecutor
{
  MonsterBox plugin;
  
  public MonsterBoxCommands(MonsterBox plugin)
  {
    this.plugin = plugin;
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
  {
    if ((sender instanceof Player))
    {
      Player player = (Player)sender;
      Block targetblock = player.getTargetBlock(this.plugin.transparentBlocks, 40);
      if (commandLabel.equalsIgnoreCase("mbox")) {
        if (args.length > 1)
        {
          if ((args[0].trim().equalsIgnoreCase("set")) && (targetblock.getType() == Material.MOB_SPAWNER))
          {
            if (this.plugin.hasPermissions(player, "monsterbox.set"))
            {
              if (this.plugin.hasPermissions(player, "monsterbox.spawn." + args[1].toLowerCase()))
              {
                if ((this.plugin.useiconomy) && (this.plugin.hasEconomy()))
                {
                  if (this.plugin.hasPermissions(player, "monsterbox.free"))
                  {
                    if (this.plugin.setSpawner(targetblock, args[1]))
                    {
                      player.sendMessage(ChatColor.DARK_GREEN + "Poof! That mob spawner is now a " + args[1].toLowerCase() + " spawner.");
                      return true;
                    }
                    player.sendMessage(ChatColor.RED + "Invalid mob type.");
                  }
                  else if (this.plugin.iConomy.hasAccount(player.getName()))
                  {
                    double balance = this.plugin.iConomy.getBalance(player.getName());
                    if (balance >= this.plugin.getMobPrice(args[1]))
                    {
                      if (this.plugin.setSpawner(targetblock, args[1]))
                      {
                        this.plugin.iConomy.withdrawPlayer(player.getName(), this.plugin.getMobPrice(args[1]));
                        player.sendMessage(ChatColor.DARK_GREEN + "Poof! That mob spawner is now a " + args[1].toLowerCase() + " spawner.");
                        return true;
                      }
                      player.sendMessage(ChatColor.RED + "Invalid mob type.");
                    }
                    else
                    {
                      player.sendMessage(ChatColor.RED + "You need " + this.plugin.iConomy.format(this.plugin.getMobPrice(args[1])) + " to set the type of monster spawner!");
                    }
                  }
                  else
                  {
                    player.sendMessage(ChatColor.RED + "You need a bank account and " + this.plugin.iConomy.format(this.plugin.getMobPrice(args[1])) + " to set the type of monster spawner!");
                  }
                }
                else
                {
                  if (this.plugin.setSpawner(targetblock, args[1]))
                  {
                    player.sendMessage(ChatColor.DARK_GREEN + "Poof! That mob spawner is now a " + args[1].toLowerCase() + " spawner.");
                    return true;
                  }
                  player.sendMessage(ChatColor.RED + "Invalid mob type.");
                }
              }
              else
              {
                player.sendMessage(ChatColor.RED + "You don't have permission to create a " + args[1].toLowerCase() + " spawner.");
                return true;
              }
            }
            else {
              player.sendMessage(ChatColor.RED + "You don't have permission to change spawner types!");
            }
          }
          else {
            return false;
          }
        }
        else if (args.length == 1)
        {
          if ((args[0].trim().equalsIgnoreCase("set")) && (targetblock.getType() == Material.MOB_SPAWNER))
          {
            if (this.plugin.usespout != null)
            {
              SpoutPlayer splayer = SpoutManager.getPlayer(player);
              if (splayer.isSpoutCraftEnabled())
              {
                splayer.getMainScreen().closePopup();
                CreatureSpawner theSpawner = (CreatureSpawner)targetblock.getState();
                String monster = theSpawner.getSpawnedType().toString().toLowerCase();
                this.plugin.ss.createMonsterGUI("This is currently a " + monster + " spawner.", !this.plugin.hasPermissions(splayer, "monsterbox.free"), splayer);
                return true;
              }
            }
            else
            {
              player.sendMessage(ChatColor.GREEN + "To set the Spawner type: /mbox set <mobname>");
              CreatureTypes[] values = CreatureTypes.values();
              String mobs = "";
              for (int i = 0; i < values.length; i++)
              {
                if (i > 0) {
                  mobs = mobs + ", ";
                }
                mobs = mobs + values[i].toString();
              }
              player.sendMessage(ChatColor.GREEN + "Valid mob types: " + mobs);
              return true;
            }
          }
          else
          {
            if (args[0].equalsIgnoreCase("get"))
            {
              if (this.plugin.hasPermissions(player, "monsterbox.view"))
              {
                if (targetblock.getType() == Material.MOB_SPAWNER) {
                  try
                  {
                    CreatureSpawner theSpawner = (CreatureSpawner)targetblock.getState();
                    String monster = theSpawner.getSpawnedType().toString().toLowerCase();
                    player.sendMessage(ChatColor.GREEN + "That is a " + ChatColor.RED + monster + ChatColor.GREEN + " spawner.");
                    return true;
                  }
                  catch (Exception e)
                  {
                    return false;
                  }
                }
                player.sendMessage(ChatColor.RED + "You must target a MobSpawner first!");
                return true;
              }
              player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
              return true;
            }
            player.sendMessage(ChatColor.GREEN + "To get the Spawner type: /mbox get");
          }
        }
        else
        {
          player.sendMessage(ChatColor.GREEN + "To set the Spawner type: /mbox set <mobname>");
          CreatureTypes[] values = CreatureTypes.values();
          String mobs = "";
          for (int i = 0; i < values.length; i++)
          {
            if (i > 0) {
              mobs = mobs + ", ";
            }
            mobs = mobs + values[i].toString();
          }
          player.sendMessage(ChatColor.GREEN + "Valid mob types: " + mobs);
        }
      }
    }
    return false;
  }
}
