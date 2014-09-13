package net.craftingstore.connector.virtualinventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.base.Strings;

public class ItemStackInfo implements Serializable {

	public enum MetaType {
		SKULL, MAP, POTION, LEATHER_ARMOR, ENCHANTMENT_STORAGE, FIREWORK, FIREWORK_EFFECT, NORMAL, BOOK
	}

	private static final long serialVersionUID = -623853395611404749L;

	public static ItemStackInfo createStackInfo(ItemStack stack) {

		ItemMeta meta = stack.getItemMeta();
		ItemStackInfo info = new ItemStackInfo();
		info.setMetaType(MetaType.NORMAL);
		info.setItemId(stack.getTypeId());
		info.setAmount(stack.getAmount());
		info.setDamageValue(stack.getDurability());
		if (meta.hasDisplayName())
			info.setName(meta.getDisplayName());
		if (meta.hasLore())
			info.setLore(meta.getLore());
		if (meta.hasEnchants())
			info.setEnchantments(meta.getEnchants());
		if (meta instanceof PotionMeta) {
			info.setMetaType(MetaType.POTION);
			PotionMeta pMeta = (PotionMeta) meta;
			if (pMeta.hasCustomEffects())
				info.setCustomPotionEffects(pMeta.getCustomEffects());
		} else if (meta instanceof SkullMeta) {
			info.setMetaType(MetaType.SKULL);
			SkullMeta sMeta = (SkullMeta) meta;
			if (sMeta.hasOwner())
				info.setOwner(sMeta.getOwner());
		} else if (meta instanceof LeatherArmorMeta) {
			info.setMetaType(MetaType.LEATHER_ARMOR);
			LeatherArmorMeta lMeta = (LeatherArmorMeta) meta;
			info.setColor(lMeta.getColor());
		} else if (meta instanceof MapMeta) {
			info.setMetaType(MetaType.MAP);
			MapMeta mMeta = (MapMeta) meta;
			info.setScaling(mMeta.isScaling());
		} else if (meta instanceof BookMeta) {
			info.setMetaType(MetaType.BOOK);
			BookMeta bMeta = (BookMeta) meta;
			if (bMeta.hasAuthor())
				info.setAuthor(bMeta.getAuthor());
			if (bMeta.hasPages())
				info.setPages(bMeta.getPages());
			if (bMeta.hasTitle())
				info.setTitle(bMeta.getTitle());
		} else if (meta instanceof FireworkMeta) {
			info.setMetaType(MetaType.FIREWORK);
			FireworkMeta fMeta = (FireworkMeta) meta;
			if (fMeta.hasEffects())
				info.setFireworkEffects(fMeta.getEffects());
			info.setPower(fMeta.getPower());
		} else if (meta instanceof FireworkEffectMeta) {
			FireworkEffectMeta feMeta = (FireworkEffectMeta) meta;
			info.setMetaType(MetaType.FIREWORK_EFFECT);
			if (feMeta.hasEffect())
				info.setFireworkEffect(feMeta.getEffect());
		} else if (meta instanceof EnchantmentStorageMeta) {
			EnchantmentStorageMeta eMeta = (EnchantmentStorageMeta) meta;
			info.setMetaType(MetaType.ENCHANTMENT_STORAGE);
			if (eMeta.hasStoredEnchants())
				info.setStoredEnchantments(eMeta.getStoredEnchants());
		}
		if (meta instanceof Repairable) {
			Repairable rMeta = (Repairable) meta;
			info.setRepairable(true);
			info.setRepairCost(rMeta.getRepairCost());
		}

		return info;
	}

	private MetaType                  metatype;
	private int                       amount;

	private String                    author;

	private short                     damage;

	private List<String>              loreLines;
	private String                    displayName;
	private String                    owner;
	private List<String>              pages;
	private int                       repaircost;
	private boolean                   scaling;
	private int                       itemId;
	private String                    title;
	private int                       rgb = 0;
	private List<Map<String, Object>> potionEffects;
	private Map<String, Object>       fireworkEffect;
	private List<Map<String, Object>> fireworkEffects;
	private Map<String, Integer>      enchantments;
	private Map<String, Integer>      storedEnchantments;
	private boolean                   repairable;
	private int                       power;

	private ItemStackInfo() {

	}

	public ItemStack createItemStack() {
		ItemStack item = new ItemStack(itemId, amount, damage);
		switch (metatype) {
			case SKULL: {
				SkullMeta sMeta = (SkullMeta) item.getItemMeta();
				if (hasOwner())
					sMeta.setOwner(getOwner());
				item.setItemMeta(sMeta);
				break;
			}
			case MAP: {
				MapMeta mMeta = (MapMeta) item.getItemMeta();
				mMeta.setScaling(isScaling());
				item.setItemMeta(mMeta);
				break;
			}
			case POTION: {
				PotionMeta pMeta = (PotionMeta) item.getItemMeta();
				if (hasCustomPotionEffects())
					for (PotionEffect e : getCustomPotionEffects())
						pMeta.addCustomEffect(e, true);
				item.setItemMeta(pMeta);
				break;
			}
			case LEATHER_ARMOR: {
				LeatherArmorMeta lMeta = (LeatherArmorMeta) item.getItemMeta();
				if (hasColor())
					lMeta.setColor(getColor());
				item.setItemMeta(lMeta);
				break;
			}
			case ENCHANTMENT_STORAGE: {
				EnchantmentStorageMeta sMeta = (EnchantmentStorageMeta) item.getItemMeta();
				if (hasStoredEnchantments()) {
					Map<Enchantment, Integer> m = getStoredEnchantments();
					for (Enchantment e : m.keySet())
						sMeta.addStoredEnchant(e, m.get(e), true);
				}
				item.setItemMeta(sMeta);
				break;
			}
			case FIREWORK: {
				FireworkMeta fMeta = (FireworkMeta) item.getItemMeta();
				fMeta.setPower(getPower());
				if (hasFireworkEffects())
					for (FireworkEffect e : getFireworkEffects())
						fMeta.addEffect(e);
				item.setItemMeta(fMeta);
				break;
			}
			case FIREWORK_EFFECT: {
				FireworkEffectMeta eMeta = (FireworkEffectMeta) item.getItemMeta();
				if (hasFireworkEffect())
					eMeta.setEffect(getFireworkEffect());
				item.setItemMeta(eMeta);
				break;
			}
			case BOOK: {
				BookMeta bMeta = (BookMeta) item.getItemMeta();
				if (hasAuthor())
					bMeta.setAuthor(getAuthor());
				if (hasTitle())
					bMeta.setTitle(getTitle());
				if (hasPages())
					bMeta.setPages(getPages());
				item.setItemMeta(bMeta);
				break;
			}
		default:
			break;
		}
		ItemMeta meta = item.getItemMeta();
		if (hasRepairCost())
			((Repairable) meta).setRepairCost(getRepairCost());
		if (hasLore())
			meta.setLore(getLore());
		if (hasDisplayName())
			meta.setDisplayName(getDisplayName());
		if (hasEnchantments()) {
			Map<Enchantment, Integer> m = getEnchantments();
			for (Enchantment e : m.keySet())
				meta.addEnchant(e, m.get(e), true);
		}
		item.setItemMeta(meta);

		return item;
	}

	public int getAmount() {
		return this.amount;
	}

	public String getAuthor() {
		return this.author;
	}

	public Color getColor() {
		return Color.fromRGB(rgb);
	}

	public List<PotionEffect> getCustomPotionEffects() {
		List<PotionEffect> effects = new ArrayList<PotionEffect>();
		for (Map<String, Object> m : potionEffects) {
			PotionEffectType t = PotionEffectType.getByName((String) m.get("type"));
			int d = (Integer) m.get("duration");
			int a = (Integer) m.get("amplifier");
			boolean am = (Boolean) m.get("ambient");
			effects.add(new PotionEffect(t, d, a, am));
		}
		return effects;
	}

	public short getDamageValue() {
		return this.damage;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public Map<Enchantment, Integer> getEnchantments() {
		if (hasEnchantments()) {
			Map<Enchantment, Integer> result = new HashMap<Enchantment, Integer>();
			for (String s : enchantments.keySet())
				result.put(Enchantment.getByName(s), enchantments.get(s));
			return result;
		} else
			return null;
	}

	@SuppressWarnings("unchecked")
	public FireworkEffect getFireworkEffect() {
		if (hasFireworkEffect()) {
			Builder builder = FireworkEffect.builder();
			builder.flicker((Boolean) fireworkEffect.get("flicker"));
			builder.trail((Boolean) fireworkEffect.get("trail"));
			builder.with((Type) fireworkEffect.get("type"));
			List<Color> colors = new ArrayList<Color>();
			for (int c : (List<Integer>) fireworkEffect.get("colors"))
				colors.add(Color.fromRGB(c));
			builder.withColor(colors);
			List<Color> fadecolors = new ArrayList<Color>();
			for (int c : (List<Integer>) fireworkEffect.get("fade-colors"))
				fadecolors.add(Color.fromRGB(c));
			builder.withFade(fadecolors);

			return builder.build();
		} else
			return null;
	}

	@SuppressWarnings("unchecked")
	public List<FireworkEffect> getFireworkEffects() {
		if (hasFireworkEffects()) {

			List<FireworkEffect> result = new ArrayList<FireworkEffect>();

			for (Map<String, Object> m : fireworkEffects) {
				Builder builder = FireworkEffect.builder();
				builder.flicker((Boolean) m.get("flicker"));
				builder.trail((Boolean) m.get("trail"));
				builder.with((Type) m.get("type"));
				List<Color> colors = new ArrayList<Color>();
				for (int c : (List<Integer>) m.get("colors"))
					colors.add(Color.fromRGB(c));
				builder.withColor(colors);
				List<Color> fadecolors = new ArrayList<Color>();
				for (int c : (List<Integer>) m.get("fade-colors"))
					fadecolors.add(Color.fromRGB(c));
				builder.withFade(fadecolors);
				result.add(builder.build());
			}
			return result;
		} else
			return null;
	}

	public int getItemId() {
		return this.itemId;
	}

	public List<String> getLore() {
		return this.loreLines;
	}

	public MetaType getMetaType() {
		return this.metatype;
	}

	public String getOwner() {
		return this.owner;
	}

	public List<String> getPages() {
		return this.pages;
	}

	public int getPower() {
		return this.power;
	}

	public int getRepairCost() {
		return this.repaircost;
	}

	public Map<Enchantment, Integer> getStoredEnchantments() {
		if (hasStoredEnchantments()) {
			Map<Enchantment, Integer> result = new HashMap<Enchantment, Integer>();
			for (String s : storedEnchantments.keySet())
				result.put(Enchantment.getByName(s), storedEnchantments.get(s));
			return result;
		} else
			return null;
	}

	public String getTitle() {
		return this.title;
	}

	public boolean hasAuthor() {
		return !Strings.isNullOrEmpty(author);
	}

	public boolean hasColor() {
		return rgb != 0;
	}

	public boolean hasCustomPotionEffects() {
		return potionEffects != null;
	}

	public boolean hasDisplayName() {
		return !Strings.isNullOrEmpty(displayName);
	}

	public boolean hasEnchantments() {
		return enchantments != null;
	}

	public boolean hasFireworkEffect() {
		return fireworkEffect != null;
	}

	public boolean hasFireworkEffects() {
		return fireworkEffects != null;
	}

	public boolean hasLore() {
		return loreLines != null;
	}

	public boolean hasOwner() {
		return !Strings.isNullOrEmpty(owner);
	}

	public boolean hasPages() {
		return this.pages != null;
	}

	public boolean hasRepairCost() {
		return this.repairable;
	}

	public boolean hasStoredEnchantments() {
		return storedEnchantments != null;
	}

	public boolean hasTitle() {
		return !Strings.isNullOrEmpty(title);
	}

	public boolean isScaling() {
		return this.scaling;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setColor(Color color) {
		rgb = color.asRGB();
	}

	public void setCustomPotionEffects(List<PotionEffect> customEffects) {
		potionEffects = new ArrayList<Map<String, Object>>();
		for (PotionEffect e : customEffects) {
			Map<String, Object> values = new HashMap<String, Object>();
			values.put("type", e.getType().getName());
			values.put("duration", e.getDuration());
			values.put("amplifier", e.getAmplifier());
			values.put("ambient", e.isAmbient());
			potionEffects.add(values);
		}
		System.out.println(potionEffects);
	}

	public void setDamageValue(short durability) {
		this.damage = durability;

	}

	public void setEnchantments(Map<Enchantment, Integer> enchants) {
		this.enchantments = new HashMap<String, Integer>();
		for (Enchantment e : enchants.keySet())
			enchantments.put(e.getName(), enchants.get(e));
	}

	public void setFireworkEffect(FireworkEffect effect) {
		this.fireworkEffect = new HashMap<String, Object>();
		fireworkEffect.put("flicker", effect.hasFlicker());
		fireworkEffect.put("trail", effect.hasTrail());
		fireworkEffect.put("type", effect.getType());
		List<Integer> colors = new ArrayList<Integer>();
		for (Color c : effect.getColors())
			colors.add(c.asRGB());
		fireworkEffect.put("colors", colors);
		List<Integer> fadeColors = new ArrayList<Integer>();
		for (Color c : effect.getFadeColors())
			fadeColors.add(c.asRGB());
		fireworkEffect.put("fade-colors", fadeColors);
	}

	public void setFireworkEffects(List<FireworkEffect> effects) {
		this.fireworkEffects = new ArrayList<Map<String, Object>>();
		for (FireworkEffect e : effects) {
			Map<String, Object> effect = new HashMap<String, Object>();
			effect.put("flicker", e.hasFlicker());
			effect.put("trail", e.hasTrail());
			effect.put("type", e.getType());
			List<Integer> colors = new ArrayList<Integer>();
			for (Color c : e.getColors())
				colors.add(c.asRGB());
			effect.put("colors", colors);
			List<Integer> fadeColors = new ArrayList<Integer>();
			for (Color c : e.getFadeColors())
				fadeColors.add(c.asRGB());
			effect.put("fade-colors", fadeColors);
			this.fireworkEffects.add(effect);
		}

	}

	public void setItemId(int itemId) {
		this.itemId = itemId;

	}

	public void setLore(List<String> lore) {
		this.loreLines = lore;
	}

	public void setMetaType(MetaType type) {
		this.metatype = type;
	}

	public void setName(String displayName) {
		this.displayName = displayName;

	}

	public void setOwner(String owner) {
		this.owner = owner;

	}

	public void setPages(List<String> pages) {
		this.pages = pages;

	}

	public void setPower(int power) {
		this.power = power;

	}

	public void setRepairable(boolean repairable) {
		this.repairable = repairable;

	}

	public void setRepairCost(int repaircost) {
		this.repaircost = repaircost;

	}

	public void setScaling(boolean scaling) {
		this.scaling = scaling;

	}

	private void setStoredEnchantments(Map<Enchantment, Integer> storedEnchants) {
		this.storedEnchantments = new HashMap<String, Integer>();
		for (Enchantment e : storedEnchants.keySet())
			storedEnchantments.put(e.getName(), storedEnchants.get(e));

	}

	public void setTitle(String title) {
		this.title = title;

	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("type: " + getMetaType());
		b.append(", id: " + getItemId());
		b.append(", amount: " + getAmount());
		b.append(", damage: " + getDamageValue());
		b.append(", name: " + getDisplayName());
		b.append(", lore: " + getLore());
		return b.toString();
	}
}
