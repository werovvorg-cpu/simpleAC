package me.server.anticheat;

import com.github.retrooper.packetevents.PacketEvents;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiCheatPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        DataManager data = new DataManager();
        ViolationManager violations = new ViolationManager(this);

        getServer().getPluginManager().registerEvents(new BukkitHooks(data), this);
        PacketEvents.getAPI().getEventManager()
                .registerListener(new MovementListener(this, data, violations));

        getLogger().info("SimpleAC enabled");
    }
}
