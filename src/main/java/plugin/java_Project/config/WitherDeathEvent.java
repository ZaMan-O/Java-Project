package plugin.java_Project.config;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import plugin.java_Project.Java_Project;

public class WitherDeathEvent implements Listener {
    Java_Project plugin;

    public WitherDeathEvent(Java_Project plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWitherDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Wither wither)) return;

        Location center = wither.getLocation();
        World world = center.getWorld();
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45);
            double x = Math.cos(angle);
            double z = Math.sin(angle);

            Location spawnLoc = center.clone().add(x * 1.5, 1.5, z * 1.5);

            TNTPrimed tnt = world.spawn(spawnLoc, TNTPrimed.class);

            Vector velocity = new Vector(x, 0.4, z).multiply(0.9f);
            tnt.setVelocity(velocity);

            tnt.setFuseTicks(40);
            tnt.setYield(3f);
            tnt.setIsIncendiary(false);
            tnt.setSource(null);
        }
        for (int i = 0; i < 16; i++) {
            double angle = Math.toRadians(i * 22.4);
            double x = Math.cos(angle);
            double z = Math.sin(angle);

            Location spawnLoc = center.clone().add(x * 1.5, 1.5, z * 1.5);

            TNTPrimed tnt = world.spawn(spawnLoc, TNTPrimed.class);

            Vector velocity = new Vector(x, 0.4, z).multiply(1.2f);
            tnt.setVelocity(velocity);

            tnt.setFuseTicks(60);
            tnt.setYield(6f);
            tnt.setIsIncendiary(false);
            tnt.setSource(null);
        }
        world.playSound(center, Sound.ENTITY_TNT_PRIMED, 1.5f, 1f);

        new BukkitRunnable() {
            int step = 0;
            final int maxSteps = 200;
            final double radiusStep = 0.08;
            final double angleStep = Math.toRadians(18);

            double radius = 0;
            double angle = 0;

            @Override
            public void run() {
                if (step >= maxSteps) {
                    this.cancel();
                    return;
                }
                for(int i=0;i<2;i++) {
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    double y = step * 0.09;
                    Location flameLoc = center.clone().add(x, y, z);
                    world.spawnParticle(
                            Particle.FLAME,
                            flameLoc,
                            6,
                            0.05, 0.05, 0.05,
                            0
                    );
                    radius += radiusStep;
                    angle += angleStep;
                    step++;
                }
            }

        }.runTaskTimer(plugin, 0L, 1L);

        new BukkitRunnable() {
            int step = 0;
            @Override
            public void run() {
                if(step >= 3) {
                    this.cancel();
                    return;
                }
                world.spawnParticle(Particle.EXPLOSION, center, 20, 0, 0, 0, 20f);
                world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1, 0.8f);
                step++;
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }
}
