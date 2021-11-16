package br.com.nextplugins.nextautosell.configuration;

import br.com.nextplugins.nextautosell.NextAutoSell;
import com.google.common.base.Stopwatch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(staticName = "of")
public final class ConfigurationManager {

    private final NextAutoSell plugin;

    @Getter private FileConfiguration multipliersConfiguration;
    @Getter private FileConfiguration pricesConfiguration;

    public void init() {
        files();
    }

    private void files() {
        plugin.saveResource("multipliers.yml", false);
        plugin.saveResource("prices.yml", false);

        multipliersConfiguration = loadConfiguration("multipliers.yml");
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
            try {
                final Stopwatch timer = Stopwatch.createStarted();

                plugin.reloadConfig();

                this.multipliersConfiguration = loadConfiguration("multipliers.yml");
                plugin.getAutoSellManager().loadMultipliers(true);

                this.pricesConfiguration = loadConfiguration("prices.yml");
                plugin.getAutoSellManager().loadPrices(true);

                timer.stop();

                return ChatColor.GREEN + String.format("Todos os arquivos de configurações foram recarregados com sucesso. (%sms)",
                    timer.elapsed(TimeUnit.MILLISECONDS)
                );
            } catch (Throwable t) {
                t.printStackTrace();
                return ChatColor.RED + "Ocorreu um erro durante o carregamento de um dos arquivos de configurações.";
            }
        });

        return reloadFuture.get();
    }

}
