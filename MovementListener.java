package me.server.anticheat;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MovementListener extends PacketListenerAbstract {
    private final AntiCheatPlugin plugin;
    private final DataManager data;
    private final ViolationManager violations;

    public MovementListener(AntiCheatPlugin plugin, DataManager data, ViolationManager violations) {
        super(PacketListenerPriority.NORMAL);
        this.plugin = plugin;
        this.data = data;
        this.violations = violations;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) return;
        if (!(event.getPlayer() instanceof Player player)) return;

        PlayerData d = data.get(player.getUniqueId());
        if (d == null) return;

        WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);
        if (!flying.hasPositionChanged()) return;

        Location loc = flying.getLocation();
        long now = System.currentTimeMillis();

        if (d.hasLast && now > d.ignoreUntil) {
            double dx = loc.getX() - d.x;
            double dz = loc.getZ() - d.z;
            double horiz = Math.sqrt(dx * dx + dz * dz);
            double max = plugin.getConfig().getDouble("speed.max-horizontal", 0.9);

            // дешёвый предфильтр в сетевом потоке; детали проверяем уже в основном потоке
            if (horiz > max) {
                Bukkit.getScheduler().runTask(plugin, () -> verifySpeed(player, d, horiz, max));
            }
        }

        d.x = loc.getX();
        d.y = loc.getY();
        d.z = loc.getZ();
        d.hasLast = true;
    }

    private void verifySpeed(Player p, PlayerData d, double horiz, double max) {
        if (!p.isOnline() || p.hasPermission("ac.bypass")) return;
        if (p.isFlying() || p.getAllowFlight() || p.isGliding()
                || p.isInsideVehicle() || p.isRiptiding()) return;
        if (System.currentTimeMillis() < d.velocityUntil) return;

        PotionEffect speed = p.getPotionEffect(PotionEffectType.SPEED);
        if (speed != null) max *= 1.0 + 0.25 * (speed.getAmplifier() + 1);

        if (horiz > max) {
            violations.flag(p, "Speed", String.format("%.2f b/tick, max %.2f", horiz, max));
        }
    }
}
