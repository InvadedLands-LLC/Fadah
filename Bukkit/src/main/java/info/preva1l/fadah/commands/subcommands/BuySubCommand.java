package info.preva1l.fadah.commands.subcommands;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.guis.MainMenu;
import info.preva1l.fadah.utils.commands.SubCommand;
import info.preva1l.fadah.utils.commands.SubCommandArgs;
import info.preva1l.fadah.utils.commands.SubCommandArguments;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BuySubCommand extends SubCommand {

    public BuySubCommand(Fadah plugin) {
        super(plugin, Lang.i().getCommands().getBuy().getAliases(), Lang.i().getCommands().getBuy().getDescription());
    }

    @Override
    @SubCommandArgs(name = "buy", permission = "fadah.buy")
    public void execute(@NotNull SubCommandArguments command) {
        Player player = command.getPlayer();

        if (command.args().length < 1) {
            new MainMenu(null, player, null, null, null).open(player);
            return;
        }

        String search = String.join(" ", command.args());
        new MainMenu(null, player, search, null, null).open(player);
    }

}
