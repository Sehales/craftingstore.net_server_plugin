package net.craftingstore.connector;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import net.craftingstore.connector.cmd.CmdCraftingstore;
import net.craftingstore.connector.util.PingUtils;
import net.craftingstore.connector.util.VaultHandler;
import net.craftingstore.connector.virtualinventory.InventoryListener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.alecgorge.minecraft.jsonapi.JSONAPI;

//ALL RIGHTS RESERVED
//COPYRIGHT 2013 CRAFTINGSTORE.NET
public class Connector extends JavaPlugin {

	private static Plugin       plugin;              //an instance of the plugin which is representing our Connector class
	private static JSONAPI      jsonapi;

	public static boolean       debugMode    = false;                                                          //for developement reasons only, should be false in production
	private static Database     db;
	private static VaultHandler vaultHandler = null;

	/**
	 * @return the db
	 */
	public static Connection getDBCon() {
		return db.getConnection();
	}

	public static JSONAPI getJSONAPI() {
		return jsonapi;
	}

	public static Plugin getPlugin() {

		return plugin;
	}

	public static VaultHandler getVaultHandler() {
		return vaultHandler;
	}

	public static Logger logger() {
		return plugin.getLogger();
	}

	private void initConfig() {
		//first make sure there is no config.yml already, saveDefaultConfig is checking this too, but we want don't want to make api calls we don't need!
		File f = new File(getDataFolder() + "/config.yml");
		if (!f.exists())
			saveDefaultConfig();
		//and now load the config
		reloadConfig();
	}

	private void initDB() throws SQLException {
		Statement stmtInv = db.getConnection().createStatement();
		stmtInv.execute("CREATE TABLE IF NOT EXISTS inventories("

		+ "name VARCHAR(64) UNIQUE," + "size INT," + "serializedInv VARCHAR(20000)" //we don't know how much items must be stored so we take a very big number

		        + ");");
		stmtInv.close();

		Statement stmtCmd = db.getConnection().createStatement();
		stmtCmd.execute("CREATE TABLE IF NOT EXISTS commands("

		+ "id INTEGER PRIMARY KEY AUTOINCREMENT, playerName VARCHAR(64)," + "commandName VARCHAR(100)," + "executionTime INT," + "playerOnline INT," + "executed INT" //same like above but for commands...

		        + ");");

		stmtCmd.execute("CREATE INDEX IF NOT EXISTS executed on commands (executed);");
		stmtCmd.close();

		Statement stmtMsg = db.getConnection().createStatement();
		stmtMsg.execute("CREATE TABLE IF NOT EXISTS messages("

		+ "target VARCHAR(16), message VARCHAR(20000), transactionId INT UNIQUE, isRead INT NOT NULL DEFAULT 0" //there is no information needed, huh?

		        + ");");
		stmtMsg.close();
	}

	private void offlinePing() {
		String input = "service unavailable"; //if we can't connect to craftingstore.net we will display service unavailable as default
		try {
			String subdomain = plugin.getConfig().getString("subdomain"); //first get the subdomain
			//connect to the api and get the result(input)
			input = PingUtils.connect(String.format("http://secure.craftingstore.net/serverapi.php?subdomain=%s&mode=%s&key=%s", subdomain, PingUtils.Mode.OFFLINE_PING,
			        PingUtils.getHash(subdomain, PingUtils.Mode.OFFLINE_PING, jsonapi.getConfig().getString("logins." + plugin.getConfig().getString("jsonapi-user")))));
			if (input.equalsIgnoreCase("true"))
				//yay that made my day
				getLogger().info("offline notification has been sent to craftingstore.net"); //maybe the admin wants to know something about what is happening, just inform him(or her)
			else
				getLogger().warning("Tried to connect to craftingstore.net, received error message: " + input);
		} catch (IOException e) {
			getLogger().severe("Can't reach craftingstore.net, result: " + input);
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		offlinePing(); //try to notify craftingstore.net
		//TODO: Save config properly without loosing the comments
	}

	@Override
	public void onEnable() {
		plugin = this;

		initConfig(); //first make sure we have a config!

		debugMode = getConfig().getBoolean("debugMode", false);

		db = new Database("plugins/CraftingStoreConnector/", "connector.db");

		try {
			initDB();
		} catch (SQLException e) {
			getLogger().severe("Something went wrong with the sqlite database! Please report this error!");
			e.printStackTrace();
			return;
		}

		if (Bukkit.getServer().getPluginManager().getPlugin("JSONAPI") != null)
			jsonapi = (JSONAPI) getServer().getPluginManager().getPlugin("JSONAPI");
		else
			return;

		jsonapi.registerMethods(new AdditionalJsonChatAPI());
		Bukkit.getPluginManager().registerEvents(new MsgListener(), plugin);

		if (getConfig().getString("subdomain").equalsIgnoreCase("yoursubdomain")) {
			getLogger().warning("You should check the config and change the subdomain entry to your craftingstore.net subdomain!");
			getLogger().info("Please complete the setup descripted at support.craftingstore.net");
			return;
		}

		if (getConfig().getBoolean("allow-exchange") && Bukkit.getServer().getPluginManager().getPlugin("Vault") != null)
			vaultHandler = new VaultHandler();
		else
			getLogger().info("Vault support has been turned off");

		getCommand("craftingstore").setExecutor(new CmdCraftingstore(plugin));

		jsonapi.registerMethods(new AdditionalJsonApiMethods());
		Bukkit.getPluginManager().registerEvents(new CmdListener(), plugin);
		Bukkit.getPluginManager().registerEvents(new InventoryListener(), plugin);
		//		Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), plugin);
		onlinePing(); //try to notify craftingstore.net

		Bukkit.getScheduler().runTaskLater(plugin, new CheckVersionTask(plugin, jsonapi), 1l);

		Bukkit.getScheduler().runTaskTimer(plugin, new CommandTimerTask(), 1l, 20l * 60l);

	}

	private void onlinePing() {
		OnlinePingTask task = new OnlinePingTask(plugin, jsonapi);
		BukkitTask onTask = Bukkit.getScheduler().runTaskTimer(plugin, task, 1l, 20l * 15l);
		task.setTask(onTask);
	}
}
