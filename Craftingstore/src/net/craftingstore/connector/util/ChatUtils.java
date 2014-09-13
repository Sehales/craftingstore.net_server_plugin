package net.craftingstore.connector.util;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatUtils {

	/**
	 * format the message and replace color-codes/formating-codes
	 * 
	 * @param message
	 * @return formatted message
	 */
	public static String formatMessage(String message) {

		message = parseColorCodes(message);
		message = parseFormatCodes(message);
		message = replaceHTMLSpecialChars(message);

		return message;
	}

	/**
	 * format the message and replace color-codes/formating-codes also run String.format
	 * 
	 * @param message
	 * @param replacementArgs
	 *            Object array for String.format(message, args)
	 * @return formatted message
	 */
	public static String formatMessage(String message, Object[] replacementArgs) {

		message = String.format(message, replacementArgs);

		return formatMessage(message);
	}

	public static boolean notfiyOPs(String message) {
		for (Player p : Bukkit.getOnlinePlayers())
			if (p.isOp() || p.hasPermission("craftingstore.admin.notify")) {
				p.sendMessage(formatMessage(message));
				return true;
			}

		return false;
	}

	/**
	 * format the message and replace color-codes
	 * 
	 * @param format
	 * @return formatted string
	 */
	public static String parseColorCodes(String format) {
		format = format.replaceAll("<black>", ChatColor.BLACK.toString()).replaceAll("&0", ChatColor.BLACK.toString());
		format = format.replaceAll("<darkblue>", ChatColor.DARK_BLUE.toString()).replaceAll("&1", ChatColor.DARK_BLUE.toString());
		format = format.replaceAll("<darkgreen>", ChatColor.DARK_GREEN.toString()).replaceAll("&2", ChatColor.DARK_GREEN.toString());
		format = format.replaceAll("<darkaqua>", ChatColor.DARK_AQUA.toString()).replaceAll("&3", ChatColor.DARK_AQUA.toString());
		format = format.replaceAll("<darkred>", ChatColor.DARK_RED.toString()).replaceAll("&4", ChatColor.DARK_RED.toString());
		format = format.replaceAll("<darkpurple>", ChatColor.DARK_PURPLE.toString()).replaceAll("&5", ChatColor.DARK_PURPLE.toString());
		format = format.replaceAll("<gold>", ChatColor.GOLD.toString()).replaceAll("&6", ChatColor.GOLD.toString());
		format = format.replaceAll("<grey>", ChatColor.GRAY.toString()).replaceAll("<gray>", ChatColor.GRAY.toString()).replaceAll("&7", ChatColor.GRAY.toString());
		format = format.replaceAll("<darkgray>", ChatColor.DARK_GRAY.toString()).replaceAll("&8", ChatColor.DARK_GRAY.toString());
		format = format.replaceAll("<blue>", ChatColor.BLUE.toString()).replaceAll("&9", ChatColor.BLUE.toString());
		format = format.replaceAll("<green>", ChatColor.GREEN.toString()).replaceAll("&a", ChatColor.GREEN.toString());
		format = format.replaceAll("<aqua>", ChatColor.AQUA.toString()).replaceAll("&b", ChatColor.AQUA.toString());
		format = format.replaceAll("<red>", ChatColor.RED.toString()).replaceAll("&c", ChatColor.RED.toString());
		format = format.replaceAll("<purple>", ChatColor.LIGHT_PURPLE.toString()).replaceAll("&d", ChatColor.LIGHT_PURPLE.toString());
		format = format.replaceAll("<yellow>", ChatColor.YELLOW.toString()).replaceAll("&e", ChatColor.YELLOW.toString());
		format = format.replaceAll("<white>", ChatColor.WHITE.toString()).replaceAll("&f", ChatColor.WHITE.toString());
		return format;
	}

	/**
	 * format the message and replace formatting-codes
	 * 
	 * @param format
	 * @return formatted string
	 */
	public static String parseFormatCodes(String format) {
		format = format.replaceAll("<bold>", ChatColor.BOLD.toString()).replaceAll("&l", ChatColor.BOLD.toString());
		format = format.replaceAll("<italic>", ChatColor.ITALIC.toString()).replaceAll("&o", ChatColor.ITALIC.toString());
		format = format.replaceAll("<magic>", ChatColor.MAGIC.toString()).replaceAll("&k", ChatColor.MAGIC.toString());
		format = format.replaceAll("<strikethrough>", ChatColor.STRIKETHROUGH.toString()).replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
		format = format.replaceAll("<underline>", ChatColor.UNDERLINE.toString()).replaceAll("&n", ChatColor.UNDERLINE.toString());
		format = format.replaceAll("<normal>", ChatColor.RESET.toString()).replaceAll("&r", ChatColor.RESET.toString()).replaceAll("<resettext>", ChatColor.RESET.toString());
		return format;
	}

	public static String replaceHTMLSpecialChars(String format) {
		format = format.replaceAll("&amp;", "&");
		format = format.replaceAll("&quot;", "\"");
		format = format.replaceAll("&gt;", ">");
		format = format.replaceAll("&lt;", "<");
		return format;
	}

	public static void sendFormattedMessage(CommandSender target, List<String> message) {
		for (String s : message)
			target.sendMessage(formatMessage(s));
	}
}
