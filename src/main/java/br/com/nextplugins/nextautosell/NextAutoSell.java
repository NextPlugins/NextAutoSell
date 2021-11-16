package br.com.nextplugins.nextautosell;

import br.com.nextplugins.nextautosell.command.NextAutoSellCommand;
import br.com.nextplugins.nextautosell.configuration.ConfigurationManager;
import br.com.nextplugins.nextautosell.hook.EconomyHook;
import br.com.nextplugins.nextautosell.listener.OreBreakListener;
import br.com.nextplugins.nextautosell.manager.AutoSellManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class NextAutoSell extends JavaPlugin {

    private final ConfigurationManager configurationManager = ConfigurationManager.of(this);

    private final AutoSellManager autoSellManager = new AutoSellManager(this);

    private final EconomyHook economyHook = new EconomyHook(this.getLogger());

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        try {
            configurationManager.init();
            autoSellManager.init();
            economyHook.init();

            commands();
            listeners();

            getLogger().info("Plugin inicializado com sucesso.");
        } catch (Throwable t) {
            t.printStackTrace();
            getLogger().severe("Ocorreu um erro durante a inicialização do plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void listeners() {
        final PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(
            new OreBreakListener(
                getConfig(),
                this.autoSellManager,
                this.economyHook
            ),
            this
        );
    }

    private void commands() {
        final PluginCommand command = getCommand("nextautosell");

        if (command == null) return;

        command.setExecutor(
            new NextAutoSellCommand(
                this,
                configurationManager.getMessagesConfiguration().getConfigurationSection("messages")
            )
        );
    }

    public static NextAutoSell getInstance() {
        return getPlugin(NextAutoSell.class);
    }

}
