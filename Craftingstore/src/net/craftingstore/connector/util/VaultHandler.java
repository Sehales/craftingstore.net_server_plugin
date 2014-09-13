package net.craftingstore.connector.util;

import net.craftingstore.connector.Connector;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHandler {

	private Economy eco;

	public VaultHandler() {
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null)
			eco = economyProvider.getProvider();
	}

	public void exchange(Player player, double amount) {
		EconomyResponse result = eco.withdrawPlayer(player.getName(), amount);
		if (result.transactionSuccess())
			try {

				String input = "service unavailable";
				String subdomain = Connector.getPlugin().getConfig().getString("subdomain", "yourSubdomain");

				input = PingUtils.connect(String.format(
				        "http://secure.craftingstore.net/serverapi.php?subdomain=%s&mode=%s&user=%s&points=%d&key=%s",
				        subdomain,
				        PingUtils.Mode.ADD_POINTS,
				        player.getName(),
				        (long) amount,
				        PingUtils.getExchangeHash(subdomain, PingUtils.Mode.ADD_POINTS,
				                Connector.getJSONAPI().getConfig().getString("logins." + Connector.getPlugin().getConfig().getString("jsonapi-user")), player.getName(), (long) amount)));

				if (!input.equalsIgnoreCase("true")) {
					if (Connector.debugMode)
						Connector.logger().info("DEBUG input value of addPoints request: " + input);
					player.sendMessage(ChatUtils.formatMessage(Connector.getPlugin().getConfig().getString("lang.not-registered", "<red>You have to register in the shop first!")));
					eco.depositPlayer(player.getName(), amount);
					return;
				}

				player.sendMessage(ChatUtils.formatMessage(String.format(Connector.getPlugin().getConfig().getString("lang.transaction-successful", "<gold>You have transfered %d points to the shop"),
				        (long) amount)));
			} catch (Exception e) {
				eco.depositPlayer(player.getName(), amount);
				player.sendMessage(ChatUtils.formatMessage(Connector.getPlugin().getConfig().getString("lang.currently-not-available", "<red>Can't reach transaction server, please try again later")));
			}
		else
			player.sendMessage(ChatUtils.formatMessage(Connector.getPlugin().getConfig().getString("lang.transaction-failed", "<red>Your transaction failed, please check your account balance")));
	}
}
