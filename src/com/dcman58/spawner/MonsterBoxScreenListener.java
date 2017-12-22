/*    */ package com.dcman58.spawner;
/*    */ 
/*    */ import net.milkbowl.vault.economy.Economy;
/*    */ import org.bukkit.Material;
/*    */ import org.bukkit.block.Block;
/*    */ import org.bukkit.event.EventPriority;
/*    */ import org.getspout.spoutapi.event.screen.ButtonClickEvent;
/*    */ import org.getspout.spoutapi.gui.Button;
/*    */ import org.getspout.spoutapi.gui.InGameHUD;
/*    */ import org.getspout.spoutapi.player.SpoutPlayer;
/*    */ 
/*    */ public class MonsterBoxScreenListener implements org.bukkit.event.Listener
/*    */ {
/*    */   MonsterBox plugin;
/*    */   
/*    */   public MonsterBoxScreenListener(MonsterBox plugin)
/*    */   {
/* 18 */     this.plugin = plugin;
/*    */   }
/*    */   
/*    */   @org.bukkit.event.EventHandler(priority=EventPriority.NORMAL)
/*    */   public void onButtonClick(ButtonClickEvent event)
/*    */   {
/* 24 */     if (this.plugin == event.getButton().getPlugin()) {
/* 25 */       Button eventbutton = event.getButton();
/* 26 */       String completebutton = eventbutton.getText();
/* 27 */       String[] buttonsplit = completebutton.split(" ");
/* 28 */       String mobname = buttonsplit[(buttonsplit.length - 1)];
/* 29 */       SpoutPlayer player = event.getPlayer();
/* 30 */       if (mobname.equalsIgnoreCase("close")) {
/* 31 */         player.getMainScreen().closePopup();
/*    */ 
/*    */ 
/*    */       }
/* 35 */       else if (this.plugin.hasPermissions(player, "monsterbox.set"))
/*    */       {
/* 37 */         if (this.plugin.hasPermissions(player, "monsterbox.spawn." + mobname.toLowerCase())) {
/* 38 */           Block targetblock = player.getTargetBlock(this.plugin.transparentBlocks, 40);
/* 39 */           if ((this.plugin.useiconomy) && (this.plugin.hasEconomy())) {
/* 40 */             if (this.plugin.hasPermissions(player, "monsterbox.free")) {
/* 41 */               if (this.plugin.setSpawner(targetblock, mobname)) {
/* 42 */                 player.sendNotification("Mob Spawner changed!", this.plugin.capitalCase(mobname) + "s galore!", Material.NOTE_BLOCK);
/* 43 */                 player.getMainScreen().closePopup();
/*    */               }
/*    */               else {
/* 46 */                 player.sendNotification("Mob Unavailable", "Invalid mob type.", Material.FIRE);
/*    */               }
/* 48 */             } else if (this.plugin.iConomy.hasAccount(player.getName())) {
/* 49 */               double balance = this.plugin.iConomy.getBalance(player.getName());
/* 50 */               if (balance >= this.plugin.getMobPrice(mobname)) {
/* 51 */                 if (this.plugin.setSpawner(targetblock, mobname)) {
/* 52 */                   this.plugin.iConomy.withdrawPlayer(player.getName(), this.plugin.getMobPrice(mobname));
/* 53 */                   player.sendNotification("Mob Spawner changed!", this.plugin.capitalCase(mobname) + "s galore!", Material.NOTE_BLOCK);
/* 54 */                   player.getMainScreen().closePopup();
/*    */                 }
/*    */                 else {
/* 57 */                   player.sendNotification("Mob Unavailable", "Invalid mob type.", Material.FIRE);
/*    */                 }
/*    */               } else {
/* 60 */                 player.sendNotification("Insufficient Funds!", "You need " + this.plugin.iConomy.format(this.plugin.getMobPrice(mobname)) + "!", Material.NOTE_BLOCK);
/*    */               }
/*    */             } else {
/* 63 */               player.sendNotification("No Bank account!", 
/* 64 */                 "You need a bank account and " + this.plugin.iConomy.format(this.plugin.getMobPrice(mobname)) + "!", Material.NOTE_BLOCK);
/*    */             }
/*    */           }
/* 67 */           else if (this.plugin.setSpawner(targetblock, mobname)) {
/* 68 */             player.sendNotification("Mob Spawner changed!", this.plugin.capitalCase(mobname) + "s galore!", Material.NOTE_BLOCK);
/* 69 */             player.getMainScreen().closePopup();
/*    */           }
/*    */           else {
/* 72 */             player.sendNotification("Mob Unavailable", "Invalid mob type.", Material.FIRE);
/*    */           }
/*    */         }
/*    */         else {
/* 76 */           player.sendNotification("Mob Unavailable", "Permission denied.", Material.FIRE);
/*    */         }
/*    */       } else {
/* 79 */         player.sendMessage(org.bukkit.ChatColor.RED + "You don't have permission to change spawner types!");
/* 80 */         player.getMainScreen().closePopup();
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\Downloads\MonsterBox.jar!\tux2\MonsterBox\MonsterBoxScreenListener.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */