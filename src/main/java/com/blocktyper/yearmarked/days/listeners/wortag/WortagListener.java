package com.blocktyper.yearmarked.days.listeners.wortag;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.NetherWarts;

import com.blocktyper.yearmarked.ConfigKey;
import com.blocktyper.yearmarked.LocalizedMessage;
import com.blocktyper.yearmarked.YearmarkedListenerBase;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.days.DayOfWeek;
import com.blocktyper.yearmarked.days.YearmarkedCalendar;
import com.blocktyper.yearmarked.items.YMRecipe;

public class WortagListener extends YearmarkedListenerBase {

	private Random random = new Random();

	public WortagListener(YearmarkedPlugin plugin) {
		super(plugin);
		new NetherstalkListener(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCropsBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();

		YearmarkedCalendar cal = new YearmarkedCalendar(block.getWorld());
		if (!cal.getDayOfWeekEnum().equals(DayOfWeek.WORTAG)) {
			return;
		}

		if (block.getType() != Material.NETHER_WARTS) {
			return;
		}

		if (!(block.getState().getData() instanceof NetherWarts)) {
			warning("Nethwart block did not have NetherWarts state data");
			return;
		}

		NetherWarts netherWarts = (NetherWarts) block.getState().getData();

		if (netherWarts.getState() != NetherWartsState.RIPE) {
			debugInfo("Nethwarts were not ripe");
			return;
		} else {
			debugInfo("Nethwarts were ripe");
		}

		if (!worldEnabled(event.getPlayer().getWorld().getName(), DayOfWeek.WORTAG.getDisplayKey())) {
			return;
		}

		int high = getConfig().getInt(ConfigKey.WORTAG_BONUS_CROPS_RANGE_HIGH.getKey(), 3);
		int low = getConfig().getInt(ConfigKey.WORTAG_BONUS_CROPS_RANGE_LOW.getKey(), 1);

		int rewardCount = random.nextInt(high + 1);

		if (rewardCount < low) {
			rewardCount = low;
		}

		if (rewardCount > 0) {
			ItemStack reward = getItemFromRecipe(YMRecipe.WORTAG_NETHERWORT, event.getPlayer(), null, null);
			String displayName = reward.getItemMeta() != null && reward.getItemMeta().getDisplayName() != null ? reward.getItemMeta().getDisplayName() : "";
			
			String bonus = getLocalizedMessage(LocalizedMessage.BONUS.getKey(), event.getPlayer());
			event.getPlayer().sendMessage(
					ChatColor.DARK_PURPLE + bonus + "[x" + rewardCount + "] " + displayName);
			
			dropItemsInStacks(block.getLocation(), rewardCount, reward);
		} else {
			debugInfo("No luck on Wortag");
			event.getPlayer().sendMessage(ChatColor.RED + ":(");
		}

	}

}
