package plugin.java_Project;

import org.bukkit.Bukkit;
import org.bukkit.entity.Wither;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.java_Project.config.WitherBattleEvent;
import plugin.java_Project.config.WitherDeathEvent;
import plugin.java_Project.config.WitherSpawnEvent;

public final class Java_Project extends JavaPlugin implements Listener {
    public Wither wither;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new WitherSpawnEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new WitherBattleEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new WitherDeathEvent(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
