package me.mrepiko.lootrush.manager;

import lombok.Setter;
import me.mrepiko.lootrush.LootRush;
import me.mrepiko.lootrush.config.ConfigGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GeneratorManager {

    private boolean started = false;
    @Setter
    private HashMap<ConfigGenerator, List<ArmorStand>> armorStands = new HashMap<>();

    public void start() {

        if (started) return;

        started = true;

        for (ConfigGenerator gen: LootRush.getGenerators()) {

            Bukkit.getWorld(LootRush.getWorld()).getNearbyEntities(gen.getLocation(), 2, 20, 2).forEach(Entity::remove);

            new BukkitRunnable() {

                int c = gen.getInterval();

                @Override
                public void run() {

                    if (!started) {
                        cancel();
                    } else {

                        if (c < 0) {
                            final Location loc = gen.getLocation().clone().add(0, 1.5, 0);
                            loc.getWorld().dropItem(loc, gen.getItem()).setVelocity(new Vector());
                            c = gen.getInterval();
                        } else {

                            List<ArmorStand> genArmorStands;
                            if (!armorStands.containsKey(gen)) {
                                ArmorStand hologram;
                                double spacing = 0;

                                genArmorStands = new ArrayList<>();

                                for (String msg: gen.getHologramMessages()) {

                                    hologram = (ArmorStand) gen.getLocation().getWorld().spawnEntity(gen.getLocation().clone().add(0, -0.5 + spacing, 0), EntityType.ARMOR_STAND);
                                    hologram.setVisible(false);
                                    hologram.setCustomNameVisible(true);
                                    hologram.setCustomName(ChatColor.translateAlternateColorCodes('&', msg.replace("{seconds}", String.valueOf(gen.getInterval()))));
                                    hologram.setGravity(false);

                                    spacing += 0.25;

                                    genArmorStands.add(hologram);

                                }

                                armorStands.put(gen, genArmorStands);

                            } else {

                                genArmorStands = armorStands.get(gen);
                                ArmorStand tempArmorStand;
                                for (int i = 0; i < genArmorStands.size(); i++) {

                                    tempArmorStand = genArmorStands.get(i);
                                    tempArmorStand.setCustomName(ChatColor.translateAlternateColorCodes('&', gen.getHologramMessages().get(i).replace("{seconds}", String.valueOf((c)))));

                                }

                            }

                            c--;

                        }

                    }

                }
            }.runTaskTimer(LootRush.getINSTANCE(), 20L, 20L);


        }

    }

    public void stop() {

        started = false;

        for (List<ArmorStand> l: armorStands.values()) {
            for (ArmorStand a: l) {
                a.remove();
            }
        }
        armorStands = new HashMap<>();


    }

}
