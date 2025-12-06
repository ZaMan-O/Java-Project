package plugin.java_Project.config;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import plugin.java_Project.Java_Project;

public class WitherSpawnEvent implements Listener {
    public Java_Project plugin;
    public WitherSpawnEvent(Java_Project plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void witherSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.WITHER &&
                event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.BUILD_WITHER) {
            Wither wither = (Wither) event.getEntity();

            wither.setAI(false);
            final Location loc = wither.getLocation();
            final World world = loc.getWorld();

            new BukkitRunnable() {
                int repeatNum = 0;
                final double radius = 2;
                final int angleNum = 30;

                @Override
                public void run() {
                    double angle = Math.toRadians(angleNum * repeatNum % 360);
                    double x = Math.cos(angle) * (radius + repeatNum * 0.1);
                    double z = Math.sin(angle) * (radius + repeatNum * 0.1);
                    Location strikeLoc = loc.clone().add(x, 0, z);
                    world.spawnParticle(Particle.DUST_COLOR_TRANSITION, strikeLoc,
                            100, 0.6, 0.6, 0.6,
                            new Particle.DustTransition(
                                    Color.fromRGB(255, 0, 0),
                                    Color.fromRGB(0, 0, 0),
                                    1f
                            ));
                    world.spawnParticle(Particle.EXPLOSION, strikeLoc,
                            8, 0, 0, 0,
                            2.7f);
                    world.spawnParticle(Particle.END_ROD, strikeLoc,
                            25, 0, 0, 0,
                            0.6f);
                    world.strikeLightning(strikeLoc);

                    repeatNum++;

                    if (repeatNum >= 48) {
                        wither.setAI(true);
                        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
                        world.spawnParticle(Particle.EXPLOSION, loc,
                                8, 0, 0, 0,
                                12f);
                        world.spawnParticle(Particle.END_ROD, loc,
                                250, 0, 0, 0,
                                2.2f);
                        for(Player p : Bukkit.getOnlinePlayers()) {
                            p.damage(70.0, wither);
                            p.addPotionEffect(
                                    new PotionEffect(PotionEffectType.BLINDNESS, 200, 1)
                            );
                        }
                        this.cancel();
                    }
                }

            }.runTaskTimer(plugin, 0L, 3L);

            new BukkitRunnable() {
                int step = 0;
                @Override
                public void run() {
                    if(wither.isDead()) {
                        this.cancel();
                        return;
                    }
                    if(step >= 7) {
                        Location nowLoc = wither.getLocation();
                        TNTPrimed tnt = world.spawn(nowLoc, TNTPrimed.class);

                        tnt.setFuseTicks(50);
                        tnt.setYield(3f);
                        tnt.setIsIncendiary(false);
                        tnt.setSource(null);
                    }
                    step++;
                }
            }.runTaskTimer(plugin, 0L, 35L);
        }
    }

    @EventHandler
    public void explodeDamageImmu(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Wither wither)) return;
        if(event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) event.setCancelled(true);
    }
}
