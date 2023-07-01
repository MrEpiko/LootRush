package me.mrepiko.lootrush.listeners;

import me.mrepiko.lootrush.EventStatus;
import me.mrepiko.lootrush.EventType;
import me.mrepiko.lootrush.LootRush;
import me.mrepiko.lootrush.manager.SoloPlayer;
import me.mrepiko.lootrush.manager.Team;
import me.mrepiko.lootrush.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class ItemDeliveryListener implements Listener {

    @EventHandler
    public void movement(PlayerMoveEvent event) {

        if (LootRush.getEventStatus() != EventStatus.ACTIVE) return;

        if (LootRush.getEventType() == EventType.NONE) return;

        Location loc = event.getPlayer().getLocation();

        if (LootRush.getEventType() == EventType.SOLO) {

            SoloPlayer soloPlayer = LootRush.getRoundManager().getSoloPlayer(event.getPlayer());
            if (soloPlayer == null) return;

            if (
                    (loc.getBlockX() >= soloPlayer.getLocation().getBlockX() - 1 && loc.getBlockX() <= soloPlayer.getLocation().getBlockX() + 1)
                    && (loc.getBlockZ() >= soloPlayer.getLocation().getBlockZ() - 1 && loc.getBlockZ() <= soloPlayer.getLocation().getBlockZ() + 1)
            ) {

                int pts;
                ItemStack modifiedItem;
                int newPoints;
                final List<ItemStack> newContents = new ArrayList<>();
                for (ItemStack item: event.getPlayer().getInventory().getContents()) {

                    if (item == null) continue;

                    modifiedItem = item.clone();
                    modifiedItem.setAmount(1);

                    if (!LootRush.getGeneratorItems().containsKey(modifiedItem)) {
                        newContents.add(item);
                        continue;
                    }

                    pts = LootRush.getGeneratorItems().get(modifiedItem) * item.getAmount();

                    if (!LootRush.getMessages().get("point-score").equalsIgnoreCase(""))
                        event.getPlayer().sendMessage(LootRush.getMessages().get("point-score").replace("{points}", String.valueOf(pts)).replace("{item}", ItemUtils.getItemName(item)).replace("{amount}", String.valueOf(item.getAmount())));

                    newPoints = LootRush.getRoundManager().getPlayers().get(event.getPlayer().getDisplayName()).getPoints() + pts;

                    LootRush.getRoundManager().getPlayers().get(event.getPlayer().getDisplayName()).setPoints(newPoints);

                }

                event.getPlayer().getInventory().setContents(newContents.toArray(new ItemStack[0]));

            }

        } else if (LootRush.getEventType() == EventType.TEAM) {

            Team team = LootRush.getRoundManager().getTeamOfPlayer(event.getPlayer());
            if (team == null) return;

            if (
                    (loc.getBlockX() >= team.getLocation().getBlockX() - 1 && loc.getBlockX() <= team.getLocation().getBlockX() + 1)
                            && (loc.getBlockZ() >= team.getLocation().getBlockZ() - 1 && loc.getBlockZ() <= team.getLocation().getBlockZ() + 1)
            ) {

                int pts;
                ItemStack modifiedItem;
                int newPoints;
                final List<ItemStack> newContents = new ArrayList<>();
                for (ItemStack item: event.getPlayer().getInventory().getContents()) {

                    if (item == null) continue;

                    modifiedItem = item.clone();
                    modifiedItem.setAmount(1);

                    if (!LootRush.getGeneratorItems().containsKey(modifiedItem)) {
                        newContents.add(item);
                        continue;
                    }

                    pts = LootRush.getGeneratorItems().get(modifiedItem) * item.getAmount();

                    if (!LootRush.getMessages().get("point-score").equalsIgnoreCase(""))
                        event.getPlayer().sendMessage(LootRush.getMessages().get("point-score").replace("{points}", String.valueOf(pts)).replace("{item}", ItemUtils.getItemName(item)).replace("{amount}", String.valueOf(item.getAmount())));

                    newPoints = LootRush.getRoundManager().getTeams().get(team.getColor()).getPoints() + pts;

                    LootRush.getRoundManager().getTeams().get(team.getColor()).setPoints(newPoints);

                }

                event.getPlayer().getInventory().setContents(newContents.toArray(new ItemStack[0]));

            }


        }

        endCheck();

    }

    private void endCheck() {

        if (LootRush.getEventType() == EventType.SOLO) {

            List<SoloPlayer> players = new ArrayList<>(LootRush.getRoundManager().getPlayers().values());

            players.sort(Comparator.comparingInt(SoloPlayer::getPoints).reversed());

            if (players.get(0).getPoints() < LootRush.getPointsNeededSolo()) return;

            if (!LootRush.getMessages().get("solo-round-end").equalsIgnoreCase("")) {

                String first = (players.size() >= 1) ? players.get(0).getPlayer().getDisplayName() : "N/A";
                int firstPoints = (players.size() >= 1) ? players.get(0).getPoints() : 0;

                String second = (players.size() >= 2) ? players.get(1).getPlayer().getDisplayName() : "N/A";
                int secondPoints = (players.size() >= 2) ? players.get(1).getPoints() : 0;

                String third = (players.size() >= 3) ? players.get(2).getPlayer().getDisplayName() : "N/A";
                int thirdPoints = (players.size() >= 3) ? players.get(2).getPoints() : 0;

                Bukkit.broadcastMessage(LootRush.getMessages().get("solo-round-end")
                        .replace("{first}", first)
                        .replace("{first_points}", String.valueOf(firstPoints))
                        .replace("{second}", second)
                        .replace("{second_points}", String.valueOf(secondPoints))
                        .replace("{third}", third)
                        .replace("{third_points}", String.valueOf(thirdPoints))
                );

                end();

            }

            Bukkit.getConsoleSender().sendMessage("[LootRush] Full list of winners:");
            for (SoloPlayer p: players) {
                Bukkit.getConsoleSender().sendMessage("- " + p.getPlayer().getDisplayName());
            }

        } else if (LootRush.getEventType() == EventType.TEAM) {

            List<Team> teams = new ArrayList<>(LootRush.getRoundManager().getTeams().values());

            teams.sort(Comparator.comparingInt(Team::getPoints).reversed());

            if (teams.get(0).getPoints() < LootRush.getPointsNeededTeam()) return;

            if (!LootRush.getMessages().get("team-round-end").equalsIgnoreCase("")) {

                String first = (teams.size() >= 1) ? teams.get(0).getColorName() : "N/A";
                int firstPoints = (teams.size() >= 1) ? teams.get(0).getPoints() : 0;

                String second = (teams.size() >= 2) ? teams.get(1).getColorName() : "N/A";
                int secondPoints =(teams.size() >= 2) ? teams.get(1).getPoints() : 0;

                String third =(teams.size() >= 3) ? teams.get(2).getColorName() : "N/A";
                int thirdPoints =(teams.size() >= 3) ? teams.get(2).getPoints() : 0;

                Bukkit.broadcastMessage(LootRush.getMessages().get("team-round-end")
                        .replace("{first}", first)
                        .replace("{first_points}", String.valueOf(firstPoints))
                        .replace("{second}", second)
                        .replace("{second_points}", String.valueOf(secondPoints))
                        .replace("{third}", third)
                        .replace("{third_points}", String.valueOf(thirdPoints))
                );

                end();

                StringBuilder output = new StringBuilder("First team: " + teams.get(0).getColorName() + "\n-");

                for (Player p: teams.get(0).getPlayers()) {

                    output.append(p.getDisplayName())
                            .append(" ");

                }

                if (!second.equalsIgnoreCase("")) {
                    output.append("\nSecond team: ")
                            .append(teams.get(1).getColorName()).append("\n-");
                    for (Player p: teams.get(1).getPlayers()) {

                        output.append(p.getDisplayName())
                                .append(" ");

                    }
                }

                if (!third.equalsIgnoreCase("")) {
                    output.append("\nThird team: ")
                            .append(teams.get(2).getColorName()).append("\n-");
                    for (Player p: teams.get(2).getPlayers()) {

                        output.append(p.getDisplayName())
                                .append(" ");

                    }
                }

                Bukkit.getServer().getConsoleSender().sendMessage("[LootRush] Team round has ended:\n\n" + output);

                Bukkit.broadcast("§e§lLoot§6§lRush §e| §7Round has ended, team output: \n\n" + output, "lootrush.debug");

            }

        }



    }

    private void end() {
        LootRush.setEventStatus(EventStatus.INACTIVE);
        LootRush.setEventType(EventType.NONE);

        LootRush.getRoundManager().clear();
        LootRush.getGeneratorManager().stop();
        LootRush.getLootDrop().stop();

        for (BukkitTask task: Bukkit.getScheduler().getPendingTasks()) {
            if (task.getOwner().equals(LootRush.getINSTANCE())) {
                task.cancel();
            }
        }

        for (Player p: Bukkit.getOnlinePlayers()) {
            if (!p.isOnline()) continue;

            p.teleport(LootRush.getSpectatorRoom());

            p.getInventory().setBoots(null);
            p.getInventory().setLeggings(null);
            p.getInventory().setHelmet(null);
            p.getInventory().setChestplate(null);
            p.getInventory().clear();

            for (PotionEffect ef: p.getActivePotionEffects()) {
                p.removePotionEffect(ef.getType());
            }

            p.setHealth(20);
            p.setSaturation(20);

        }
    }

}
