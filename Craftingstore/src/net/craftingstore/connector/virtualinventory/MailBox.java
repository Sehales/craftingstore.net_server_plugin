package net.craftingstore.connector.virtualinventory;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * A mailbox stores the items a player gets if he bought them online
 * 
 * @author Maddis
 * 
 */
public class MailBox {

	private Player   owner;
	private Location location;
	private World    world;

	public MailBox(Location mailBoxLocation, Player player) {
		this.location = mailBoxLocation;
		this.owner = player;
	}

	public VirtualInventory createVirtualInventory() {
		return new VirtualInventory(this.owner.getName());
	}

	public Location getLocation() {
		return location;
	}

	public Player getOwner() {
		return owner;
	}

	public World getWorld() {
		return world;
	}

}
