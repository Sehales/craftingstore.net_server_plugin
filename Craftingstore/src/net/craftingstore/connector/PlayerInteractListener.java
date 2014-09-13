package net.craftingstore.connector;

import net.craftingstore.connector.util.ChatUtils;
import net.craftingstore.connector.virtualinventory.MailBox;
import net.craftingstore.connector.virtualinventory.MailBoxHandler;
import net.craftingstore.connector.virtualinventory.NoMailBoxFoundException;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

public class PlayerInteractListener implements Listener {
	private Plugin plugin;
	public PlayerInteractListener(Plugin plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e){
		if (!e.isCancelled() && e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().hasMetadata("craftingstore_mailbox")){
			e.getPlayer().sendMessage("YAY!");
		}
		if(!e.isCancelled() && e.getAction() == Action.RIGHT_CLICK_BLOCK && plugin.getConfig().getBoolean("mailbox.enable", true) 
				&& e.getClickedBlock().getTypeId() == plugin.getConfig().getInt("mailbox.blockId",130)){
			
			Block clickedBlock = e.getClickedBlock();
			
			try{
				MailBox mailBox = MailBoxHandler.getMailBox(clickedBlock.getLocation());
				mailBox.createVirtualInventory().open(e.getPlayer());
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatUtils.formatMessage(plugin.getConfig().getString("lang.openend-mailbox","<gold>You opened your store mailbox")));
				return;
			} catch (NoMailBoxFoundException exception){
			}
			
			//DOUBLE CHEST FIX:
			if (e.getClickedBlock().getType().equals(Material.CHEST)){
				Block blockToCheck=null;
				if(clickedBlock.getLocation().add(-1, 0, 0).getBlock().getType().equals(Material.CHEST)){
					blockToCheck = clickedBlock.getLocation().add(-1, 0, 0).getBlock();
				} else if(clickedBlock.getLocation().add(+1, 0, 0).getBlock().getType().equals(Material.CHEST)){
					blockToCheck = clickedBlock.getLocation().add(-1, 0, 0).getBlock();
				} else if(clickedBlock.getLocation().add(0, 0, -1).getBlock().getType().equals(Material.CHEST)){
					blockToCheck = clickedBlock.getLocation().add(-1, 0, 0).getBlock();
				} else if(clickedBlock.getLocation().add(0, 0, +1).getBlock().getType().equals(Material.CHEST)){
					blockToCheck = clickedBlock.getLocation().add(-1, 0, 0).getBlock();
				}
				if (blockToCheck != null){

					try{
						MailBox mailBox = MailBoxHandler.getMailBox(blockToCheck.getLocation());
						mailBox.createVirtualInventory().open(e.getPlayer());
						e.setCancelled(true);
						e.getPlayer().sendMessage(ChatUtils.formatMessage(plugin.getConfig().getString("lang.openend-mailbox","<gold>You opened your store mailbox")));
						return;
					} catch (NoMailBoxFoundException exception){
					}
				}
			}
			
		}

		
		
	}
}
