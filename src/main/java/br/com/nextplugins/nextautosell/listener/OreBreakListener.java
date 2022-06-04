package br.com.nextplugins.nextautosell.listener;

import br.com.nextplugins.nextautosell.hook.EconomyHook;
import br.com.nextplugins.nextautosell.manager.AutoSellManager;
import br.com.nextplugins.nextautosell.manager.FortuneManager;
import br.com.nextplugins.nextautosell.model.Multiplier;
import br.com.nextplugins.nextautosell.util.ActionBarUtil;
import br.com.nextplugins.nextautosell.util.ColorUtil;
import br.com.nextplugins.nextautosell.util.FortuneUtil;
import br.com.nextplugins.nextautosell.util.NumberFormatter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@RequiredArgsConstructor
public final class OreBreakListener implements Listener {

    private final FileConfiguration configuration;
    private final AutoSellManager manager;
    private final EconomyHook economy;
    private final boolean useFortuneMultiplier;
    private final FortuneManager fortuneManager;

    private final Random RANDOM = new Random();

    @EventHandler
    public void handleOreBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        final List<String> allowedWorlds = configuration.getStringList("allowed-worlds");

        if (!allowedWorlds.contains(block.getWorld().getName())) return;

        final Multiplier playerMultiplier = manager.getPlayerMultiplier(player);

        final double blockPrice = manager.getBlockPriceWithMultiplier(block.getType(), playerMultiplier.getValue());

        if (blockPrice == 0) return;

        int fortuneLevel = FortuneUtil.getFortuneLevel(player.getItemInHand());

        double finalPrice = blockPrice;

        if (useFortuneMultiplier) {
            finalPrice = fortuneManager.applyFortuneMultiplier(
                player,
                blockPrice,
                getDrops(player.getItemInHand(), block, fortuneLevel)
            );
        }

        event.setCancelled(true);
        block.setType(Material.AIR);

        economy.depositCoins(player, finalPrice);

        final String formattedPrice = NumberFormatter.format(finalPrice);

        final String message = Objects.requireNonNull(configuration.getString("message"))
            .replace("{moneyEarned}", formattedPrice)
            .replace("{multiplierName}", ColorUtil.colored(playerMultiplier.getDisplayName()))
            .replace("{multiplierValue}", NumberFormatter.format(playerMultiplier.getValue()))
            .replace("{fortune}", String.valueOf(fortuneLevel));

        ActionBarUtil.sendActionBar(player, message);
    }

    private int getDrops(ItemStack tool, Block block, int fortuneLevel) {
        final ItemStack drop = block.getDrops(tool).stream()
            .findFirst().orElse(null);

        return drop == null ? 1 : drop.getAmount() + RANDOM.nextInt(fortuneLevel == 0 ? 1 : fortuneLevel);
    }

}
