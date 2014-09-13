package net.craftingstore.connector.virtualinventory;

import java.util.HashMap;
import java.util.Map;

public class VirtualInventoryHandler {

	private static Map<String, VirtualInventory> invMap = new HashMap<String, VirtualInventory>();

	static void addVirtualInventory(VirtualInventory inv) {
		if (!contains(inv.getName()) && !invMap.containsValue(inv))
			invMap.put(inv.getName(), inv);
	}

	public static boolean contains(String name) {
		return invMap.containsKey(name);
	}

	public static VirtualInventory getVirtualInventory(String name) {
		if (contains(name)) {
			VirtualInventory inv = invMap.get(name);
			//inv.loadInternalInventory(serializedObject)
			return inv;
		} else
			return null;
	}

	static void remove(String name) {
		if (contains(name)) {
			getVirtualInventory(name).save();
			invMap.remove(name);
		}
	}
}
