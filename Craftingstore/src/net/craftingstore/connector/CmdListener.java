package net.craftingstore.connector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.craftingstore.connector.util.BukkitUtils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class CmdListener implements Listener {

	@EventHandler()
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		try {
			String sql = "SELECT * FROM commands WHERE (`executed` = 0) AND (`executionTime` <= ?) AND (`playerName` = ?);";
			PreparedStatement stmt = Connector.getDBCon().prepareStatement(sql);
			stmt.setLong(1, System.currentTimeMillis());
			stmt.setString(2, p.getName());
			stmt.execute();
			ResultSet result = stmt.getResultSet();
			stmt.close();

			List<Integer> ids = new ArrayList<Integer>();
			if (result != null)
				while (result.next()) {
					if (Connector.debugMode)
						Connector.logger().info("DEBUG CmdListener cmd: " + result.getString("commandName") + " id: " + result.getInt("ROWID"));
					BukkitUtils.executeCommand(result.getString("commandName"));
					int id = result.getInt("id");
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
					Connector.logger().info("DEBUG CmdListener sql query 2 " + markSql);
				markStmt.execute(markSql);
				markStmt.close();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
