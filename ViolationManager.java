package me.server.anticheat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Копит очки нарушений, шлёт алерты, при желании наказывает. Вызывать из основного потока. */
public class ViolationManager {
    private final AntiCheatPlugin plugin;
    private final Map<UUID, Map<String, Double>> vl = new ConcurrentHashMap<>();

    public ViolationManager(AntiCheatPlugin plugin) {
        this.plugin = plugin;
        // раз в 5 секунд снимаем по 1 очку, чтобы честные игроки не копили нарушения вечно
        Bukkit.getScheduler().runTaskTimer(plugin, this::decay, 100L, 100L);
    }

    public void flag(Player p, String check, String info) {
        double v = vl.computeIfAbsent(p.getUniqueId(), k -> new ConcurrentHashMap<>())
                .merge(check, 1.0, Double::sum);

        String msg = "§c[AC] §f" + p.getName() + " §7failed §e" + check
                + " §7(" + info + ") §cVL " + (int) v;
        for (Player o : Bukkit.getOnlinePlayers()) {
            if (o.hasPermission("ac.alerts")) o.sendMessage(msg);
        }
        plugin.getLogger().info(p.getName() + " failed " + check + " (" + info + ") VL " + (int) v);

        String cmd = plugin.getConfig().getString("punish-command", "");
        int threshold = plugin.getConfig().getInt("punish-threshold", 15);
        if (!cmd.isBlank() && v >= threshold) {
            vl.remove(p.getUniqueId());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", p.getName()));
        }
    }

    private void decay() {
        vl.forEach((id, checks) -> {
            checks.replaceAll((c, v) -> v - 1.0);
            checks.values().removeIf(v -> v <= 0);
        });
        vl.values().removeIf(Map::isEmpty);
    }
}
