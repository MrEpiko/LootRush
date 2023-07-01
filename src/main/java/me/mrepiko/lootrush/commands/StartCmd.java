package me.mrepiko.lootrush.commands;

import me.mrepiko.lootrush.EventStatus;
import me.mrepiko.lootrush.EventType;
import me.mrepiko.lootrush.LootRush;
import me.mrepiko.lootrush.manager.SoloPlayer;
import me.mrepiko.lootrush.manager.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class StartCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) return true;

        Player player = (Player) commandSender;

        if (!(player.hasPermission("lootrush.op"))) {
            player.sendMessage(LootRush.getPermissionsMessage());
            return true;
        }

        if (LootRush.getEventStatus() != EventStatus.REGISTRATIONS) {
            player.sendMessage("§e§lLoot§6§lRush §e| §7Event cannot be started as it's not in REGISTRATIONS status.");
            return true;
        }

        LootRush.setEventStatus(EventStatus.ACTIVE);

        if (LootRush.getEventType() == EventType.SOLO) {

            final List<ItemStack> itemsToAdd = new ArrayList<>();
            ItemStack helmet = null;
            ItemStack chestplate = null;
            ItemStack leggings = null;
            ItemStack boots = null;
            boolean hasTracker = false;

            for (ItemStack it: LootRush.getStartItems().get(EventType.SOLO)) {

                if (it.getType().name().endsWith("_HELMET")) helmet = it;
                else if (it.getType().name().endsWith("_CHESTPLATE")) chestplate = it;
                else if (it.getType().name().endsWith("_LEGGINGS")) leggings = it;
                else if (it.getType().name().endsWith("_BOOTS")) boots = it;
                else {
                    if (it.getItemMeta().getDisplayName().equalsIgnoreCase(LootRush.getTrackerIdentifier())) hasTracker = true;
                    itemsToAdd.add(it);
                }
            }

            for (SoloPlayer sp: LootRush.getRoundManager().getPlayers().values()) {

                if (!sp.getPlayer().isOnline()) continue;

                sp.getPlayer().teleport(sp.getLocation());

                sp.getPlayer().getInventory().clear();

                sp.getPlayer().getInventory().setArmorContents(new ItemStack[]{});

                sp.getPlayer().getInventory().setHelmet(helmet);
                sp.getPlayer().getInventory().setChestplate(chestplate);
                sp.getPlayer().getInventory().setLeggings(leggings);
                sp.getPlayer().getInventory().setBoots(boots);

                sp.getPlayer().getInventory().setContents(itemsToAdd.toArray(new ItemStack[0]));

                sp.getPlayer().setHealth(20);
                sp.getPlayer().setSaturation(20);

                sp.getPlayer().setGameMode(GameMode.SURVIVAL);

                for (PotionEffect pe: sp.getPlayer().getActivePotionEffects()) {
                    sp.getPlayer().removePotionEffect(pe.getType());
                }

                if (hasTracker) sp.getPlayer().setCompassTarget(sp.getLocation());

                sp.getPlayer().updateInventory();

            }

            if (!LootRush.getMessages().get("solo-round-start").equalsIgnoreCase(""))
                Bukkit.broadcastMessage(LootRush.getMessages().get("solo-round-start"));

        } else if (LootRush.getEventType() == EventType.TEAM) {

            final List<ItemStack> itemsToAdd = new ArrayList<>();
            ItemStack helmet = null;
            ItemStack chestplate = null;
            ItemStack leggings = null;
            ItemStack boots = null;
            boolean hasTracker = false;

            for (ItemStack it: LootRush.getStartItems().get(EventType.TEAM)) {

                if (it.getType().name().endsWith("_HELMET")) helmet = it;
                else if (it.getType().name().endsWith("_CHESTPLATE")) chestplate = it;
                else if (it.getType().name().endsWith("_LEGGINGS")) leggings = it;
                else if (it.getType().name().endsWith("_BOOTS")) boots = it;
                else {
                    if (it.getItemMeta().getDisplayName().equalsIgnoreCase(LootRush.getTrackerIdentifier())) hasTracker = true;
                    itemsToAdd.add(it);
                }
            }

            String[] rgbElements;

            for (Team tp: LootRush.getRoundManager().getTeams().values()) {

                for (Player p: tp.getPlayers()) {

                    if (!p.isOnline()) continue;

                    p.teleport(tp.getLocation());

                    p.getPlayer().getInventory().clear();

                    if (LootRush.getSyncArmorWithTeamColor()) {

                        rgbElements = tp.getColor().split(",");

                        if (helmet != null) {

                            if (helmet.getType().name().startsWith("LEATHER_")) {

                                LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
                                meta.setColor(Color.fromRGB(Integer.parseInt(rgbElements[0]), Integer.parseInt(rgbElements[1]), Integer.parseInt(rgbElements[2])));
                                helmet.setItemMeta(meta);
                            }
                        }

                        if (chestplate != null) {

                            if (chestplate.getType().name().startsWith("LEATHER_")) {

                                LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
                                meta.setColor(Color.fromRGB(Integer.parseInt(rgbElements[0]), Integer.parseInt(rgbElements[1]), Integer.parseInt(rgbElements[2])));
                                chestplate.setItemMeta(meta);
                            }
                        }

                        if (leggings != null) {
                            if (leggings.getType().name().startsWith("LEATHER_")) {

                                LeatherArmorMeta meta = (LeatherArmorMeta) leggings.getItemMeta();
                                meta.setColor(Color.fromRGB(Integer.parseInt(rgbElements[0]), Integer.parseInt(rgbElements[1]), Integer.parseInt(rgbElements[2])));
                                leggings.setItemMeta(meta);
                            }
                        }

                        if (boots != null) {
                            if (boots.getType().name().startsWith("LEATHER_")) {

                                LeatherArmorMeta meta = (LeatherArmorMeta) boots.getItemMeta();
                                meta.setColor(Color.fromRGB(Integer.parseInt(rgbElements[0]), Integer.parseInt(rgbElements[1]), Integer.parseInt(rgbElements[2])));
                                boots.setItemMeta(meta);
                            }
                        }

                    }

                    p.getInventory().setArmorContents(new ItemStack[]{});

                    p.getPlayer().getInventory().setHelmet(helmet);
                    p.getPlayer().getInventory().setChestplate(chestplate);
                    p.getPlayer().getInventory().setLeggings(leggings);
                    p.getPlayer().getInventory().setBoots(boots);

                    p.getInventory().setContents(itemsToAdd.toArray(new ItemStack[0]));

                    p.setHealth(20);
                    p.setSaturation(20);

                    p.setGameMode(GameMode.SURVIVAL);

                    for (PotionEffect pe: p.getActivePotionEffects()) {
                        p.removePotionEffect(pe.getType());
                    }

                    if (hasTracker) p.setCompassTarget(tp.getLocation());

                    p.updateInventory();

                }

            }

            if (!LootRush.getMessages().get("team-round-start").equalsIgnoreCase(""))
                Bukkit.broadcastMessage(LootRush.getMessages().get("team-round-start"));

        } else {

            player.sendMessage("§e§lLoot§6§lRush §e| §7Invalid event type.");
            return true;

        }

        if (LootRush.getLootDrop() != null) LootRush.getLootDrop().start();

        LootRush.getGeneratorManager().start();

        player.sendMessage("§e§lLoot§6§lRush §e| §7Event started!");

        return true;

    }
}
