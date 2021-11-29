package br.com.nextplugins.nextautosell.manager;

import br.com.nextplugins.nextautosell.util.ColorUtil;
import br.com.nextplugins.nextautosell.util.FortuneUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class FortuneManager {

    public static FortuneMode FORTUNE_MODE;
    private final ConfigurationSection configuration;

    public void init() {
        FORTUNE_MODE = FortuneMode.valueOf(configuration.getString("type"));
    }

    public double applyFortuneMultiplier(Player player, double value, int drops) {
        switch (FORTUNE_MODE) {
            case ABSOLUTE:
                int fortuneLevel = FortuneUtil.getFortuneLevel(player.getItemInHand());

                if (fortuneLevel == 0) fortuneLevel = 1;

                value *= fortuneLevel;

                break;
            case DROP:
                if (drops == 0) drops = 1;

                value *= drops;

                player.sendMessage(ColorUtil.colored("&aTotal de drops: &f" + drops));

                break;
        }

        return value;
    }

    enum FortuneMode {
        ABSOLUTE,
        DROP
    }

}
