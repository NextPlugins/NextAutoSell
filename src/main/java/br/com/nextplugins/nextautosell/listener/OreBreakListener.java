package br.com.nextplugins.nextautosell.listener;

import br.com.nextplugins.nextautosell.hook.EconomyHook;
import br.com.nextplugins.nextautosell.manager.AutoSellManager;
import br.com.nextplugins.nextautosell.util.ActionBarUtil;
import br.com.nextplugins.nextautosell.util.FortuneUtil;
import br.com.nextplugins.nextautosell.util.NumberFormatter;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public final class OreBreakListener implements Listener {

    private final FileConfiguration configuration;
    private final AutoSellManager manager;
    private final EconomyHook economy;
    private final boolean useFortuneMultiplier;

    @EventHandler
    public void handleOreBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        final List<String> allowedWorlds = configuration.getStringList("allowed-worlds");

        if (!allowedWorlds.contains(block.getWorld().getName())) return;

        final double playerMultiplier = manager.getPlayerMultiplier(player);

        final double blockPrice = manager.getBlockPriceWithMultiplier(block.getType(), playerMultiplier);

        if (blockPrice == 0) return;

        int fortuneLevel = FortuneUtil.getFortuneLevel(player.getItemInHand());

        double finalPrice = blockPrice;

        if (useFortuneMultiplier) {
            fortuneLevel = fortuneLevel == 0
                ? 1
                : fortuneLevel;

            finalPrice = blockPrice * fortuneLevel;
        }

        economy.depositCoins(player, finalPrice);

        final String formattedPrice = NumberFormatter.format(finalPrice);

        final String message = Objects.requireNonNull(configuration.getString("message"))
            .replace("{moneyEarned}", formattedPrice)
            .replace("{multiplier}", NumberFormatter.format(playerMultiplier))
            .replace("{fortune}", String.valueOf(fortuneLevel));

        ActionBarUtil.sendActionBar(player, message);
    }

}
