package net.craftingstore.connector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.craftingstore.connector.util.ChatUtils;
import net.craftingstore.connector.util.VersionUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MsgListener implements Listener {

	@EventHandler()
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (p.isOp() && !VersionUtils.isUpToDate()) {
			p.sendMessage(ChatColor.RED + "CraftingStoreConnector is out of date!");
			p.sendMessage(ChatColor.RED + "It is important to be up to date!");
			p.sendMessage(VersionUtils.downloadLink != null? ChatColor.RED + "see " + ChatColor.DARK_AQUA + VersionUtils.downloadLink + ChatColor.RED + " for recent versions!" : ChatColor.RED
			        + "No download information given, something has failed, you have to check them by yourself!");
		}

		if (p.isOp() || p.hasPermission("craftingstore.admin.notify")) {
			Statement stmt;
			try {
				stmt = Connector.getDBCon().createStatement();
				ResultSet result = stmt.executeQuery("SELECT * FROM messages WHERE `target` = 'op;' AND `isRead` = 0;");

				if (result != null)
					while (result.next()) {
						String message = result.getString("message");
						p.sendMessage(ChatUtils.formatMessage(message));
						setMessageRead("op;", result.getString("transactionId"));
					}

				stmt.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		PreparedStatement stmt;
		try {
			stmt = Connector.getDBCon().prepareStatement("SELECT * FROM messages WHERE `target` = ? AND `isRead` = 0;");
			stmt.setString(1, p.getName());
			ResultSet result = stmt.executeQuery();

			if (result != null)
				while (result.next()) {
					String message = result.getString("message");
					p.sendMessage(ChatUtils.formatMessage(message));
					setMessageRead(p.getName(), result.getString("transactionId"));
				}

			stmt.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	private void setMessageRead(String target, String transId) throws SQLException {
		Statement stmt = Connector.getDBCon().createStatement();
		stmt.execute("UPDATE messages SET `isRead` = '" + 1 + "'  WHERE `transactionId` = '" + transId + "' AND `target` = '" + target + "';");
		stmt.close();
	}
}
