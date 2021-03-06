package br.com.nextplugins.nextautosell.manager;

import br.com.nextplugins.nextautosell.NextAutoSell;
import br.com.nextplugins.nextautosell.model.Multiplier;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public final class AutoSellManager {

    private final NextAutoSell plugin;

    private final Map<String, Multiplier> multipliers = Maps.newLinkedHashMap();
    private final Map<Material, Double> prices = Maps.newLinkedHashMap();

    public void init() {
        loadMultipliers(false);
        loadPrices(false);
    }

    public void loadPrices(boolean fromReload) {
        final FileConfiguration pricesConfiguration = plugin.getConfigurationManager().getPricesConfiguration();
        final ConfigurationSection pricesSection = pricesConfiguration.getConfigurationSection("prices");

        if (pricesSection == null) return;

        final Stopwatch timer = Stopwatch.createStarted();

        if (fromReload) prices.clear();

        for (String key : pricesSection.getKeys(false)) {
            final Material material = Material.valueOf(key);
            final double price = pricesSection.getDouble(key);

            prices.put(material, price);
        }

        timer.stop();

        if (!fromReload) {
            final Logger logger = plugin.getLogger();

            logger.log(Level.INFO, "All block prices have been loaded and cached. ({0}ms)", timer.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    public void loadMultipliers(boolean fromReload) {
        final FileConfiguration multipliersConfiguration = plugin.getConfigurationManager().getMultipliersConfiguration();
        final ConfigurationSection multipliersSection = multipliersConfiguration.getConfigurationSection("multipliers");

        if (multipliersSection == null) return;

        final Stopwatch timer = Stopwatch.createStarted();

        if (fromReload) multipliers.clear();

        for (String key : multipliersSection.getKeys(false)) {
            multipliers.put(key, Multiplier.builder()
                    .id(key)
                    .name(multipliersSection.getString(key + ".name"))
                    .displayName(multipliersSection.getString(key + ".display-name"))
                    .value(multipliersSection.getDouble(key + ".value"))
                    .build()
            );
        }

        timer.stop();

        if (!fromReload) {
            final Logger logger = plugin.getLogger();

            logger.log(Level.INFO, "All multipliers have been loaded and cached. ({0}ms)", timer.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    public double getBlockPriceWithMultiplier(Material type, double multiplier) {
        final double price = prices.getOrDefault(type, 0D);

        return Math.floor(price * multiplier);
    }

    public Multiplier getPlayerMultiplier(Player player) {
        Multiplier playerMultiplier = multipliers.getOrDefault("default", null);

        for (String multiplier : multipliers.keySet()) {
            if (player.hasPermission("oreautosell.multiplier." + multiplier)) {
                playerMultiplier = multipliers.get(multiplier);
            }
        }

        return playerMultiplier;
    }

}
