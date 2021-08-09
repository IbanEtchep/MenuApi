package fr.iban.menuapi.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import fr.iban.menuapi.MenuItem;
import fr.iban.menuapi.utils.ItemBuilder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;


public abstract class Menu implements InventoryHolder {

	protected Player player;
	protected Inventory inventory;
	protected ItemStack FILLER_GLASS = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).setName("").build();
	protected Map<Integer, MenuItem> menuItems = new HashMap<>();
	protected Multimap<Integer, MenuItem> templateItems = ArrayListMultimap.create();
	protected List<Integer> templateSlots;
	protected Menu previousMenu;
	private int lastInsert;

	public Menu(Player player) {
		this.player = player;
		inventory = Bukkit.createInventory(this, getSlots(), LegacyComponentSerializer.builder().hexColors().build().deserialize(getMenuName()));
		this.setMenuTemplateItems();
		this.templateSlots= getTemplateSlots();
		this.setMenuItems();
	}

	public Menu(Player player, Menu previousMenu) {
		this(player);
		this.previousMenu = previousMenu;
	}

	public void open() {
		inventory.clear();
		fillInventory();
		player.openInventory(inventory);
	}

	public abstract String getMenuName();

	public abstract int getRows();

	public int getSlots() {
		return getRows()*9;
	}

	public void handleMenu(InventoryClickEvent e) {
		if(e.getClickedInventory() == e.getView().getTopInventory()) {
			int slot = e.getSlot();
			MenuItem item = menuItems.get(slot);
			if(item != null && item.getCallback() != null && item.getDisplayCondition().getAsBoolean()) {
				item.getCallback().onClick(e);
				return;
			}
			for(MenuItem templateItem : templateItems.get(slot)) {
				if(templateItem != null && templateItem.getCallback() != null && templateItem.getDisplayCondition().getAsBoolean()) {
					templateItem.getCallback().onClick(e);
				}
			}
		}
	}

	public void handleMenuClose(InventoryCloseEvent e) {

	}

	public abstract void setMenuTemplateItems();

	public boolean isTemplateSlot(int slot) {
		return templateSlots.contains(slot) ;
	}

	public abstract void setMenuItems();

	protected void fillTemplateItems() {
		for (int i = 0; i < getSlots(); i++) {
			for(MenuItem templateItem : templateItems.get(i)) {
				if(!templateItem.getDisplayCondition().getAsBoolean())
					continue;
				inventory.setItem(i, templateItem.getItem());
			}
		}
	}

	private void fillItems() {
		for (int i = 0; i < getSlots(); i++) {
			MenuItem menuItem = menuItems.get(i);
			if(menuItem == null) continue;
			inventory.setItem(i, menuItem.getItem());
		}
	}

	protected void fillInventory() {
		fillTemplateItems();
		fillItems();
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	protected MenuItem getCloseBotton() {
		if(hasPreviousMenu()) {
			return new MenuItem(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(ChatColor.DARK_RED + "Retour").build(), click -> {
				previousMenu.open();
			});
		}else {
			return new MenuItem(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(ChatColor.DARK_RED + "Fermer").build(), click -> {
				player.closeInventory();
			});
		}
	}

	protected boolean hasPreviousMenu() {
		return previousMenu != null;
	}

	private List<Integer> getTemplateSlots() {
		List<Integer> templateSlots = new ArrayList<>();
		for (int i = 0; i < getSlots(); i++) {
			if(templateItems.containsKey(i)) {
				templateSlots.add(i);
			}
		}
		return templateSlots;
	}


	/*
	 * MenuItems methods
	 */

	protected void setMenuItem(int row, int coll, MenuItem item) {
		setMenuItem(row*9 + coll, item);
	}


	protected void setMenuItem(int slot, MenuItem item) {
		menuItems.put(slot, item);
	}

	protected void addMenuItem(MenuItem item) {
		int i = lastInsert;
		while(menuItems.keySet().contains(i) || isTemplateSlot(i)) {
			i++;
		}
		menuItems.put(i, item);
		lastInsert = i;
	}

	/*
	 * Menu template methods
	 */

	protected void setMenuTemplateItem(int row, int coll, MenuItem item) {
		setMenuTemplateItem(row*9 + coll, item);
	}

	protected void setMenuTemplateItem(int slot, MenuItem item) {
		templateItems.put(slot, item);
	}


	/*
	 * Utils
	 */


	/**
	 * Util to cut a string into litle parts of defined size.
	 * @param msg
	 * @param lineSize
	 * @return
	 */
	protected List<String> splitString(String msg, int lineSize) {
		List<String> res = new ArrayList<>();
		Pattern p = Pattern.compile("\\b.{1," + (lineSize-1) + "}\\b\\W?");
		Matcher m = p.matcher(msg);
		while(m.find()) {
			res.add("§a" + m.group());
		}
		return res;
	}

	public void setFillerGlass(){
		for (int i = 0; i < getSlots(); i++) {
			if (inventory.getItem(i) == null){
				inventory.setItem(i, FILLER_GLASS);
			}
		}
	}

	public void fillWithGlass() {
		for (int i = inventory.firstEmpty() ; inventory.firstEmpty() != -1; i = inventory.firstEmpty()) {
			inventory.setItem(i, FILLER_GLASS);
		}
	}

	public void addMenuBorder(){
		for (int i = 0; i < 10; i++) {
			setMenuTemplateItem(i, new MenuItem(FILLER_GLASS));
		}
		if(getRows() >= 2) {
			setMenuTemplateItem(9, new MenuItem(FILLER_GLASS));
			setMenuTemplateItem(17, new MenuItem(FILLER_GLASS));		
		}
		if(getRows() >= 3) {
			setMenuTemplateItem(18, new MenuItem(FILLER_GLASS));
			setMenuTemplateItem(26, new MenuItem(FILLER_GLASS));
		}
		if(getRows() >= 4) {
			setMenuTemplateItem(35, new MenuItem(FILLER_GLASS));
			setMenuTemplateItem(27, new MenuItem(FILLER_GLASS));
		}
		if(getRows() >= 5) {
			setMenuTemplateItem(36, new MenuItem(FILLER_GLASS));
			setMenuTemplateItem(44, new MenuItem(FILLER_GLASS));
		}
		for (int i = getSlots() - 9; i < getSlots(); i++) {
			setMenuTemplateItem(i, new MenuItem(FILLER_GLASS));
		}
	}

}

