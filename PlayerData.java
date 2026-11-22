package me.server.anticheat;

/** Состояние игрока, которое пакетный поток читает и пишет. */
public class PlayerData {
    public volatile double x, y, z;
    public volatile boolean hasLast;
    /** До этого момента (мс) проверки движения пропускаются (телепорт, респавн, смена мира). */
    public volatile long ignoreUntil;
    /** До этого момента (мс) игрок мог получить knockback/взрыв. */
    public volatile long velocityUntil;

    public void resetPosition(long ignoreMillis) {
        hasLast = false;
        ignoreUntil = System.currentTimeMillis() + ignoreMillis;
    }
}
