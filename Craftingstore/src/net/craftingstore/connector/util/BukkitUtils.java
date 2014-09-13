package net.craftingstore.connector.util;

import net.craftingstore.connector.Connector;

import org.bukkit.Bukkit;

public class BukkitUtils {

	public static boolean executeCommand(String cmd) {
		if (Connector.debugMode)
			System.out.println("BukkitUtils: executing command: " + cmd);
		return Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
	}

}
