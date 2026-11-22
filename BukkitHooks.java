package me.server.anticheat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class BukkitHooks implements Listener {
    private final DataManager data;

    public BukkitHooks(DataManager data) { this.data = data; }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) { data.create(e.getPlayer().getUniqueId()).resetPosition(3000); }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) { data.remove(e.getPlayer().getUniqueId()); }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) { reset(e.getPlayer().getUniqueId()); }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) { reset(e.getPlayer().getUniqueId()); }

    @EventHandler
    public void onWorld(PlayerChangedWorldEvent e) { reset(e.getPlayer().getUniqueId()); }

    @EventHandler
    public void onVelocity(PlayerVelocityEvent e) {
        PlayerData d = data.get(e.getPlayer().getUniqueId());
        if (d != null) d.velocityUntil = System.currentTimeMillis() + 1500;
    }

    private void reset(java.util.UUID id) {
        PlayerData d = data.get(id);
        if (d != null) d.resetPosition(1500);
    }
}
