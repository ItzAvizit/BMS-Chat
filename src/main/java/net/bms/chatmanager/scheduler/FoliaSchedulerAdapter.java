package net.bms.chatmanager.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import java.util.concurrent.TimeUnit;

public class FoliaSchedulerAdapter implements SchedulerAdapter {
    private final Plugin plugin;

    public FoliaSchedulerAdapter(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runAsync(Runnable runnable) {
        Bukkit.getAsyncScheduler().runNow(plugin, task -> runnable.run());
    }

    @Override
    public void runSync(Runnable runnable) {
        Bukkit.getGlobalRegionScheduler().execute(plugin, runnable);
    }

    @Override
    public void runLaterAsync(Runnable runnable, long delayTicks) {
        Bukkit.getAsyncScheduler().runDelayed(plugin, task -> runnable.run(), delayTicks * 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runTimerAsync(Runnable runnable, long delayTicks, long periodTicks) {
        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, task -> runnable.run(), delayTicks * 50, periodTicks * 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void cancelTasks(Plugin plugin) {
        Bukkit.getAsyncScheduler().cancelTasks(plugin);
        Bukkit.getGlobalRegionScheduler().cancelTasks(plugin);
    }
}
