package plugin.java_Project.pattern;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import plugin.java_Project.Java_Project;

import java.util.Random;

public class WitherAttackPattern {
    private Java_Project plugin;
    private int patternNum;
    private Wither wither;
    private final Random random = new Random();

    public WitherAttackPattern(Wither wither, Java_Project plugin) {
        this.wither = wither;
        this.patternNum = 1;
        this.plugin = plugin;
        this.doPattern();
    }

    public WitherAttackPattern(Wither wither, int patternNum, Java_Project plugin) {
        this.wither = wither;
        this.patternNum = patternNum;
        this.plugin = plugin;
        this.doPattern();
    }

    public void doPattern() {
        switch(patternNum) {
            case 1 -> pullPlayers();
            case 2 -> teleportLightning();
        }
    }

    public void pullPlayers() {
        Location loc = wither.getLocation();
        World world = wither.getWorld();

        for (Player plr : world.getPlayers()) {
            if(plr.getLocation().distance(loc) <= 120) {
                Vector pull = loc.toVector()
                        .subtract(plr.getLocation().toVector())
                        .normalize()
                        .multiply(0.9);

                plr.setVelocity(pull);
            }
        }

        world.playSound(loc, Sound.ENTITY_WITHER_AMBIENT, 1f, 0.6f);
        world.spawnParticle(Particle.PORTAL, loc, 400, 0, 0, 0, 8f);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            world.spawnParticle(Particle.EXPLOSION, loc, 15, 0, 0, 0, 10f);
            world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1, 0.8f);

            for (Player player : world.getPlayers()) {
                if (player.getLocation().distance(loc) <= 15) {
                    player.damage(30.0, wither);
                }
            }

        }, 35L);
    }

    public void teleportLightning() {
        Location prevLoc = wither.getLocation();
        World world = prevLoc.getWorld();

        Location newLoc = prevLoc.clone().add(
                random.nextInt(20) - 10,
                0,
                random.nextInt(20) - 10
        );
        wither.teleport(newLoc);

        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45);
            double x = Math.cos(angle) * 5;
            double z = Math.sin(angle) * 5;

            Location strikeLoc = newLoc.clone().add(x, 0, z);
            world.strikeLightningEffect(strikeLoc);
        }

        world.playSound(prevLoc, Sound.ENTITY_WITHER_SHOOT, 1, 0.6f);
    }
}
