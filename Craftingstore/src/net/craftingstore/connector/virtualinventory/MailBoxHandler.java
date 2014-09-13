package net.craftingstore.connector.virtualinventory;

import java.util.LinkedList;

import org.bukkit.Location;

public class MailBoxHandler {
	 static LinkedList<MailBox> boxes = new LinkedList<MailBox>();
	
	 /** Location should always represent the exact block location, so no values behind the comma! */
	public static MailBox getMailBox(Location location) throws NoMailBoxFoundException {
		for(MailBox box: boxes){
			if (box.getLocation().getBlock().getLocation().equals(location.getBlock().getLocation())) {
				return box;
			}
		}
		throw new NoMailBoxFoundException();
	}
	public static  boolean addMailBox(MailBox mailBox){
		return boxes.add(mailBox);
	}
	public static  boolean removeMailBox(MailBox mailBox){
		return boxes.remove(mailBox);
	}
	public static int getSize(){
		return boxes.size();
	}
	public static String getListString(){
		return "Boxes: Size:"+boxes.size()+" "+boxes.get(0).getLocation().toString();
	}
	
	
}
