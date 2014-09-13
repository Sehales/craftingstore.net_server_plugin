package net.craftingstore.connector.util;

import java.util.List;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {

	/**
	 * add an enchantment to this item
	 * 
	 * @param item
	 * @param enchantment
	 * @param level
	 * @param ignoreRestrictions
	 * @return the enchanted item
	 */
	public static ItemStack addItemEnchantment(ItemStack item, Enchantment enchantment, int level, boolean ignoreRestrictions) {
		ItemMeta metaData = item.getItemMeta();
		metaData.addEnchant(enchantment, level, ignoreRestrictions);
		item.setItemMeta(metaData);
		return item;
	}

	public static ItemStack parseEnchantments(ItemStack item, String enchantments) {
		for (String e : enchantments.split(",")) {
			String[] rawEnchantment = e.split(":");
			Enchantment enchantment = Enchantment.getByName(rawEnchantment[0]);
			int level = Integer.parseInt(rawEnchantment[1]);
			item = ItemUtils.addItemEnchantment(item, enchantment, level, true);
		}
		return item;
	}

	/**
	 * set the lore(description) of an item each element of the List<String> represents one line
	 * 
	 * @param item
	 * @param test
	 * @return the changed item
	 */
	public static ItemStack setItemLore(ItemStack item, List<String> loreLines) {
		ItemMeta metaData = item.getItemMeta();
		metaData.setLore(loreLines);
		item.setItemMeta(metaData);
		return item;
	}

	/**
	 * set the display name of an item
	 * 
	 * @param item
	 * @param name
	 * @return the changed item
	 */
	public static ItemStack setItemName(ItemStack item, String name) {
		ItemMeta metaData = item.getItemMeta();
		metaData.setDisplayName(name);
		item.setItemMeta(metaData);
		return item;
	}
}
