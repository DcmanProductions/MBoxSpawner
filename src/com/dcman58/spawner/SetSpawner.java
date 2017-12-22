/*    */ package com.dcman58.spawner;
/*    */ 
/*    */ import org.bukkit.block.CreatureSpawner;
/*    */ 
/*    */ public class SetSpawner implements Runnable
/*    */ {
/*    */   CreatureSpawner ts;
/*    */   CreatureTypes ct;
/*    */   
/*    */   public SetSpawner(CreatureSpawner theSpawner, CreatureTypes ct)
/*    */   {
/* 12 */     this.ts = theSpawner;
/* 13 */     this.ct = ct;
/*    */   }
/*    */   
/*    */   public void run()
/*    */   {
/* 18 */     this.ts.setSpawnedType(org.bukkit.entity.EntityType.valueOf(this.ct.toString()));
/*    */   }
/*    */ }