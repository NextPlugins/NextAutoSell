package br.com.nextplugins.nextautosell;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class NextAutoSell extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            getLogger().info("Plugin inicializado com sucesso.");
        } catch (Throwable t) {
            t.printStackTrace();
            getLogger().severe("Ocorreu um erro durante a inicialização do plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public static NextAutoSell getInstance() {
        return getPlugin(NextAutoSell.class);
    }

}
