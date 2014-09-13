package net.craftingstore.connector.virtualinventory;

import java.util.HashMap;
import java.util.Map;

import net.craftingstore.connector.Connector;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

	private static Map<Player, ItemStack> lastItemMap = new HashMap<Player, ItemStack>();

	private static ItemStack getLastItem(Player p) {
		if (!lastItemMap.containsKey(p))
			lastItemMap.put(p, new ItemStack(Material.AIR));
		return lastItemMap.get(p);

	}

	private void cancelInvClickEvent(InventoryClickEvent e) {
		e.setCursor(e.getCursor());
		e.setCurrentItem(e.getCurrentItem());
		e.setCancelled(true);
		e.setResult(Result.DENY);
	}

	@EventHandler()
	public void onInventoryClick(InventoryClickEvent e) {
		if (VirtualInventoryHandler.contains(e.getInventory().getName())) {
			Player p = (Player) e.getWhoClicked();
			ItemStack cursor = e.getCursor();
			ItemStack current = e.getCurrentItem();
			int rawSlot = e.getRawSlot();
			VirtualInventory vi = VirtualInventoryHandler.getVirtualInventory(e.getInventory().getName());
			if (rawSlot < vi.getSize() && !cursor.getType().equals(Material.AIR) && !cursor.equals(getLastItem(p))) {
				cancelInvClickEvent(e);
				return;
			}
			if (rawSlot >= vi.getSize() && e.isShiftClick()) {
				cancelInvClickEvent(e);
				return;
			}
			if (rawSlot < vi.getSize())
				lastItemMap.put(p, current);
			if (Connector.debugMode)
				System.out.println(String.format("slot type: %s, current item: %s, item on cursor: %s, raw slot id: %s, slot id: %s", e.getSlotType(), current, cursor, e.getRawSlot(), e.getSlot()));
			vi.restockInternalInventory();
		}
	}

	@EventHandler()
	public void onInventoryClose(InventoryCloseEvent e) {
		if (VirtualInventoryHandler.contains(e.getInventory().getName()))
			VirtualInventoryHandler.getVirtualInventory(e.getInventory().getName()).save();
	}

	@EventHandler()
	public void onInventoryOpen(InventoryOpenEvent e) {
		if (VirtualInventoryHandler.contains(e.getInventory().getName())) {
			VirtualInventory vi = VirtualInventoryHandler.getVirtualInventory(e.getInventory().getName());
			vi.load(false);
			vi.restockInternalInventory();
		}
	}
}
