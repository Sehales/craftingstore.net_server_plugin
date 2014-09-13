package net.craftingstore.connector.virtualinventory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import net.craftingstore.connector.Connector;
import net.craftingstore.connector.util.ChatUtils;
import net.craftingstore.connector.util.ObjectUtils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class VirtualInventory implements Serializable {

	public static final String    INV_NAME          = ChatUtils.formatMessage("<gold>Store Mailbox (%s)");
	private static final long     serialVersionUID  = 8709451331152982871L;
	private String                name;                                                                   // internal name
	private LinkedList<ItemStack> extendedInventory = new LinkedList<ItemStack>();
	private transient Inventory   internalInventory;                                                      //we can't serialize it so we don't do it
	private String                owner;
	private final int             MAX_SIZE          = 54;
	private int                   size              = MAX_SIZE;
	private transient Connection  con               = Connector.getDBCon();

	/**
	 * create a virtual inventory with an internal inventory size of 54 or load an old one
	 * 
	 * @param player
	 *            the inventory's owner
	 */
	public VirtualInventory(String playerName) {
		setName(String.format(INV_NAME, playerName));
		setOwner(playerName);
		createVirtualInventory(true);
		load(false);
		VirtualInventoryHandler.addVirtualInventory(this);

	}

	/**
	 * this will create a new inventory
	 * 
	 * @param player
	 *            the inventory's owner
	 * @param size
	 *            the size of the displaying inventory, must be a multiple of 9, max 54
	 */
	public VirtualInventory(String playerName, int size) {
		setName(String.format(INV_NAME, playerName));
		setOwner(playerName);
		this.setSize(size);
		createVirtualInventory(true);
		load(false);
		VirtualInventoryHandler.addVirtualInventory(this);

	}

	public void addItem(ItemStack item) {
		synchronized (extendedInventory) {
			extendedInventory.addLast(item);
			restockInternalInventory();
		}
	}

	boolean createVirtualInventory(boolean force) {
		if (!force && internalInventory != null)
			return false;

		if (getSize() > MAX_SIZE)
			setSize(MAX_SIZE);
		internalInventory = Bukkit.createInventory(null, getSize(), name);
		return true;
	}

	/**
	 * @return the internalInventory
	 */
	public Inventory getInternalInventory() {
		return internalInventory;
	}

	/**
	 * get the name of that VirtualInventory
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * get the name of the owner of the internal inventory
	 * 
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	void insert() {
		try {
			PreparedStatement stmt = con.prepareStatement("INSERT INTO inventories values(?,?,?);");
			stmt.setString(1, this.name);
			stmt.setInt(2, this.size);
			stmt.setString(3, saveInternalInventory());
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void load(boolean forceInsert) {
		Statement stmt;
		try {
			stmt = con.createStatement();
			ResultSet result = stmt.executeQuery("SELECT * FROM inventories WHERE `name` = '" + this.name + "';");
			if (!forceInsert && result != null && result.next())
				loadInternalInventory(result.getString(3));
			else
				insert();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			insert();
		}

	}

	@SuppressWarnings("unchecked")
	void loadInternalInventory(String serializedObject) {
		synchronized (extendedInventory) {
			LinkedList<ItemStackInfo> tmpMap;
			if ((tmpMap = (LinkedList<ItemStackInfo>) ObjectUtils.deserializeFromString(serializedObject)) != null && !tmpMap.isEmpty())
				this.extendedInventory = new LinkedList<ItemStack>();
			for (ItemStackInfo i : tmpMap)
				this.extendedInventory.addLast(i.createItemStack());
			if (Connector.debugMode) {
				System.out.println(serializedObject);
				System.out.println(extendedInventory.toString());
			}
		}
	}

	public void open(Player player) {
		player.openInventory(getInternalInventory());
	}

	public void reload() {
		save();
		load(false);
	}

	public void remove() {
		VirtualInventoryHandler.remove(this.name);
	}

	public void restockInternalInventory() {
		synchronized (extendedInventory) {
			while (internalInventory.firstEmpty() != -1 && !extendedInventory.isEmpty()) {
				ItemStack stack;
				for (int i = 0; i < this.size; i++)
					if (internalInventory.getItem(i) == null)
						if ((stack = extendedInventory.pollFirst()) != null) {
							internalInventory.addItem(stack);
							if (Connector.debugMode)
								System.out.println(internalInventory.getContents().length);
						}
			}
		}
	}

	void save() {
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate("UPDATE inventories SET `serializedInv` = '" + saveInternalInventory() + "', `size` = '" + this.size + "' WHERE `name` = '" + this.name + "';");
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	String saveInternalInventory() {
		synchronized (extendedInventory) {
			if (internalInventory != null && internalInventory.getContents().length > 0)
				for (ItemStack stack : internalInventory.getContents())
					if (stack != null && !stack.getType().equals(Material.AIR)) {
						extendedInventory.addFirst(stack);
						internalInventory.remove(stack);
					}

			internalInventory.clear();
			LinkedList<ItemStackInfo> serializedInventory = new LinkedList<ItemStackInfo>();
			for (ItemStack item : extendedInventory)
				serializedInventory.addLast(ItemStackInfo.createStackInfo(item));
			String serializedObject = ObjectUtils.serializeToString(serializedInventory);
			if (Connector.debugMode)
				System.out.println(serializedObject);
			return serializedObject;
		}
	}

	/**
	 * change the name of that inventory
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * set the owner of the internal inventory
	 * 
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}
}
