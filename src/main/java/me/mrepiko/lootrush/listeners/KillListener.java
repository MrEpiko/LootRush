package me.mrepiko.lootrush.listeners;

import me.mrepiko.lootrush.EventStatus;
import me.mrepiko.lootrush.EventType;
import me.mrepiko.lootrush.LootRush;
import me.mrepiko.lootrush.manager.SoloPlayer;
import me.mrepiko.lootrush.manager.Team;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class KillListener implements Listener {

    @EventHandler
    public void kill(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (LootRush.getEventStatus() == EventStatus.INACTIVE) return;

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;

        Player player = (Player) event.getEntity();

        if (event.getDamager() instanceof Player) {

            if (LootRush.getEventType() == EventType.TEAM && !LootRush.getFriendlyFire() && LootRush.getRoundManager().getTeamOfPlayer(player) != null && LootRush.getRoundManager().getTeamOfPlayer((Player) event.getDamager()) != null) {

                if (LootRush.getRoundManager().getTeamOfPlayer(player).getColor().equalsIgnoreCase(LootRush.getRoundManager().getTeamOfPlayer((Player) event.getDamager()).getColor())) {
                    event.setCancelled(true);
                    return;
                }
            }

        }

        String victimColorCode = "";
        String damagerColorCode = "&e";

        if (!(player.getHealth() - event.getFinalDamage() <= 0)) return;

        if (LootRush.getEventType() == EventType.SOLO) {
            if (LootRush.getRoundManager().getSoloPlayer(player) == null) return;
        } else if (LootRush.getEventType() == EventType.TEAM) {
            if (LootRush.getRoundManager().getTeamOfPlayer(player) == null) return;
            victimColorCode = LootRush.getRoundManager().getTeamOfPlayer(player).getColorCode();
        } else return;

        if (event.getDamager() instanceof Player) {

            if (LootRush.getEventType() == EventType.TEAM) {
                if (LootRush.getRoundManager().getTeamOfPlayer((Player) event.getDamager()) != null) damagerColorCode = LootRush.getRoundManager().getTeamOfPlayer((Player) event.getDamager()).getColorCode();
            } else if (LootRush.getEventType() == EventType.SOLO) {}
            else return;

        }

        event.setCancelled(true);
        player.teleport(LootRush.getRespawnRoom());

        player.setGameMode(GameMode.SPECTATOR);

        if (!LootRush.getMessages().get("respawn-message").equalsIgnoreCase(""))
            player.sendMessage(LootRush.getMessages().get("respawn-message").replace("{seconds}", String.valueOf(LootRush.getRespawnInternal())).replace("{second/seconds}", (LootRush.getRespawnInternal() == 1) ? "second" : "seconds"));

        final List<ItemStack> newContents = new ArrayList<>();
        final List<ItemStack> contentsForDamager = new ArrayList<>();

        ItemStack modifiedItem;
        for (ItemStack item: player.getInventory().getContents()) {

            if (item == null) continue;

            modifiedItem = item.clone();
            modifiedItem.setAmount(1);

            if (LootRush.getGeneratorItems().containsKey(modifiedItem)) {
                contentsForDamager.add(item);
                continue;
            }

            newContents.add(item);

        }

        player.getInventory().setContents(newContents.toArray(new ItemStack[0]));

        new BukkitRunnable() {

            int interval = LootRush.getRespawnInternal();

            @Override
            public void run() {

                if (LootRush.getEventStatus() == EventStatus.INACTIVE) {
                    cancel();
                } else {

                    if (interval != 1) {
                        if (!LootRush.getMessages().get("respawn-message").equalsIgnoreCase(""))
                            player.sendMessage(LootRush.getMessages().get("respawn-message").replace("{seconds}", String.valueOf(interval - 1)).replace("{second/seconds}", (interval - 1 == 1) ? "second" : "seconds"));
                    }

                    interval--;

                    if (interval == 0) {

                        Location location = (LootRush.getEventType() == EventType.SOLO) ? LootRush.getRoundManager().getSoloPlayer(player).getLocation() : LootRush.getRoundManager().getTeamOfPlayer(player).getLocation();

                        player.teleport(location);
                        player.setHealth(20);
                        player.setFoodLevel(20);

                        player.setGameMode(GameMode.SURVIVAL);

                        if (!LootRush.getMessages().get("respawned-message").equalsIgnoreCase(""))
                            player.sendMessage(LootRush.getMessages().get("respawned-message"));

                        cancel();

                    }

                }

            }
        }.runTaskTimer(LootRush.getINSTANCE(), 20L, 20L);

        if (!(event.getDamager() instanceof Player)) {
            if (!LootRush.getMessages().get("death-message").equalsIgnoreCase(""))
                Bukkit.broadcastMessage(LootRush.getMessages().get("death-message").replace("{player}", player.getDisplayName()));
            return;
        }

        Player damager = (Player) event.getDamager();

        if (!LootRush.getMessages().get("kill-message").equalsIgnoreCase(""))
            Bukkit.broadcastMessage(LootRush.getMessages().get("kill-message").replace("{victim}", player.getDisplayName()).replace("{killer}", damager.getDisplayName()).replace("{victim_team_color_code}", victimColorCode.replace("&", "§")).replace("{killer_team_color_code}", damagerColorCode.replace("&", "§")));

        if (LootRush.getEventType() == EventType.NONE) return;

        int newCoins;
        if (LootRush.getEventType() == EventType.SOLO) {

            SoloPlayer spDamager = LootRush.getRoundManager().getSoloPlayer(damager);

            if (spDamager == null) return;

            newCoins = spDamager.getCoins() + LootRush.getCoinsPerKill();
            LootRush.getRoundManager().getPlayers().get(spDamager.getPlayer().getDisplayName()).setCoins(newCoins);

        } else if (LootRush.getEventType() == EventType.TEAM) {

            Team teamDamager = LootRush.getRoundManager().getTeamOfPlayer(damager);

            if (teamDamager == null) return;

            newCoins = teamDamager.getPlayerCoins(damager) + LootRush.getCoinsPerKill();
            LootRush.getRoundManager().getTeams().get(teamDamager.getColor()).setPlayerCoins(damager, newCoins);

        }

        for (ItemStack itemForDamager: contentsForDamager) {
            damager.getInventory().addItem(itemForDamager);
        }

        if (!LootRush.getMessages().get("coin-score").equalsIgnoreCase(""))
            damager.sendMessage(LootRush.getMessages().get("coin-score").replace("{coins}", String.valueOf(LootRush.getCoinsPerKill())));

    }

}
