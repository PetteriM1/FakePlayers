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
    
    private Map<String,UUID> fakers = new HashMap<>();
    private Map<UUID,String> fakers2 = new HashMap<>();
    private Skin skin = new Skin();
    private String alphabet = "abcdefghijklmnopqrstuvwxyz";
    private SecureRandom random = new SecureRandom();
    private boolean randomCount = false;
    private int lastRandomCount;
    private int minRandomCount;
    private int maxRandomCount;
    private int minChangeRandomCount;
    private int maxChangeRandomCount;

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
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
            switch (args[0].toLowerCase()) {
                case "add":
                    fakers.put(args[1], uuid);
                    fakers2.put(uuid, args[1]);
                    Server.getInstance().updatePlayerListData(uuid, Entity.entityCount++, args[1], skin, xuid);
                    sender.sendMessage("\u00A7aFake player added");
                    return true;
                case "remove":
                    try {
                        fakers.remove(args[1], uuid);
                        fakers2.remove(uuid, args[1]);
                        Server.getInstance().removePlayerListData(fakers.get(args[1]));
                        sender.sendMessage("\u00A7aFake player removed");
                    } catch (NullPointerException e) {
                        sender.sendMessage("\u00A7cFailed to remove player " + args[1]);
                    }
                    return true;
                default:
                    return false;
            }
        } else if (cmd.getName().equalsIgnoreCase("fakerandomquery")) {
            if (args.length == 0) return false;
            switch (args[0].toLowerCase()) {
                case "on":
                    if (args.length != 6) return false;
                    try {
                        lastRandomCount = Integer.valueOf(args[1]);
                        minRandomCount = Integer.valueOf(args[2]);
                        maxRandomCount = Integer.valueOf(args[3]);
                        minChangeRandomCount = Integer.valueOf(args[4]);
                        maxChangeRandomCount = Integer.valueOf(args[5]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("\u00A7cValue must be numeric");
                        return true;
                    }
                    randomCount = true;
                    sender.sendMessage("\u00A7aFake random query enabled");
                    return true;
                case "off":
                    randomCount = false;
                    sender.sendMessage("\u00A7aFake random query disabled");
                    return true;
                default:
                    return false;
            }
        }

        return true;
    }
    
    @EventHandler
    public void setQuery(QueryRegenerateEvent e) {
        if (randomCount) {
            int count;
            count = lastRandomCount + (random.nextInt(maxChangeRandomCount - minChangeRandomCount + 1) + minChangeRandomCount);
            if (count > maxRandomCount) count = maxRandomCount;
            if (count < minRandomCount) count = minRandomCount;
            e.setPlayerCount(count);
        } else {
            e.setPlayerCount(e.getPlayerCount() + fakers.size());
            //TODO: Add fake plyer names to query
        }
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
