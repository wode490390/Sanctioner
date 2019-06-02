package cn.wode490390.nukkit.sanctioner;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginIdentifiableCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.protocol.InventoryContentPacket;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.TextFormat;

public class CrashCommand extends Command implements PluginIdentifiableCommand {

    private static final InventoryContentPacket CRASH_PACKET = new InventoryContentPacket();

    static {
        CRASH_PACKET.inventoryId = ContainerIds.CREATIVE;
        CRASH_PACKET.slots = new Item[]{Item.get(230)};
    }

    private final Plugin plugin;

    public CrashCommand(Plugin plugin) {
        super("crash", "Crashs the player's client", "/crash <player>");
        this.setPermission("sanctioner.crash");
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
                player.dataPacket(CRASH_PACKET);
                Command.broadcastCommandMessage(sender, TextFormat.YELLOW + "Successfully crashed " + args[0] + "'s client");
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
