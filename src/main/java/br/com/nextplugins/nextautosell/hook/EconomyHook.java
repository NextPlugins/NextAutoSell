package br.com.nextplugins.nextautosell.hook;

import br.com.nextplugins.nextautosell.NextAutoSell;
import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public final class EconomyHook {

    private final Logger logger;

    private Economy economy;

    public void init() {
        final PluginManager pluginManager = Bukkit.getPluginManager();

        if (!pluginManager.isPluginEnabled("Vault")) {
            logger.severe("Vault not found on the server! Disabling the plugin...");
            pluginManager.disablePlugin(NextAutoSell.getInstance());
            return;
        }

        final RegisteredServiceProvider<Economy> registration = Bukkit.getServicesManager().getRegistration(Economy.class);

        if (registration == null) {
            logger.severe("No economy plugin found on the server! Disabling the plugin...");
            pluginManager.disablePlugin(NextAutoSell.getInstance());
        } else {
            economy = registration.getProvider();
            logger.log(Level.INFO, "Successfully linked economy plugin! ({0})", registration.getPlugin().getName());
        }
    }

    public void depositCoins(OfflinePlayer player, double amount) {
        economy.depositPlayer(player, amount);
    }

}
