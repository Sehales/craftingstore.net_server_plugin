package net.craftingstore.connector;

import net.craftingstore.connector.util.PingUtils;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

class OnlinePingTask implements Runnable {

	private BukkitTask task;
	private boolean    success = false;
	private Plugin     plugin;
	private Plugin     jsonapi;

	OnlinePingTask(Plugin plugin, Plugin jsonapi) {
		this.plugin = plugin;
		this.jsonapi = jsonapi;
	}

	void destroy() {
		success = true;
		task.cancel();
	}

	@Override
	public void run() {
		if (!success) {
			String input = "service unavailable"; //if we can't connect to craftingstore.net we will display service unavailable as default
			try {
				String subdomain = plugin.getConfig().getString("subdomain"); //first get the subdomain
				//connect to the api and get the result(input)
				input = PingUtils.connect(String.format("http://secure.craftingstore.net/serverapi.php?subdomain=%s&mode=%s&key=%s", subdomain, PingUtils.Mode.ONLINE_PING,
				        PingUtils.getHash(subdomain, PingUtils.Mode.ONLINE_PING, jsonapi.getConfig().getString("logins." + plugin.getConfig().getString("jsonapi-user")))));
				if (input.equalsIgnoreCase("true")) {
					plugin.getLogger().info("online notification has been sent to craftingstore.net"); //maybe the admin wants to know something about what is happening, just inform him(or her)
					destroy(); //we have contacted craftingstore.net, lets disable the task!
				} else {
					plugin.getLogger().warning("Tried to connect to craftingstore.net, received error message: " + input); //inform the admin
					plugin.getLogger().info("retrying soon"); //we never give up!
				}
			} catch (Exception e) {
				plugin.getLogger().severe("Can't reach craftingstore.net, result: " + input);
			}
		} else
			destroy();
	}

	void setTask(BukkitTask task) {
		this.task = task;
	}
}
