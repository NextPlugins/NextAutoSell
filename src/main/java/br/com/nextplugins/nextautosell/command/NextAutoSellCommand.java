package br.com.nextplugins.nextautosell.command;

import br.com.nextplugins.nextautosell.NextAutoSell;
import br.com.nextplugins.nextautosell.configuration.ConfigurationManager;
import br.com.nextplugins.nextautosell.util.ColorUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class NextAutoSellCommand implements CommandExecutor {

    private final NextAutoSell plugin;

    private final ConfigurationSection messages;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            final Set<String> helpMessage = Objects.requireNonNull(messages.getStringList("commands.help"))
                .stream()
                .map(s -> s.replace("{label}", label))
                .collect(Collectors.toSet());

            for (String message : helpMessage) {
                sender.sendMessage(ColorUtil.colored(message));
            }

            return true;
        }

        final String argument = args[0];

        if (argument.equalsIgnoreCase("reload") || argument.equalsIgnoreCase("rl")) {
            if (!sender.hasPermission("nextautosell.command.reload")) {
                sender.sendMessage(ChatColor.RED + "");
                return true;
            }

            final ConfigurationManager configurationManager = plugin.getConfigurationManager();

            final String callbackMessage = configurationManager.tryReloadAllAndGiveCallbackMessage();

            sender.sendMessage(ColorUtil.colored(callbackMessage));

            return true;
        } else {
            sender.sendMessage(ColorUtil.colored(messages.getString("invalid-arguments")));
        }

        return false;
    }

}
