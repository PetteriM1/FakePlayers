package idk.plugin.fake;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.plugin.PluginBase;

import java.security.SecureRandom;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class Main extends PluginBase implements Listener {
    
    Map<String,UUID> fakers = new HashMap<>();
    Map<UUID,String> fakers2 = new HashMap<>();
    Skin skin = new Skin(new byte[Skin.SINGLE_SKIN_SIZE], "Standard_Custom");
    String alphabet = "abcdefghijklmnopqrstuvwxyz";
    SecureRandom random = new SecureRandom();

    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("fake")) {
            if (args.length != 2) return false;
            UUID uuid = UUID.randomUUID();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 15; ++i) {
                sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
            }
            String xuid = sb.toString();
            switch (args[0]) {
                case "add":
                    fakers.put(args[1], uuid);
                    fakers2.put(uuid, args[1]);
                    Server.getInstance().updatePlayerListData(uuid, Entity.entityCount++, args[1], skin, xuid);
                    sender.sendMessage("\u00A7aFake player added");
                    break;
                case "remove":
                    try {
                        fakers.remove(args[1], uuid);
                        fakers2.remove(uuid, args[1]);
                        Server.getInstance().removePlayerListData(fakers.get(args[1]));
                        sender.sendMessage("\u00A7aFake player removed");
                    } catch (NullPointerException e) {
                        sender.sendMessage("\u00A7cFailed to remove player " + args[1]);
                    }
                    break;
                default:
                    return false;
            }
        }

        return true;
    }
    
    @EventHandler
    public void setQuery(QueryRegenerateEvent e) {
        e.setPlayerCount(e.getPlayerCount() + fakers.size());
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        for (String name : fakers2.values()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 15; ++i) {
                sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
            }
            String xuid = sb.toString();
            UUID uuid = fakers.get(name);
            Server.getInstance().updatePlayerListData(uuid, Entity.entityCount++, name, skin, xuid);
        }
    }
}
