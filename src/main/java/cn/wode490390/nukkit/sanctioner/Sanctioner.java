package cn.wode490390.nukkit.sanctioner;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.permission.BanEntry;
import cn.nukkit.permission.BanList;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;

public class Sanctioner extends PluginBase implements Listener {

    private static final List<Byte> IGNORE_PACKETS = ImmutableList.<Byte>builder()
            .add(ProtocolInfo.ADVENTURE_SETTINGS_PACKET)
            .add(ProtocolInfo.ANIMATE_PACKET)
            .add(ProtocolInfo.BLOCK_ENTITY_DATA_PACKET)
            .add(ProtocolInfo.BLOCK_PICK_REQUEST_PACKET)
            .add(ProtocolInfo.BOOK_EDIT_PACKET)
            .add(ProtocolInfo.BOSS_EVENT_PACKET)
            .add(ProtocolInfo.COMMAND_BLOCK_UPDATE_PACKET)
            .add(ProtocolInfo.COMMAND_REQUEST_PACKET)
            .add(ProtocolInfo.CONTAINER_CLOSE_PACKET)
            .add(ProtocolInfo.CRAFTING_EVENT_PACKET)
            .add(ProtocolInfo.ENTITY_EVENT_PACKET)
            .add(ProtocolInfo.ENTITY_FALL_PACKET)
            .add(ProtocolInfo.ENTITY_PICK_REQUEST_PACKET)
            .add(ProtocolInfo.INTERACT_PACKET)
            .add(ProtocolInfo.INVENTORY_TRANSACTION_PACKET)
            .add(ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET)
            //.add(ProtocolInfo.LAB_TABLE_PACKET)
            .add(ProtocolInfo.LECTERN_UPDATE_PACKET)
            .add(ProtocolInfo.LEVEL_SOUND_EVENT_PACKET)
            .add(ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V1)
            .add(ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V2)
            .add(ProtocolInfo.MAP_CREATE_LOCKED_COPY_PACKET)
            .add(ProtocolInfo.MAP_INFO_REQUEST_PACKET)
            .add(ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET)
            .add(ProtocolInfo.MOB_EQUIPMENT_PACKET)
            .add(ProtocolInfo.MODAL_FORM_RESPONSE_PACKET)
            .add(ProtocolInfo.MOVE_ENTITY_ABSOLUTE_PACKET)
            .add(ProtocolInfo.MOVE_PLAYER_PACKET)
            .add(ProtocolInfo.NETWORK_STACK_LATENCY_PACKET)
            .add(ProtocolInfo.NPC_REQUEST_PACKET)
            .add(ProtocolInfo.PLAYER_ACTION_PACKET)
            .add(ProtocolInfo.PLAYER_HOTBAR_PACKET)
            .add(ProtocolInfo.PLAYER_INPUT_PACKET)
            .add(ProtocolInfo.PLAYER_SKIN_PACKET)
            .add(ProtocolInfo.PURCHASE_RECEIPT_PACKET)
            .add(ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET)
            .add(ProtocolInfo.RIDER_JUMP_PACKET)
            .add(ProtocolInfo.SCRIPT_CUSTOM_EVENT_PACKET)
            .add(ProtocolInfo.SERVER_SETTINGS_REQUEST_PACKET)
            .add(ProtocolInfo.SET_DEFAULT_GAME_TYPE_PACKET)
            .add(ProtocolInfo.SET_DIFFICULTY_PACKET)
            .add(ProtocolInfo.SET_ENTITY_DATA_PACKET)
            .add(ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET)
            .add(ProtocolInfo.SHOW_CREDITS_PACKET)
            .add(ProtocolInfo.SIMPLE_EVENT_PACKET)
            .add(ProtocolInfo.SPAWN_EXPERIENCE_ORB_PACKET)
            .add(ProtocolInfo.STRUCTURE_BLOCK_UPDATE_PACKET)
            .add(ProtocolInfo.TEXT_PACKET)
            .build();

    private final List<BanEntry> banned = Lists.newArrayList();
    private BanList ban;

    @Override
    public void onEnable() {
        try {
            new MetricsLite(this);
        } catch (Exception ignore) {

        }
        this.ban = this.getServer().getNameBans();
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getCommandMap().register("sanctioner", new CrashCommand(this));
    }

    @Override
    public void onDisable() {
        for (BanEntry entry : Lists.newArrayList(this.banned)) {
            this.ban.add(entry);
            this.banned.remove(entry);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPreLogin(PlayerPreLoginEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        if (this.ban.isBanned(name)) {
            this.banned.add(this.ban.getEntires().get(name.toLowerCase()));
            Command.broadcastCommandMessage(this.getServer().getConsoleSender(), TextFormat.YELLOW + "Player " + name + " has been banned! Enable ghost mode for " + name);
            this.ban.remove(name);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (BanEntry entry : Lists.newArrayList(this.banned)) {
            if (entry.getName().equalsIgnoreCase(player.getName())) {
                this.ban.add(entry);
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDataPacketReceive(DataPacketReceiveEvent event) {
        Player player = event.getPlayer();
        if (player != null && IGNORE_PACKETS.contains(event.getPacket().pid())) {
            for (BanEntry entry : this.banned) {
                if (entry.getName().equalsIgnoreCase(player.getName())) {
                    event.setCancelled();
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (BanEntry entry : Lists.newArrayList(this.banned)) {
            if (entry.getName().equalsIgnoreCase(player.getName())) {
                this.ban.add(entry);
                this.banned.remove(entry);
                break;
            }
        }
    }
}
