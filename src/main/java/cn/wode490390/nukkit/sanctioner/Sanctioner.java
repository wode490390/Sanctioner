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
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.TextPacket;
import cn.nukkit.permission.BanEntry;
import cn.nukkit.permission.BanList;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import com.google.common.collect.Lists;

import java.util.List;

public class Sanctioner extends PluginBase implements Listener {

    private static final boolean[] IGNORE_PACKETS = new boolean[256];

    static {
        IGNORE_PACKETS[ProtocolInfo.ADVENTURE_SETTINGS_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.ANIMATE_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.ANVIL_DAMAGE_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.BLOCK_ENTITY_DATA_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.BLOCK_PICK_REQUEST_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.BOOK_EDIT_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.BOSS_EVENT_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.COMMAND_BLOCK_UPDATE_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.COMMAND_REQUEST_PACKET & 0xff] = true;
        //IGNORE_PACKETS[ProtocolInfo.CONTAINER_CLOSE_PACKET & 0xff] = false; // 1.16 inventory :(
        IGNORE_PACKETS[ProtocolInfo.CRAFTING_EVENT_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.DEBUG_INFO_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.EMOTE_LIST_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.EMOTE_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.ENTITY_EVENT_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.ENTITY_FALL_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.ENTITY_PICK_REQUEST_PACKET & 0xff] = true;
        //IGNORE_PACKETS[ProtocolInfo.INTERACT_PACKET & 0xff] = false; // 1.16 inventory :(
        IGNORE_PACKETS[ProtocolInfo.INVENTORY_TRANSACTION_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.ITEM_STACK_REQUEST_PACKET & 0xff] = true;
        IGNORE_PACKETS[0x6d & 0xff] = true; //ProtocolInfo.LAB_TABLE_PACKET
        IGNORE_PACKETS[ProtocolInfo.LECTERN_UPDATE_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.LEVEL_SOUND_EVENT_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V1 & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V2 & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.MAP_CREATE_LOCKED_COPY_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.MAP_INFO_REQUEST_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.MOB_EQUIPMENT_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.MODAL_FORM_RESPONSE_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.MOVE_ENTITY_ABSOLUTE_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.MOVE_PLAYER_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.MULTIPLAYER_SETTINGS_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.NETWORK_STACK_LATENCY_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.NPC_REQUEST_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.PACKET_VIOLATION_WARNING_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.PLAYER_ACTION_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.PLAYER_AUTH_INPUT_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.PLAYER_HOTBAR_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.PLAYER_INPUT_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.PLAYER_SKIN_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.POS_TRACKING_CLIENT_REQUEST_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.PURCHASE_RECEIPT_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.RESPAWN_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.RIDER_JUMP_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.SCRIPT_CUSTOM_EVENT_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.SERVER_SETTINGS_REQUEST_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.SET_DEFAULT_GAME_TYPE_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.SET_DIFFICULTY_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.SET_ENTITY_DATA_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.SHOW_CREDITS_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.SIMPLE_EVENT_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.SPAWN_EXPERIENCE_ORB_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.STRUCTURE_BLOCK_UPDATE_PACKET & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.STRUCTURE_TEMPLATE_DATA_EXPORT_REQUEST & 0xff] = true;
        IGNORE_PACKETS[ProtocolInfo.TEXT_PACKET & 0xff] = true;
    }

    private final List<BanEntry> banned = Lists.newArrayList();
    private BanList ban;

    @Override
    public void onEnable() {
        try {
            new MetricsLite(this, 4838);
        } catch (Throwable ignore) {

        }

        this.ban = this.getServer().getNameBans();

        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getCommandMap().register("sanctioner", new CrashCommand(this));
        this.getServer().getCommandMap().register("sanctioner", new StuckCommand(this));
    }

    @Override
    public void onDisable() {
        for (BanEntry entry : Lists.newArrayList(this.banned)) {
            this.ban.add(entry);
            this.banned.remove(entry);
        }

        this.getServer().getOnlinePlayers().values().forEach(player -> {
            if (this.ban.isBanned(player.getName())) {
                player.close("", "", false);
            }
        });
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
        DataPacket packet = event.getPacket();
        int id = packet.pid();
        if (player != null && IGNORE_PACKETS[id & 0xff]) {
            for (BanEntry entry : this.banned) {
                if (entry.getName().equalsIgnoreCase(player.getName())) {
                    event.setCancelled();

                    switch (id) {
                        case ProtocolInfo.TEXT_PACKET:
                            TextPacket textPacket = (TextPacket) packet;
                            if (textPacket.type == TextPacket.TYPE_CHAT) {
                                TextPacket pk = new TextPacket();
                                pk.type = TextPacket.TYPE_RAW;
                                pk.message = this.getServer().getLanguage().translateString("chat.type.text", new String[]{player.getDisplayName(), textPacket.message});
                                player.dataPacket(pk); // feedback
                            }
                            break;
                        case ProtocolInfo.COMMAND_REQUEST_PACKET:
                            TextPacket pk = new TextPacket();
                            pk.type = TextPacket.TYPE_RAW;
                            pk.message = TextFormat.RED + "An unknown error occurred while attempting to perform this command: java.lang.NullPointerException";
                            player.dataPacket(pk); // fake feedback
                            break;
                        default:
                            break;
                    }

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
