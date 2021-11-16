package br.com.nextplugins.nextautosell.configuration;

import br.com.nextplugins.nextautosell.NextAutoSell;
import com.google.common.base.Stopwatch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(staticName = "of")
public final class ConfigurationManager {

    private final NextAutoSell plugin;

    @Getter private FileConfiguration multipliersConfiguration;
    @Getter private FileConfiguration messagesConfiguration;
    @Getter private FileConfiguration pricesConfiguration;

    public void init() {
        files();
    }

    private void files() {
        plugin.saveResource("multipliers.yml", false);
        plugin.saveResource("messages.yml", false);
        plugin.saveResource("prices.yml", false);

        multipliersConfiguration = loadConfiguration("multipliers.yml");
        messagesConfiguration = loadConfiguration("messages.yml");
        pricesConfiguration = loadConfiguration("prices.yml");
    }

    @SneakyThrows
    private FileConfiguration loadConfiguration(String name) {
        final File file = new File(plugin.getDataFolder(), name);

        return YamlConfiguration.loadConfiguration(file);
    }

    @SneakyThrows
    public String tryReloadAllAndGiveCallbackMessage() {
        final CompletableFuture<String> reloadFuture = CompletableFuture.supplyAsync(() -> {
            final ConfigurationSection messages = messagesConfiguration.getConfigurationSection("messages");

            try {
                final Stopwatch timer = Stopwatch.createStarted();

                plugin.reloadConfig();

                this.multipliersConfiguration = loadConfiguration("multipliers.yml");
                plugin.getAutoSellManager().loadMultipliers(true);

                this.pricesConfiguration = loadConfiguration("prices.yml");
                plugin.getAutoSellManager().loadPrices(true);

                this.messagesConfiguration = loadConfiguration("messages.yml");

                timer.stop();

                return Objects.requireNonNull(messages.getString("successful-reload"))
                    .replace("{elapsedTime}", String.valueOf(timer.elapsed(TimeUnit.MILLISECONDS)));
            } catch (Throwable t) {
                t.printStackTrace();
                return messages.getString("reload-failed");
            }
        });

        return reloadFuture.get();
    }

}
