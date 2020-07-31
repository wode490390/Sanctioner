package cn.wode490390.nukkit.sanctioner;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginIdentifiableCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.network.protocol.ChangeDimensionPacket;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.TextFormat;

public class StuckCommand extends Command implements PluginIdentifiableCommand {

    private static final ChangeDimensionPacket STUCK_PACKET = new ChangeDimensionPacket();

    static {
        STUCK_PACKET.dimension = Level.DIMENSION_OVERWORLD;
        STUCK_PACKET.encode();
    }

    private final Plugin plugin;

    public StuckCommand(Plugin plugin) {
        super("stuck", "Stucks the player's client", "/stuck <player>");
        this.setPermission("sanctioner.stuck");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false)
        });
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!this.plugin.isEnabled() || !this.testPermission(sender)) {
            return false;
        }

        if (args.length > 0) {
            Player player = plugin.getServer().getPlayer(args[0]);
            if (player != null) {
                player.dataPacket(STUCK_PACKET);

                Command.broadcastCommandMessage(sender, TextFormat.YELLOW + "Successfully stuck " + args[0] + "'s client");
            } else {
                sender.sendMessage("No targets matched selector");
            }
        } else {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.getUsage()));
        }

        return true;
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }
}
