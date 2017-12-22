package com.dcman58.spawner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spout.Spout;

import net.milkbowl.vault.economy.Economy;

public class MonsterBox extends JavaPlugin {
	private final HashMap<Player, Boolean> debugees = new HashMap();
	private final ConcurrentHashMap<String, Double> mobprice = new ConcurrentHashMap();
	private final ConcurrentHashMap<String, Double> mobeggprice = new ConcurrentHashMap();
	private final ConcurrentHashMap<String, LinkedList<EntityType>> disabledspawners = new ConcurrentHashMap();
	ConcurrentHashMap<String, Integer> disabledspawnerlocs = new ConcurrentHashMap();
	public MonsterBoxBlockListener bl;
	public Economy iConomy = null;
	boolean useiconomy = false;
	public double iconomyprice = 0.0D;
	public Spout usespout = null;
	public boolean separateprices = false;
	public Material tool = Material.GOLD_SWORD;
	public int buttonwidth = 80;
	public String version = "0.8";
	public SpoutStuff ss = null;
	public HashSet<Byte> transparentBlocks = new HashSet();
	private ConcurrentHashMap<String, String> mobcase = new ConcurrentHashMap();
	public String eggthrowmessage = "I'm sorry, but you can't spawn that mob.";
	public boolean needssilktouch = false;
	public double eggprice = 0.0D;
	public boolean separateeggprices = false;
	public String blocksavefile = "plugins/MonsterBox/disabledspawners.list";

	public MonsterBox() {
		loadconfig();
		loadprices();
		loadeggprices();
		loadDisabledSpawners();

		this.transparentBlocks.add(Byte.valueOf((byte) 0));
		this.transparentBlocks.add(Byte.valueOf((byte) 8));
		this.transparentBlocks.add(Byte.valueOf((byte) 9));
		this.transparentBlocks.add(Byte.valueOf((byte) 20));
		this.transparentBlocks.add(Byte.valueOf((byte) 30));
		this.transparentBlocks.add(Byte.valueOf((byte) 65));
		this.transparentBlocks.add(Byte.valueOf((byte) 66));
		this.transparentBlocks.add(Byte.valueOf((byte) 78));
		this.transparentBlocks.add(Byte.valueOf((byte) 83));
		this.transparentBlocks.add(Byte.valueOf((byte) 101));
		this.transparentBlocks.add(Byte.valueOf((byte) 102));
		this.transparentBlocks.add(Byte.valueOf((byte) 106));
	}

	private void loadprices() {
		File folder = new File("plugins/MonsterBox");

		File configFile = new File("plugins/MonsterBox/prices.ini");
		if (configFile.exists()) {
			try {
				this.mobprice.clear();
				Properties theprices = new Properties();
				theprices.load(new FileInputStream(configFile));
				Iterator<Map.Entry<Object, Object>> iprices = theprices.entrySet().iterator();
				while (iprices.hasNext()) {
					Map.Entry<Object, Object> price = (Map.Entry) iprices.next();
					try {
						this.mobprice.put(price.getKey().toString().toLowerCase(), new Double(price.getValue().toString()));
					} catch (NumberFormatException ex) {
						System.out.println("[MonsterBox] Unable to parse the value for " + price.getKey().toString());
					}
				}
			} catch (IOException localIOException) {
			}
			if (this.mobprice.size() < CreatureTypes.values().length) {
				System.out.println("[MonsterBox] - New mobs found! Updating prices.ini");
				createprices();
			}
		} else {
			System.out.println("[MonsterBox] Price file not found");
			folder.mkdir();

			System.out.println("[MonsterBox] - creating file prices.ini");
			createprices();
		}
	}

	private void loadeggprices() {
		File folder = new File("plugins/MonsterBox");

		File configFile = new File("plugins/MonsterBox/eggprices.ini");
		if (configFile.exists()) {
			try {
				this.mobeggprice.clear();
				Properties theprices = new Properties();
				theprices.load(new FileInputStream(configFile));
				Iterator<Map.Entry<Object, Object>> iprices = theprices.entrySet().iterator();
				while (iprices.hasNext()) {
					Map.Entry<Object, Object> price = (Map.Entry) iprices.next();
					try {
						this.mobeggprice.put(price.getKey().toString().toLowerCase(), new Double(price.getValue().toString()));
					} catch (NumberFormatException ex) {
						System.out.println("[MonsterBox] Unable to parse the value for " + price.getKey().toString() + "in the eggprices.ini file.");
					}
				}
			} catch (IOException localIOException) {
			}
			if (this.mobeggprice.size() < CreatureTypes.values().length) {
				System.out.println("[MonsterBox] - New mobs found! Updating eggprices.ini");
				createeggprices();
			}
		} else {
			System.out.println("[MonsterBox] Egg price file not found");
			folder.mkdir();

			System.out.println("[MonsterBox] - creating file eggprices.ini");
			createeggprices();
		}
	}

	private void createprices() {
		try {
			BufferedWriter outChannel = new BufferedWriter(new FileWriter("plugins/MonsterBox/prices.ini"));
			outChannel.write("#This config file contains all the separate prices for all the mobs\n# if the option separateprices is true\n\n\n");

			CreatureTypes[] mobs = CreatureTypes.values();
			CreatureTypes[] arrayOfCreatureTypes1;
			int j = (arrayOfCreatureTypes1 = mobs).length;
			for (int i = 0; i < j; i++) {
				CreatureTypes mob = arrayOfCreatureTypes1[i];
				outChannel.write(mob.toString() + " = " + String.valueOf(getMobPrice(mob.toString())) + "\n");
			}
			outChannel.close();
		} catch (Exception e) {
			System.out.println("[MonsterBox] - Prices file creation failed, using defaults.");
		}
	}

	private void createeggprices() {
		try {
			BufferedWriter outChannel = new BufferedWriter(new FileWriter("plugins/MonsterBox/eggprices.ini"));
			outChannel.write("#This config file contains all the separate prices for all the mobs\n# for eggs if the option separateeggprices is true\n\n\n");

			CreatureTypes[] mobs = CreatureTypes.values();
			CreatureTypes[] arrayOfCreatureTypes1;
			int j = (arrayOfCreatureTypes1 = mobs).length;
			for (int i = 0; i < j; i++) {
				CreatureTypes mob = arrayOfCreatureTypes1[i];
				outChannel.write(mob.toString() + " = " + String.valueOf(getEggMobPrice(mob.toString())) + "\n");
			}
			outChannel.close();
		} catch (Exception e) {
			System.out.println("[MonsterBox] - Egg prices file creation failed, using defaults.");
		}
	}

	public void onEnable() {
		setupSpout();
		setupMobCase();

		CustomRecipe cr = new CustomRecipe();

		PluginManager pm = getServer().getPluginManager();
		this.bl = new MonsterBoxBlockListener(this);
		MonsterBoxPlayerListener pl = new MonsterBoxPlayerListener(this);
		if (this.useiconomy) {
			setupEconomy();
		}
		pm.registerEvents(this.bl, this);
		pm.registerEvents(pl, this);
		if (this.usespout != null) {
			pm.registerEvents(new MonsterBoxScreenListener(this), this);
			this.ss = new SpoutStuff(this);
		}
		MonsterBoxCommands commandL = new MonsterBoxCommands(this);
		PluginCommand batchcommand = getCommand("mbox");
		batchcommand.setExecutor(commandL);

		PluginDescriptionFile pdfFile = getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}

	public void onDisable() {
		System.out.println("MonsterBox disabled!");
	}

	public boolean isDebugging(Player player) {
		if (this.debugees.containsKey(player)) {
			return ((Boolean) this.debugees.get(player)).booleanValue();
		}
		return false;
	}

	public void setDebugging(Player player, boolean value) {
		this.debugees.put(player, Boolean.valueOf(value));
	}

	private void setupSpout() {
		Plugin p = getServer().getPluginManager().getPlugin("Spout");
		if (p == null) {
			this.usespout = null;
			System.out.println("[MonsterBox] Spout not detected. Disabling spout support.");
		} else {
			try {
				this.usespout = ((Spout) p);
			} catch (Exception e) {
				System.out.println("[MonsterBox] Error hooking into spout. Disabling spout support.");
			}
			System.out.println("[MonsterBox] Spout detected. Spout support enabled.");
		}
	}

	public boolean hasPermissions(Player player, String node) {
		return player.hasPermission(node);
	}

	private void loadconfig() {
		File folder = new File("plugins/MonsterBox");

		File configFile = new File("plugins/MonsterBox/settings.ini");
		if (configFile.exists()) {
			try {
				Properties themapSettings = new Properties();
				themapSettings.load(new FileInputStream(configFile));

				String iconomy = themapSettings.getProperty("useEconomy", "false");
				String price = themapSettings.getProperty("price", "0.0");
				String eggsprice = themapSettings.getProperty("eggprice", "0.0");
				String sprices = themapSettings.getProperty("separateprices", "false");
				String seggprices = themapSettings.getProperty("separateeggprices", "false");
				String swidth = themapSettings.getProperty("buttonwidth", "100");
				String stool = themapSettings.getProperty("changetool", Material.GOLD_SWORD.toString());

				String theversion = themapSettings.getProperty("version", "0.1");
				this.eggthrowmessage = themapSettings.getProperty("eggdenymessage", this.eggthrowmessage);
				String silktouch = themapSettings.getProperty("needssilktouch", "false");

				this.needssilktouch = stringToBool(silktouch);

				this.useiconomy = stringToBool(iconomy);
				this.separateprices = stringToBool(sprices);
				this.separateeggprices = stringToBool(seggprices);
				try {
					int itool = Integer.parseInt(stool.trim());
					this.tool = Material.getMaterial(itool);
					updateIni();
				} catch (Exception ex) {
					this.tool = Material.getMaterial(stool.trim().toUpperCase());
				}
				try {
					this.buttonwidth = Integer.parseInt(swidth.trim());
				} catch (Exception localException1) {
				}
				try {
					this.iconomyprice = Double.parseDouble(price.trim());
				} catch (Exception localException2) {
				}
				try {
					this.eggprice = Double.parseDouble(eggsprice.trim());
				} catch (Exception localException3) {
				}
				double dbversion = 0.1D;
				try {
					dbversion = Double.parseDouble(theversion.trim());
				} catch (Exception localException4) {
				}
				if (dbversion >= 0.8D) {
					return;
				}
				if (dbversion == 0.1D) {
					String sconomy = themapSettings.getProperty("useiConomy", "false");
					this.useiconomy = stringToBool(sconomy);
				}
				updateIni();
			} catch (IOException localIOException) {
			}
		} else {
			System.out.println("[MonsterBox] Configuration file not found");

			System.out.println("[MonsterBox] + creating folder plugins/MonsterBox");
			folder.mkdir();

			System.out.println("[MonsterBox] - creating file settings.ini");
			updateIni();
		}
	}

	private void updateIni() {
		try {
			BufferedWriter outChannel = new BufferedWriter(new FileWriter("plugins/MonsterBox/settings.ini"));
			outChannel.write("#This is the main MonsterBox config file\n#\n# useiConomy: Charge to change monster spawner type using your economy system\nuseEconomy = " +

					this.useiconomy + "\n" + "# price: The price to change monster spawner type\n" + "price = " + this.iconomyprice + "\n\n" + "# eggprice: The price to change monster spawner type using eggs\n" + "eggprice = " + this.eggprice + "\n\n" + "# separateprices: If you want separate prices for all the different types of mobs\n" + "# set this to true.\n" + "separateprices = " + this.separateprices + "\n" + "# separateeggprices: If you want separate prices for all the different types of mobs\n" + "# set this to true.\n" + "separateeggprices = " + this.separateeggprices + "\n" + "# changetool is the tool that opens up the spout gui for changing the monster spawner.\n" + "changetool = " + this.tool.toString() + "\n" + "# needssilktouch Does the player need a silk touch enchanted tool to get a spawner?.\n" + "needssilktouch = " + this.needssilktouch + "\n" + "# buttonwidth changes the width of the buttons in the spoutcraft gui, just in case the\n"
					+ "# text doesn't fit for you.\n" + "buttonwidth = " + this.buttonwidth + "\n\n" + "# eggdenymessage sets the message displayed to players when they are denied egg spawning\n" + "# if they have the monsterbox.eggthrowmessage permission node.\n" + "eggdenymessage = " + this.eggthrowmessage + "\n\n" + "#Do not change anything below this line unless you know what you are doing!\n" + "version = " + this.version);
			outChannel.close();
		} catch (Exception e) {
			System.out.println("[MonsterBox] - file creation failed, using defaults.");
		}
	}

	public boolean hasEconomy() {
		if (this.iConomy != null) {
			return this.iConomy.isEnabled();
		}
		return false;
	}

	private synchronized boolean stringToBool(String thebool) {
		boolean result;
		if ((thebool.trim().equalsIgnoreCase("true")) || (thebool.trim().equalsIgnoreCase("yes"))) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	boolean setSpawner(Block targetBlock, String type) {
		try {
			CreatureSpawner theSpawner = (CreatureSpawner) targetBlock.getState();
			if (this.mobcase.containsKey(type.toLowerCase().trim())) {
				type = (String) this.mobcase.get(type.toLowerCase().trim());
			} else {
				type = capitalCase(type);
			}
			EntityType ct = null;
			if (type.equalsIgnoreCase("bat")) {
				ct = EntityType.BAT;
			} else if (type.equalsIgnoreCase("blaze")) {
				ct = EntityType.BLAZE;
			} else if (type.equalsIgnoreCase("cavespider")) {
				ct = EntityType.CAVE_SPIDER;
			} else if (type.equalsIgnoreCase("chicken")) {
				ct = EntityType.CHICKEN;
			} else if (type.equalsIgnoreCase("cow")) {
				ct = EntityType.COW;
			} else if (type.equalsIgnoreCase("creeper")) {
				ct = EntityType.CREEPER;
			} else if (type.equalsIgnoreCase("enderdragon")) {
				ct = EntityType.ENDER_DRAGON;
			} else if (type.equalsIgnoreCase("enderman")) {
				ct = EntityType.ENDERMAN;
			} else if (type.equalsIgnoreCase("ghast")) {
				ct = EntityType.GHAST;
			} else if (type.equalsIgnoreCase("giant")) {
				ct = EntityType.GIANT;
			} else if (type.equalsIgnoreCase("horse")) {
				ct = EntityType.HORSE;
			} else if (type.equalsIgnoreCase("irongolem")) {
				ct = EntityType.IRON_GOLEM;
			} else if (type.equalsIgnoreCase("magmacube")) {
				ct = EntityType.MAGMA_CUBE;
			} else if (type.equalsIgnoreCase("mushroomcow")) {
				ct = EntityType.MUSHROOM_COW;
			} else if (type.equalsIgnoreCase("ocelot")) {
				ct = EntityType.OCELOT;
			} else if (type.equalsIgnoreCase("pig")) {
				ct = EntityType.PIG;
			} else if (type.equalsIgnoreCase("sheep")) {
				ct = EntityType.SHEEP;
			} else if (type.equalsIgnoreCase("silverfish")) {
				ct = EntityType.SILVERFISH;
			} else if (type.equalsIgnoreCase("skeleton")) {
				ct = EntityType.SKELETON;
			} else if (type.equalsIgnoreCase("slime")) {
				ct = EntityType.SLIME;
			} else if (type.equalsIgnoreCase("snowman")) {
				ct = EntityType.SNOWMAN;
			} else if (type.equalsIgnoreCase("spider")) {
				ct = EntityType.SPIDER;
			} else if (type.equalsIgnoreCase("squid")) {
				ct = EntityType.SQUID;
			} else if (type.equalsIgnoreCase("villager")) {
				ct = EntityType.VILLAGER;
			} else if (type.equalsIgnoreCase("witch")) {
				ct = EntityType.WITCH;
			} else if (type.equalsIgnoreCase("wither")) {
				ct = EntityType.WITHER;
			} else if (type.equalsIgnoreCase("wolf")) {
				ct = EntityType.WOLF;
			} else if (type.equalsIgnoreCase("zombie")) {
				ct = EntityType.ZOMBIE;
			} else if (type.equalsIgnoreCase("bossgolem")) {
				ct = EntityType.fromId(383);
			}
			if (ct == null) {
				try {
					ct = EntityType.valueOf(type.toUpperCase());
				} catch (Exception localException1) {
				}
			}
			if (ct == null) {
				return false;
			}
			theSpawner.setSpawnedType(ct);
			if (this.disabledspawnerlocs.containsKey(locationBuilder(targetBlock.getLocation()))) {
				removeDisabledSpawner(targetBlock);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	String capitalCase(String s) {
		return s.toUpperCase().charAt(0) + s.toLowerCase().substring(1);
	}

	public double getMobPrice(String name) {
		if ((this.separateprices) && (this.mobprice.containsKey(name.toLowerCase()))) {
			return ((Double) this.mobprice.get(name.toLowerCase())).doubleValue();
		}
		return this.iconomyprice;
	}

	public double getEggMobPrice(String name) {
		if ((this.separateeggprices) && (this.mobeggprice.containsKey(name.toLowerCase()))) {
			return ((Double) this.mobeggprice.get(name.toLowerCase())).doubleValue();
		}
		return this.eggprice;
	}

	private void setupMobCase() {
		CreatureTypes[] mobs = CreatureTypes.values();
		CreatureTypes[] arrayOfCreatureTypes1;
		int j = (arrayOfCreatureTypes1 = mobs).length;
		for (int i = 0; i < j; i++) {
			CreatureTypes mob = arrayOfCreatureTypes1[i];
			String mobname = mob.toString().trim();
			this.mobcase.put(mobname.toLowerCase(), mobname);
		}
	}

	private void setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null) {
			this.iConomy = ((Economy) economyProvider.getProvider());
		}
	}

	public boolean canSpawnMob(Location loc, EntityType type) {
		String locname = locationBuilder(loc);
		if (this.disabledspawners.containsKey(locname)) {
			return !((LinkedList) this.disabledspawners.get(locname)).contains(type);
		}
		return true;
	}

	public String locationBuilder(Location loc) {
		return loc.getBlockX() + "." + loc.getBlockY() + "." + loc.getBlockZ() + "." + loc.getWorld().getName();
	}

	public void addDisabledSpawner(Block spawner) {
		if (spawner.getType() == Material.MOB_SPAWNER) {
			CreatureSpawner theSpawner = (CreatureSpawner) spawner.getState();
			EntityType mobname = theSpawner.getSpawnedType();
			addDisabledSpawner(spawner.getLocation(), mobname);
		}
	}

	public void addDisabledSpawner(Location spawner, EntityType mobname) {
		int startx = spawner.getBlockX() - 4;
		int endx = startx + 8;
		int starty = spawner.getBlockY() - 1;
		int endy = starty + 8;
		int startz = spawner.getBlockZ() - 4;
		int endz = startz + 8;
		this.disabledspawnerlocs.put(locationBuilder(spawner), new Integer(mobname.getTypeId()));
		for (int x = startx; x < endx; x++) {
			for (int y = starty; y < endy; y++) {
				for (int z = startz; z < endz; z++) {
					String location = x + "." + y + "." + z + "." + spawner.getWorld().getName();
					if (this.disabledspawners.containsKey(location)) {
						((LinkedList) this.disabledspawners.get(location)).add(mobname);
					} else {
						LinkedList<EntityType> tlist = new LinkedList();
						tlist.add(mobname);
						this.disabledspawners.put(location, tlist);
					}
				}
			}
		}
		saveDisabledSpawners();
	}

	public void removeDisabledSpawner(Block spawner) {
		if (spawner.getType() == Material.MOB_SPAWNER) {
			CreatureSpawner theSpawner = (CreatureSpawner) spawner.getState();
			EntityType mobname = theSpawner.getSpawnedType();
			removeDisabledSpawner(spawner.getLocation(), mobname);
		}
	}

	public void removeDisabledSpawner(Location spawner, EntityType mobname) {
		int startx = spawner.getBlockX() - 4;
		int endx = startx + 8;
		int starty = spawner.getBlockY() - 1;
		int endy = starty + 8;
		int startz = spawner.getBlockZ() - 4;
		int endz = startz + 8;
		this.disabledspawnerlocs.remove(locationBuilder(spawner));
		for (int x = startx; x < endx; x++) {
			for (int y = starty; y < endy; y++) {
				for (int z = startz; z < endz; z++) {
					String location = x + "." + y + "." + z + "." + spawner.getWorld().getName();
					if (this.disabledspawners.containsKey(location)) {
						((LinkedList) this.disabledspawners.get(location)).remove(mobname);
					}
				}
			}
		}
		saveDisabledSpawners();
	}

	public void saveDisabledSpawners() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(this.blocksavefile)));
			out.writeObject(this.disabledspawnerlocs);
			out.flush();
			out.close();
		} catch (Exception localException) {
		}
	}

	public void loadDisabledSpawners() {
		try {
			ObjectInputStream out = new ObjectInputStream(new FileInputStream(new File(this.blocksavefile)));
			this.disabledspawnerlocs = ((ConcurrentHashMap) out.readObject());
			Set<String> keys = this.disabledspawnerlocs.keySet();
			for (String key : keys) {
				try {
					int mobtype = ((Integer) this.disabledspawnerlocs.get(key)).intValue();
					EntityType type = EntityType.fromId(mobtype);
					String[] location = key.split("\\.");
					String destworld = location[3];
					int x = Integer.parseInt(location[0]);
					int y = Integer.parseInt(location[1]);
					int z = Integer.parseInt(location[2]);
					int startx = x - 4;
					int endx = startx + 8;
					int starty = y - 1;
					int endy = starty + 8;
					int startz = z - 4;
					int endz = startz + 8;
					for (int x1 = startx; x1 < endx; x1++) {
						for (int y1 = starty; y1 < endy; y1++) {
							for (int z1 = startz; z1 < endz; z1++) {
								String slocation = x1 + "." + y1 + "." + z1 + "." + destworld;
								if (this.disabledspawners.containsKey(slocation)) {
									((LinkedList) this.disabledspawners.get(slocation)).add(type);
								} else {
									LinkedList<EntityType> tlist = new LinkedList();
									tlist.add(type);
									this.disabledspawners.put(slocation, tlist);
								}
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			out.close();
		} catch (Exception localException1) {
		}
	}
}
