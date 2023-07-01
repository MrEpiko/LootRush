package me.mrepiko.lootrush.manager.lootdrop;

import lombok.Getter;
import lombok.Setter;
import me.mrepiko.lootrush.LootRush;
import me.mrepiko.lootrush.manager.lootdrop.LootDropItem;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class LootDrop {

    @Getter private Material material;
    @Getter private int interval;
    @Getter private final List<LootDropItem> items;
    @Getter private final int guiSize;
    @Getter private final String guiTitle;
    @Getter private List<String> hologramMessages;
    @Getter private Location northEastCorner;
    @Getter private Location southWestCorner;

    @Getter @Setter private Material previousBlock = null;
    @Getter @Setter private Location currentLocation = null;
    @Getter @Setter private List<ArmorStand> armorStands = new ArrayList<>();

    @Getter private Boolean started = false;
    @Getter @Setter private int secondsLeft;

    public LootDrop(Material material, int interval, List<LootDropItem> items, int guiSize, String guiTitle, List<String> hologramMessages, Location northEastCorner, Location southWestCorner) {
        this.material = material;
        this.guiSize = guiSize;
        this.guiTitle = guiTitle;
        this.interval = interval;
        this.items = items;
        Collections.reverse(hologramMessages);
        this.hologramMessages = hologramMessages;
        this.northEastCorner = northEastCorner;
        this.southWestCorner = southWestCorner;
    }

    public void start() {

        if (this.started) return;

        if (
                (northEastCorner.getBlockX() <= southWestCorner.getBlockX())
                || (northEastCorner.getBlockY() <= southWestCorner.getBlockY())
                || (northEastCorner.getBlockZ() >= southWestCorner.getBlockZ())
        ) {
            Bukkit.getServer().getConsoleSender().sendMessage("[LootRush] Invalid Loot drop corners setup.");
            return;
        }

        this.started = true;

        new BukkitRunnable() {

            int c = interval;
            int randomX, safeY, randomZ = 0;
            final Random random = new Random();

            @Override
            public void run() {

                if (!started) {
                    cancel();
                } else {

                    if (c >= 0) LootRush.getLootDrop().setSecondsLeft(c);
                    else LootRush.getLootDrop().setSecondsLeft(interval);

                    if (c < 0) {
                        randomX = random.nextInt(northEastCorner.getBlockX() - southWestCorner.getBlockX()) + southWestCorner.getBlockX();
                        randomZ = random.nextInt(southWestCorner.getBlockZ() - northEastCorner.getBlockZ()) + northEastCorner.getBlockZ();

                        Location sampleLoc = null;
                        for (int i = southWestCorner.getBlockY(); i < northEastCorner.getBlockY(); i++) {

                            sampleLoc = new Location(northEastCorner.getWorld(), randomX, i, randomZ);
                            if (sampleLoc.getBlock().getType().equals(Material.AIR)) {
                                break;
                            }

                        }

                        if (sampleLoc == null) {
                            Bukkit.getServer().getConsoleSender().sendMessage("[LootRush] Invalid Loot drop corners setup.");
                            cancel();
                            return;
                        }

                        if (previousBlock != null)
                            currentLocation.getBlock().setType(previousBlock);

                        previousBlock = sampleLoc.getBlock().getType();
                        currentLocation = sampleLoc;

                        sampleLoc.getBlock().setType(material);

                        double spacing = 0;
                        ArmorStand hologram;
                        List<ArmorStand> tempArmorStands = new ArrayList<>();
                        for (String msg: hologramMessages) {

                            hologram = (ArmorStand) sampleLoc.getWorld().spawnEntity(sampleLoc.clone().add(0.5, -0.5 + spacing, 0.5), EntityType.ARMOR_STAND);
                            hologram.setVisible(false);
                            hologram.setCustomNameVisible(true);
                            hologram.setCustomName(ChatColor.translateAlternateColorCodes('&', msg));
                            hologram.setGravity(false);

                            tempArmorStands.add(hologram);

                            spacing += 0.25;

                        }

                        for (ArmorStand a: armorStands) {
                            a.remove();
                        }

                        armorStands.clear();

                        armorStands = tempArmorStands;

                        if (!LootRush.getMessages().get("loot-drop-spawned").equalsIgnoreCase(""))
                            Bukkit.broadcastMessage(LootRush.getMessages().get("loot-drop-spawned").replace("{x}", String.valueOf(sampleLoc.getBlockX())).replace("{y}", String.valueOf(sampleLoc.getBlockY())).replace("{z}", String.valueOf(sampleLoc.getBlockZ())));


                        c = interval;

                    } else c--;

                }

            }
        }.runTaskTimer(LootRush.getINSTANCE(), 20L, 20L);

    }

    public void stop() {

        this.started = false;

        if (previousBlock != null)
            currentLocation.getBlock().setType(previousBlock);

        for (ArmorStand a: armorStands) {
            a.remove();
        }

        previousBlock = null;
        currentLocation = null;
        armorStands = new ArrayList<>();

    }

}
