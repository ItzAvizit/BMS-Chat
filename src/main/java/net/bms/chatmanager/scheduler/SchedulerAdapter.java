package net.bms.chatmanager.scheduler;

import org.bukkit.plugin.Plugin;

public interface SchedulerAdapter {
    void runAsync(Runnable runnable);
    void runSync(Runnable runnable);
    void runLaterAsync(Runnable runnable, long delayTicks);
    void runTimerAsync(Runnable runnable, long delayTicks, long periodTicks);
    void cancelTasks(Plugin plugin);
}
