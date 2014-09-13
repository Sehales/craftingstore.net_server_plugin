package net.craftingstore.connector.cmd;

import net.craftingstore.connector.Connector;
import net.craftingstore.connector.util.ChatUtils;
import net.craftingstore.connector.virtualinventory.VirtualInventory;
import net.craftingstore.connector.virtualinventory.VirtualInventoryHandler;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.Lists;

public class CmdCraftingstore implements CommandExecutor {

	private Plugin plugin;

	public CmdCraftingstore(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length > 0)
			if (sender instanceof Player && (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("mail") || args[0].equalsIgnoreCase("mailbox"))) {
				//				if (plugin.getConfig().getBoolean("mailboxOnly", false)) {
				//					sender.sendMessage(ChatUtils.formatMessage(plugin.getConfig().getString("lang.mailbox-only", "<red>Mailboxes are not accessible via command!")));
				//					return false;
				//				}

				if (!VirtualInventoryHandler.contains(String.format(String.format(VirtualInventory.INV_NAME, sender.getName()))))
					new VirtualInventory(sender.getName());
				VirtualInventoryHandler.getVirtualInventory(String.format(VirtualInventory.INV_NAME, sender.getName())).open(((Player) sender).getPlayer());
				return true;
			} else if (args[0].equalsIgnoreCase("reload") && (sender.isOp() || sender.hasPermission("craftingstore.admin.reload"))) {
				this.plugin.reloadConfig();
				sender.sendMessage(ChatUtils.formatMessage(plugin.getConfig().getString("lang.reload-msg", "<gold>Config has been reloaded!")));
				return true;
			} else if (args[0].equalsIgnoreCase("exchange") && sender.hasPermission("craftingstore.exchange") && sender instanceof Player) {
				Player player = (Player) sender;
				if (Connector.getVaultHandler() != null) {
					if (args.length > 1) {
						if (NumberUtils.isNumber(args[1]))
							Connector.getVaultHandler().exchange(player, NumberUtils.toDouble(args[1]));
						else
							sender.sendMessage(ChatUtils.formatMessage(plugin.getConfig().getString("lang.not-a-number", "<red>Please type in a valid number!")));
					} else
						sender.sendMessage(ChatUtils.formatMessage(plugin.getConfig().getString("lang.specify-value", "<red>You have to specify an amount which you want to exchange!")));
				} else
					sender.sendMessage(ChatUtils.formatMessage(plugin.getConfig().getString("lang.feature-not-available", "<red>That feature is not available!")));
				return true;
			}

		//				else if (args[0].equalsIgnoreCase("setmailbox") && (sender.isOp() || sender.hasPermission("craftingstore.mailbox.set"))) {
		//
		//				if (!plugin.getConfig().getBoolean("mailbox.enabled", true)) {
		//					sender.sendMessage(ChatUtils.formatMessage(plugin.getConfig().getString("lang.mailbox-disabled", "<red>Mailboxes are disabled on this server!")));
		//					return false;
		//				}
		//
		//				if (sender instanceof Player) {
		//					Player p = (Player) sender;
		//					Block target = p.getTargetBlock(null, 10);
		//
		//					if (target.getTypeId() == plugin.getConfig().getInt("mailbox.blockId", 130)) {
		//						if (MailBoxHandler.addMailBox(new MailBox(target.getLocation(), p))) {
		//							p.sendMessage(ChatUtils.formatMessage(plugin.getConfig().getString("lang.mailbox-created", "<gold>Your store mailbox was created!")));
		//							target.getWorld().playEffect(target.getLocation(), Effect.MOBSPAWNER_FLAMES, 0, 3);
		//							target.setMetadata("craftingstore_mailbox", new MailBoxMetaValue(plugin, String.format(VirtualInventory.INV_NAME, sender.getName())));
		//						} else
		//							p.sendMessage(ChatUtils.formatMessage(plugin.getConfig().getString("lang.mailbox-not-created", "<red>Your store mailbox could not get created!")));
		//						return true;
		//					}
		//					sender.sendMessage(ChatUtils.formatMessage(plugin.getConfig().getString("lang.mailbox-invalid-block", "<red>That block can not be a mailbox! Please try another one!")));
		//				} else {
		//					sender.sendMessage("The console can't set a mailbox!");
		//					return true;
		//				}
		//			}
		//			else if (args[0].equalsIgnoreCase("boxes") && sender.isOp()) {
		//				sender.sendMessage(MailBoxHandler.getListString());
		//				return true;
		//			}
		ChatUtils.sendFormattedMessage(
		        sender,
		        Lists.newArrayList(String.format(
		                plugin.getConfig().getString("lang.default-msg",
		                        "<gold>To access our store visit: <newline><red>http://%s.craftingstore.net<newline><gold>To open your mailbox box try /store mailbox"),
		                plugin.getConfig().getString("subdomain")).split("<newline>")));
		return true;
	}
}
