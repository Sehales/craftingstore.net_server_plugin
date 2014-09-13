package net.craftingstore.connector;

import java.io.IOException;

import net.craftingstore.connector.util.PingUtils;
import net.craftingstore.connector.util.VersionUtils;

import org.bukkit.plugin.Plugin;

class CheckVersionTask implements Runnable {

	private Plugin plugin;
	private Plugin jsonapi;

	CheckVersionTask(Plugin plugin, Plugin jsonapi) {
		this.plugin = plugin;
		this.jsonapi = jsonapi;
	}

	private void checkVersion() {
		String input = "service unavailable"; //if we can't connect to craftingstore.net we will display service unavailable as default
		try {
			String subdomain = plugin.getConfig().getString("subdomain", "yourSubdomain"); //first get the subdomain
			//connect to the api and get the result(input)
			input = PingUtils.connect(String.format("http://secure.craftingstore.net/serverapi.php?subdomain=%s&mode=%s&key=%s", subdomain, PingUtils.Mode.GET_VERSION,
			        PingUtils.getHash(subdomain, PingUtils.Mode.GET_VERSION, jsonapi.getConfig().getString("logins." + plugin.getConfig().getString("jsonapi-user")))));
			if (!input.equalsIgnoreCase("") && !input.equalsIgnoreCase("service unavailable")) {
				String[] args = input.split(",");
				int result = VersionUtils.compareVersions(plugin.getDescription().getVersion(), args[0]);
				if (result >= 0) {
					plugin.getLogger().info("Your version is up to date");
					VersionUtils.setUpToDate(true);
				} else {
					plugin.getLogger().warning("You are using an outdated version! It is important to be up to date!");
					plugin.getLogger().warning(String.format("see %s for recent versions", args[1]));
					VersionUtils.setUpToDate(false);
					VersionUtils.downloadLink = args[1];
				}
			} else
				plugin.getLogger().warning("Tried to connect to craftingstore.net, received error message: " + input);
		} catch (IOException e) {
			plugin.getLogger().severe("Can't reach craftingstore.net, result: " + input);
			//	e.printStackTrace();
		}
	}

	@Override
	public void run() {
		checkVersion();
	}
}
