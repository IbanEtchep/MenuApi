package fr.iban.menuapi.objects;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import fr.iban.menuapi.utils.HexColor;
import fr.iban.menuapi.utils.ItemBuilder;

@SerializableAs("display")
public class Display implements ConfigurationSerializable {

	private int page = -1;
	private int slot = -1;
	private ItemStack itemstack;
	private String name;
	private List<String> lore;
	private boolean enchanted = false;
	private ItemStack builtItemStack;


	public Display(int page, int slot, ItemStack itemstack, String name, List<String> lore, boolean enchanted) {
		this.page = page;
		this.slot = slot;
		this.itemstack = itemstack;
		this.name = name;
		this.lore = lore;
		this.enchanted = enchanted;
	}

	@SuppressWarnings("unchecked")
	public Display(Map<String, Object> map) {
//		for (String string : map.keySet()) {
//			System.out.println(string);
//		}
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
		if(map.containsKey("enchanted")) {
			enchanted = (boolean)map.get("enchanted");
		}
	}

	public Display() {
		this(-1, -1, new ItemStack(Material.DIRT), "No name", Arrays.asList("NoDesc", "Nodesc"), false);
	}

	public ItemStack getItemstack() {
		return itemstack;
	}

	public void setItemstack(ItemStack itemstack) {
		this.builtItemStack = null;
		this.itemstack = itemstack;
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

	public ItemStack getBuiltItemStack() {
		if(builtItemStack == null) {
			System.out.println(name);
			System.out.println(lore);
			builtItemStack = new ItemBuilder(itemstack).setName(HexColor.translateColorCodes(name)).setLore(HexColor.translateColorCodes(lore)).setGlow(enchanted).build();
		}
		return builtItemStack;
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("page", getPage());
		map.put("slot", getSlot());
		map.put("item", getItemstack());
		map.put("name", getName());
		map.put("lore", getLore());
		map.put("enchanted", isEnchanted());
		return map;
	}

}
