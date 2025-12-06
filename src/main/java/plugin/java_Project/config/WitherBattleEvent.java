package plugin.java_Project.config;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import plugin.java_Project.Java_Project;
import plugin.java_Project.pattern.WitherAttackPattern;

import java.util.*;

public class WitherBattleEvent implements Listener {
    Java_Project plugin;
    private final Random random = new Random();

    private final Map<UUID, Boolean> shieldPhaseMap = new HashMap<>();

    public WitherBattleEvent(Java_Project plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWitherSkullImpact(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof WitherSkull skull)) return;
        if (!(skull.getShooter() instanceof Wither wither)) return;
        if (event.getHitBlock() == null) return;

        Location impactLoc = skull.getLocation();
        World world = impactLoc.getWorld();

        if (random.nextDouble() < 0.25) {
            WitherSkeleton witherSkeleton = world.spawn(impactLoc, WitherSkeleton.class);
            if (random.nextDouble() < 0.2) {
                witherSkeleton.getEquipment().setHelmet(
                        new ItemStack(Material.DIAMOND_HELMET)
                );
            } else {
                witherSkeleton.getEquipment().setHelmet(
                        new ItemStack(Material.IRON_HELMET)
                );
            }
            witherSkeleton.setCustomName("§4위더의 잔재");
            witherSkeleton.setCustomNameVisible(true);

            world.spawnParticle(Particle.DUST_COLOR_TRANSITION, impactLoc,
                    66, 0.3, 0.6, 0.3,
                    new Particle.DustTransition(
                            Color.fromRGB(255, 0, 0),
                            Color.fromRGB(0, 0, 0),
                            1f
                    ));
            world.playSound(impactLoc, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.3f, 1f);
        }
    }

    @EventHandler
    public void witherPatternOnDamage(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Wither wither)) return;
        if(!(event.getDamager() instanceof Player player)) return;

        UUID id = wither.getUniqueId();
        double max = Objects.requireNonNull(wither.getAttribute(Attribute.MAX_HEALTH)).getValue();
        double after = wither.getHealth() - event.getFinalDamage();
        if (!shieldPhaseMap.getOrDefault(id, false) && after <= max * 0.6) {
            startShieldPhase(wither);
        }
        if(random.nextDouble() < 0.08) {
            new WitherAttackPattern(wither, plugin);
        } else if(random.nextDouble() < 0.1){
            new WitherAttackPattern(wither, 2, plugin);
        }
    }

    private void startShieldPhase(Wither wither) {

        UUID id = wither.getUniqueId();
        shieldPhaseMap.put(id, true);
        Location loc = wither.getLocation();
        World world = loc.getWorld();

        for (Player player : world.getPlayers()) {
            if (player.getLocation().distance(loc) <= 40) {

                Vector up = player.getVelocity();
                up.setY(1.6);

                player.setVelocity(up);

                player.playSound(
                        player.getLocation(),
                        Sound.ENTITY_ENDER_DRAGON_FLAP,
                        0.8f,
                        1.2f
                );
            }
        }


        for (int i = 0; i < 4; i++) {
            double angle = Math.toRadians(i * 90);
            double x = Math.cos(angle) * 4;
            double z = Math.sin(angle) * 4;

            Location spawnLoc = loc.clone().add(x, 0, z);

            WitherSkeleton witherSkeleton = world.spawn(spawnLoc, WitherSkeleton.class);
            witherSkeleton.setCustomName("§c위더의 잔재");
            witherSkeleton.setCustomNameVisible(true);
        }

        world.playSound(loc, Sound.ENTITY_WITHER_AMBIENT, 1, 0.5f);
    }
}
