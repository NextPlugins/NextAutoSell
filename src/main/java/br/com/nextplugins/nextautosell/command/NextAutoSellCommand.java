package br.com.nextplugins.nextautosell.command;

import br.com.nextplugins.nextautosell.NextAutoSell;
import br.com.nextplugins.nextautosell.configuration.ConfigurationManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public final class NextAutoSellCommand implements CommandExecutor {

    private final NextAutoSell plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(
                "",
                "§b§lNextAutoSell §8-§7 Ajuda",
                "",
                String.format("§7/%s §8-§f Leia esta mensagem", label),
                String.format("§7/%s reload §8-§f Recarrega todos os arquivos", label)
            );
            return true;
        }

        final String argument = args[0];

        if (argument.equalsIgnoreCase("reload") || argument.equalsIgnoreCase("rl")) {
            if (!sender.hasPermission("nextautosell.command.reload")) {
                sender.sendMessage(ChatColor.RED + "Você não tem permissão para executar este comando.");
                return true;
            }

            final ConfigurationManager configurationManager = plugin.getConfigurationManager();

            final String callbackMessage = configurationManager.tryReloadAllAndGiveCallbackMessage();

            sender.sendMessage(callbackMessage);

            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Argumentos inválidos! Use: /oreautosell ou /oas para ajuda.");
        }

        return false;
    }

}
