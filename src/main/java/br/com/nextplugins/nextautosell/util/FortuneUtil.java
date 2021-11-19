package br.com.nextplugins.nextautosell.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public final class FortuneUtil {

    public static int getFortuneLevel(ItemStack itemStack) {
        return itemStack.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
    }

}
