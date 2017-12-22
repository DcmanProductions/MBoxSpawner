/*    */ package com.dcman58.spawner;
/*    */ 
/*    */ import org.getspout.spoutapi.gui.GenericButton;
/*    */ import org.getspout.spoutapi.gui.GenericLabel;
/*    */ import org.getspout.spoutapi.gui.GenericPopup;
/*    */ import org.getspout.spoutapi.gui.Widget;
/*    */ import org.getspout.spoutapi.player.SpoutPlayer;
/*    */ 
/*    */ public class SpoutStuff
/*    */ {
/*    */   MonsterBox plugin;
/*    */   
/*    */   public SpoutStuff(MonsterBox plugin)
/*    */   {
/* 15 */     this.plugin = plugin;
/*    */   }
/*    */   
/*    */   public void createMonsterGUI(String title, boolean showprices, SpoutPlayer splayer) {
/* 19 */     if (this.plugin.usespout != null) {
/* 20 */       GenericPopup monsters = new GenericPopup();
/* 21 */       CreatureTypes[] mobs = CreatureTypes.values();
/* 22 */       int x = 5;
/* 23 */       int y = 20;
/* 24 */       GenericLabel label = new GenericLabel(title);
/* 25 */       label.setWidth(200).setHeight(20);
/* 26 */       label.setTextColor(new org.getspout.spoutapi.gui.Color(0, 200, 0));
/* 27 */       label.setAlign(org.getspout.spoutapi.gui.WidgetAnchor.TOP_CENTER).setAnchor(org.getspout.spoutapi.gui.WidgetAnchor.TOP_CENTER);
/* 28 */       label.shiftYPos(5);
/* 29 */       monsters.attachWidget(this.plugin, label);
/* 30 */       CreatureTypes[] arrayOfCreatureTypes1; int j = (arrayOfCreatureTypes1 = mobs).length; for (int i = 0; i < j; i++) { CreatureTypes mob = arrayOfCreatureTypes1[i];
/* 31 */         GenericButton tbutton = new GenericButton(mob.toString());
/* 32 */         if ((showprices) && (this.plugin.hasEconomy())) {
/* 33 */           tbutton.setTooltip(this.plugin.iConomy.format(this.plugin.getMobPrice(mob.toString())));
/*    */         }
/* 35 */         tbutton.setX(x).setY(y);
/* 36 */         tbutton.setWidth(this.plugin.buttonwidth).setHeight(20);
/* 37 */         monsters.attachWidget(this.plugin, tbutton);
/* 38 */         y += 30;
/* 39 */         if (y > 180) {
/* 40 */           y = 20;
/* 41 */           x += this.plugin.buttonwidth + 5;
/*    */         }
/*    */       }
/* 44 */       GenericButton tbutton = new GenericButton("Close");
/* 45 */       tbutton.setX(200).setY(210);
/* 46 */       tbutton.setWidth(80).setHeight(20);
/* 47 */       monsters.attachWidget(this.plugin, tbutton);
/* 48 */       splayer.getMainScreen().attachPopupScreen(monsters);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\Downloads\MonsterBox.jar!\tux2\MonsterBox\SpoutStuff.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */