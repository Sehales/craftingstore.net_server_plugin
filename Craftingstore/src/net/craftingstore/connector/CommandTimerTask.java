package net.craftingstore.connector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.craftingstore.connector.util.BukkitUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandTimerTask implements Runnable {

	private static void checkForCommands() {

		try {
			String addStmt = ")";

			StringBuilder names = new StringBuilder();

			Player[] players = Bukkit.getOnlinePlayers();
			if (players.length > 0) {
				names.append(" OR playerName IN (");
				for (Player p : players) {
					names.append("'");
					names.append(p.getName());
					names.append("',");
				}

				names.delete(names.length() - 1, names.length());
				names.append("))");
				addStmt = names.toString();
			}
			String sql = "SELECT * FROM commands WHERE (executed = 0) AND (executionTime <= ?) AND (playerOnline = 0" + addStmt;

			if (Connector.debugMode)
				Connector.logger().info("DEBUG CmdTimerTask sql query 1: " + sql);
			PreparedStatement stmt = Connector.getDBCon().prepareStatement(sql);
			stmt.setLong(1, System.currentTimeMillis());

			List<Integer> ids = new ArrayList<Integer>();
			ResultSet result = stmt.executeQuery();

			if (result != null)
				while (result.next()) {
					int id = result.getInt("id");
					String command = result.getString("commandName");
					if (!BukkitUtils.executeCommand(command))
						Connector.logger().warning("Failed to execute command: " + command);
					if (Connector.debugMode)
						Connector.logger().info("DEBUG CmdTimerTask cmd: " + command + " id: " + id);
					ids.add(id);
				}

			stmt.close();

			if (ids.size() > 0) {
				StringBuilder sb = new StringBuilder();

				for (int i : ids) {
					sb.append("'");
					sb.append(i);
					sb.append("',");
				}
				sb.delete(sb.length() - 1, sb.length());

				Statement markStmt = Connector.getDBCon().createStatement();
				String markSql = "UPDATE commands SET `executed` = 1 WHERE `id` IN (" + sb.toString() + ");";
				if (Connector.debugMode)
					Connector.logger().info("DEBUG CmdTimerTask sql query 2: " + markSql);
				markStmt.execute(markSql);
				markStmt.close();
			}

		} catch (SQLException e) {
			Connector.logger().severe("SQLException! Error code: " + e.getErrorCode() + " Error message: " + e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		checkForCommands();
	}

}
