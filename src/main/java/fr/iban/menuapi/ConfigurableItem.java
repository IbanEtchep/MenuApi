package fr.iban.menuapi;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import fr.iban.menuapi.utils.HexColor;
import fr.iban.menuapi.utils.ItemBuilder;

@SerializableAs("menuitem")
public class ConfigurableItem implements ConfigurationSerializable {

	private int page = -1;
	private int slot = -1;
	private ItemStack itemstack;
	private String name;
	private List<String> lore;
	private boolean enchanted = false;
	//private ItemStack builtItemStack;
	private List<String> clickCommands;


	public ConfigurableItem(int page, int slot, ItemStack itemstack, String name, List<String> lore, boolean enchanted, List<String> clickCommands) {
		this.page = page;
		this.slot = slot;
		this.itemstack = removeItemMeta(itemstack);
		this.name = name;
		this.lore = lore;
		this.enchanted = enchanted;
		this.clickCommands = clickCommands;
	}

	public ConfigurableItem(ConfigurableItem item){
		this(item.getPage(), item.getSlot(), item.getItemstack(), item.getName(), item.getLore(), item.isEnchanted(), item.getClickCommands());
	}

	@SuppressWarnings("unchecked")
	public ConfigurableItem(Map<String, Object> map) {
		if(map.containsKey("page")) {
			page = (int) map.get("page");
		}
		if(map.containsKey("slot")) {
			slot = (int) map.get("slot");
		}
		if(map.containsKey("item")) {
			itemstack = (ItemStack) map.get("item");
		}
		if(map.containsKey("name")) {
			name = (String) map.get("name");
		}
		if(map.containsKey("lore")) {
			lore = (List<String>) map.get("lore");
		}
		if(map.containsKey("commands")) {
			clickCommands = (List<String>) map.get("commands");
		}
		if(map.containsKey("enchanted")) {
			enchanted = (boolean)map.get("enchanted");
		}
	}

	public ConfigurableItem() {
		this(-1, -1, new ItemStack(Material.DIRT), "No name", Arrays.asList("NoDesc", "Nodesc"), false, null);
	}

	public ItemStack getItemstack() {
		return itemstack;
	}

	public void setItemstack(ItemStack item) {
		this.itemstack = removeItemMeta(item);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getLore() {
		return lore;
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public boolean isEnchanted() {
		return enchanted;
	}

	public List<String> getClickCommands() {
		return clickCommands;
	}

	public ItemStack getBuiltItemStack() {
//		if(builtItemStack == null) {
//			builtItemStack = new ItemBuilder(itemstack).setName(HexColor.translateColorCodes(name)).setLore(lore == null ? new ArrayList<>() : HexColor.translateColorCodes(lore)).setGlow(enchanted).build();
//		}
		return new ItemBuilder(itemstack).setName(HexColor.translateColorCodes(name)).setLore(lore == null ? new ArrayList<>() : HexColor.translateColorCodes(lore)).setGlow(enchanted).build();
	}

//	public void setBuiltItemStack(ItemStack builtItemStack) {
//		this.builtItemStack = builtItemStack;
//	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("page", getPage());
		map.put("slot", getSlot());
		map.put("item", removeItemMeta(getItemstack()));
		map.put("name", getName());
		map.put("lore", getLore());
		map.put("enchanted", isEnchanted());
		map.put("commands", clickCommands);
		return map;
	}

	/*
		Remove item meta to avoid duplicated meta information on config file.
	 */
	private ItemStack removeItemMeta(ItemStack itemstack){
		ItemStack item = itemstack.clone();
		if (item.hasItemMeta()) {
			ItemMeta im = item.getItemMeta();
			if (im.hasLore())
				im.setLore(new ArrayList<>());
			if (im.hasDisplayName())
				im.setDisplayName("");
			if(itemstack.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
				im.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
				enchanted = true;
				if(im.hasEnchants()){
					for(Enchantment enchant : im.getEnchants().keySet()){
						im.removeEnchant(enchant);
					}
				}
			}
			item.setItemMeta(im);
		}
		return item;
	}

}
