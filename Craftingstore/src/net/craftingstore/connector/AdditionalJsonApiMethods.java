package net.craftingstore.connector;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import net.craftingstore.connector.util.BukkitUtils;
import net.craftingstore.connector.util.ChatUtils;
import net.craftingstore.connector.util.ItemUtils;
import net.craftingstore.connector.virtualinventory.VirtualInventory;
import net.craftingstore.connector.virtualinventory.VirtualInventoryHandler;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.alecgorge.minecraft.jsonapi.dynamic.API_Method;
import com.alecgorge.minecraft.jsonapi.dynamic.JSONAPIMethodProvider;

public class AdditionalJsonApiMethods implements JSONAPIMethodProvider {

	@API_Method(namespace = "store")
	public boolean addEnchantmentBook() {
		return false;
	}

	@API_Method(namespace = "store")
	public boolean addFireworkItem(String playerName, String samout, String sdamage, String name, String lore, String enchantments, String effects) {
		return false;
	}

	//	@API_Method(namespace = "store")
	//	public String addItem(String playerName, String sitemId) {
	//		return addItem(playerName, sitemId, "", "", "", "", "");
	//	}
	//
	//	@API_Method(namespace = "store")
	//	public String addItem(String playerName, String sitemId, String samount) {
	//		return addItem(playerName, sitemId, samount, "", "", "", "");
	//	}
	//
	//	@API_Method(namespace = "store")
	//	public String addItem(String playerName, String sitemId, String samount, String sdamage) {
	//		return addItem(playerName, sitemId, samount, sdamage, "", "", "");
	//	}
	//
	//	@API_Method(namespace = "store")
	//	public String addItem(String playerName, String sitemId, String samount, String sdamage, String name) {
	//		return addItem(playerName, sitemId, samount, sdamage, name, "", "");
	//	}
	//
	//	@API_Method(namespace = "store")
	//	public String addItem(String playerName, String sitemId, String samount, String sdamage, String name, String lore) {
	//		return addItem(playerName, sitemId, samount, sdamage, name, lore, "");
	//	}

	/**
	 * 
	 * @param playerName
	 *            self explaining...
	 * @param sitemId
	 *            the item id as String
	 * @param samount
	 *            the amount MAX 64! as String, could be empty
	 * @param sdamage
	 *            the damage value/durability value as String, could be empty
	 * @param name
	 *            the item's name to set, could be empty
	 * @param lore
	 *            the item's lore, "<newline>" will initiate a new line, could be empty
	 * @param enchantments
	 *            which enchantments should be added, must equal(in uppercase) the name of one enum element from "org.bukkit.enchantments.Enchantment"
	 * @return true if the item has been successfully added to the player's virtual inventory
	 */
	@API_Method(namespace = "store", argumentDescriptions = { "The name to indicate which player should get this item.", "The item id, I think that is self explaining!",
	        "How many items should be added?", "The data value/durability value", "a custom name", "a custem item description", "enchantments to add" })
	public String addItem(String playerName, String sitemId, String samount, String sdamage, String name, String lore, String enchantments) {
		int itemId;
		int amount;
		short damage;
		try {
			itemId = Integer.parseInt(sitemId);
		} catch (Exception e) {
			return "INVALID_ITEMID";
		}
		if (samount.isEmpty() || sdamage.equalsIgnoreCase(""))
			amount = 1;
		else
			try {
				amount = Integer.parseInt(samount);
			} catch (Exception e) {
				return "INVALID_AMOUNT";
			}
		if (sdamage.isEmpty() || sdamage.equalsIgnoreCase(""))
			damage = 0;
		else
			try {
				damage = Short.parseShort(sdamage);
			} catch (Exception e) {
				return "INVALID_DAMAGE_VALUE";
			}
		if (!(itemId > 0))
			return "INVALID_ITEMID";
		if (playerName != null && !playerName.isEmpty() && !playerName.equalsIgnoreCase("")) {
			if (!VirtualInventoryHandler.contains(String.format(VirtualInventory.INV_NAME, playerName)))
				new VirtualInventory(playerName);
			VirtualInventory vi = VirtualInventoryHandler.getVirtualInventory(String.format(VirtualInventory.INV_NAME, playerName));
			ItemStack item = new ItemStack(itemId);
			if (amount > 1)
				item.setAmount(amount);
			if (damage > -1)
				item.setDurability(damage);
			if (name != null && !name.isEmpty())
				item = ItemUtils.setItemName(item, ChatUtils.formatMessage(name));
			if (lore != null && !lore.isEmpty() && !lore.equalsIgnoreCase("")) {
				List<String> loreLines = new ArrayList<String>();
				for (String s : lore.split("<newline>"))
					loreLines.add(ChatUtils.formatMessage(s));
				item = ItemUtils.setItemLore(item, loreLines);
			}
			if (enchantments != null && !enchantments.isEmpty() && !enchantments.equalsIgnoreCase(""))
				try {
					item = ItemUtils.parseEnchantments(item, enchantments);
				} catch (Exception e) { //yes I know 'Pokemon Exception' - gotta catch them all
					return "INVALID_ENCHANTMENTS";
				}
			vi.addItem(item);
			vi.reload();
		} else
			return "INVALID_PLAYERNAME";
		return "SUCCESS";
	}

	private int executeCmd(String cmd, String playerName, long executionTime, int playerOnline) {
		if (executionTime <= System.currentTimeMillis())
			if (playerOnline == 0 || Bukkit.getPlayer(playerName) != null) {
				BukkitUtils.executeCommand(cmd);
				return 1;
			}
		return 0;
	}

	/**
	 * 
	 * @param playerName
	 *            self explaining...
	 * @param online
	 *            "true" will be converterted to boolean 'true', otherwise it will be false
	 * @param cmd
	 *            the cmd WITHOUT a leading "/"!
	 * @param longMinutes
	 *            the time in minutes, must be a string!
	 * @return true if everything was successful
	 */
	@API_Method(namespace = "store")
	public boolean executeCommand(String playerName, String online, String cmd, String longMinutes) {
		try {
			if (playerName.isEmpty() || playerName == null)
				throw new IllegalArgumentException("Player name cannot be empty!");

			if (cmd.isEmpty() || cmd == null)
				throw new IllegalArgumentException("Command cannot be empty!");

			long executionTime = System.currentTimeMillis() + Long.parseLong(longMinutes) * 60l * 1000L;
			int playerOnline = online.equalsIgnoreCase("true")? 1 : 0;

			PreparedStatement stmt = Connector.getDBCon().prepareStatement("INSERT INTO commands(playerName, commandName, executionTime, playerOnline, executed) values (?,?,?,?,?);");
			stmt.setString(1, playerName);
			stmt.setString(2, cmd);
			stmt.setLong(3, executionTime);
			stmt.setInt(4, playerOnline);
			stmt.setInt(5, executeCmd(cmd, playerName, executionTime, playerOnline));
			stmt.execute();
			stmt.close();

		} catch (Exception e) {
			Connector.logger().severe("Failed to store command, arguments: " + playerName + "," + online + "," + cmd + "," + longMinutes + " exception: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
