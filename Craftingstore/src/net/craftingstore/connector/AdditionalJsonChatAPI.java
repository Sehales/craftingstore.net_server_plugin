package net.craftingstore.connector;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.craftingstore.connector.util.ChatUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.alecgorge.minecraft.jsonapi.dynamic.API_Method;
import com.alecgorge.minecraft.jsonapi.dynamic.JSONAPIMethodProvider;

public class AdditionalJsonChatAPI implements JSONAPIMethodProvider {

	@API_Method(namespace = "store")
	public boolean sendMessage(String target, String message, String transactionId) {
		long tId;
		try {
			tId = Long.parseLong(transactionId);
		} catch (NumberFormatException e) {
			Connector.logger().severe("store.sendMessage() cannot parse transactionId String to Long, reporting this error is really important!");
			return false;
		}

		int isRead = 0;

		if (target.equalsIgnoreCase("op;")) {
			Connector.logger().info(message);
			if (ChatUtils.notfiyOPs(message))
				isRead = 1;
		} else {
			Player p = Bukkit.getPlayer(target);
			if (p != null) {
				p.sendMessage(ChatUtils.formatMessage(message));
				isRead = 1;
			}
		}
		try {
			PreparedStatement stmt = Connector.getDBCon().prepareStatement("INSERT INTO messages values(?,?,?,?);");
			stmt.setString(1, target);
			stmt.setString(2, message);
			stmt.setLong(3, tId);
			stmt.setInt(4, isRead);
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
