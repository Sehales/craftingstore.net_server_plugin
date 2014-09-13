package net.craftingstore.connector.virtualinventory;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class MailBoxMetaValue implements MetadataValue {

	Plugin plugin;
	Object data;
	
	public MailBoxMetaValue(Plugin plugin, Object data){
		this.plugin = plugin;
		this.data   = data;
	}
	public void set(Object obj) {
		this.data = obj;
	}
	
	@Override
	public boolean asBoolean() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte asByte() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double asDouble() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float asFloat() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int asInt() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long asLong() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short asShort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String asString() {
		// TODO Auto-generated method stub
		return (String) data;
	}

	@Override
	public Plugin getOwningPlugin() {
		return plugin;
	}

	@Override
	public void invalidate() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object value() {
		return data;
	}

}
