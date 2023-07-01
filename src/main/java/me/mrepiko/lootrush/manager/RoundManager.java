package me.mrepiko.lootrush.manager;

import lombok.Getter;
import me.mrepiko.lootrush.EventStatus;
import me.mrepiko.lootrush.EventType;
import me.mrepiko.lootrush.LootRush;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class RoundManager {

    @Getter private final HashMap<String, Team> teams = new HashMap<>();
    @Getter private final HashMap<String, SoloPlayer> players = new HashMap<>();

    private int teamsIndex = 0;
    private int playersIndex = 0;

    public void clear() {
        this.teams.clear();
        this.players.clear();
        this.teamsIndex = 0;
        this.playersIndex = 0;
    }

    public Team getTeamOfPlayer(Player player) {

        for (Team tp: this.teams.values()) {

            for (Player p: tp.getPlayers()) {

                if (p.getDisplayName().equalsIgnoreCase(player.getDisplayName())) {
                    return tp;
                }

            }

        }

        return null;

    }

    public SoloPlayer getSoloPlayer(Player player) {

        for (SoloPlayer sp: this.players.values()) {
            if (sp.getPlayer().getDisplayName().equalsIgnoreCase(player.getDisplayName())) {
                return sp;
            }
        }

        return null;

    }

    public RoundManagerOutput addPlayer(Player player) {

        if (LootRush.getEventStatus() != EventStatus.REGISTRATIONS) return RoundManagerOutput.EVENT_DIDNT_START;

        if (LootRush.getEventType() != EventType.SOLO) return RoundManagerOutput.INVALID_TYPE;

        if (this.players.containsKey(player.getDisplayName())) return RoundManagerOutput.ALREADY_REGISTERED;

        if (this.players.size() >= LootRush.getMaximumPlayers()) return RoundManagerOutput.MAXIMUM_PLAYERS_REACHED;

        this.players.put(player.getDisplayName(), new SoloPlayer(player, LootRush.getPlayerConfigs().get(this.playersIndex).getLocation()));
        playersIndex++;

        return RoundManagerOutput.SUCCESS;

    }

    public RoundManagerOutput removePlayer(Player player) {

        if (LootRush.getEventStatus() == EventStatus.INACTIVE) return RoundManagerOutput.EVENT_DIDNT_START;

        if (LootRush.getEventType() != EventType.SOLO) return RoundManagerOutput.INVALID_TYPE;

        if (!this.players.containsKey(player.getDisplayName())) return RoundManagerOutput.PLAYER_NOT_FOUND;

        this.players.remove(player.getDisplayName());
        this.playersIndex--;

        return RoundManagerOutput.SUCCESS;

    }

    public RoundManagerOutput addPlayerToTeam(Player player) {

        if (LootRush.getEventStatus() != EventStatus.REGISTRATIONS) return RoundManagerOutput.EVENT_DIDNT_START;

        if (LootRush.getEventType() != EventType.TEAM) return RoundManagerOutput.INVALID_TYPE;

        for (Team t: this.teams.values()) {
            if (t.getPlayers().contains(player)) return RoundManagerOutput.ALREADY_REGISTERED;
        }

        if (this.teams.keySet().stream().mapToInt(c -> this.teams.get(c).getPlayers().size()).sum() >= LootRush.getTeamConfigs().size() * LootRush.getMaximumPlayers()) return RoundManagerOutput.MAXIMUM_PLAYERS_REACHED;

        if (this.teams.size() == 0) {
            this.teams.put(LootRush.getTeamConfigs().get(this.teamsIndex).getColor(), new Team(player, LootRush.getTeamConfigs().get(this.teamsIndex).getLocation(), LootRush.getTeamConfigs().get(this.teamsIndex).getColor(), LootRush.getTeamConfigs().get(this.teamsIndex).getColorName(), LootRush.getTeamConfigs().get(this.teamsIndex).getColorCode()));
            this.teamsIndex++;
            return RoundManagerOutput.SUCCESS;
        }

        int i = 0;


        for (String c: this.teams.keySet()) {


            if (this.teams.get(c).getPlayers().size() >= LootRush.getMaximumPlayers()) {


                if (i == this.teams.size() - 1) {


                    this.teams.put(LootRush.getTeamConfigs().get(this.teamsIndex).getColor(), new Team(player, LootRush.getTeamConfigs().get(this.teamsIndex).getLocation(), LootRush.getTeamConfigs().get(this.teamsIndex).getColor(), LootRush.getTeamConfigs().get(this.teamsIndex).getColorName(), LootRush.getTeamConfigs().get(this.teamsIndex).getColorCode()));
                    this.teamsIndex++;


                    break;

                }

            } else {
                this.teams.get(c).addPlayer(player);
                break;
            }


            i += 1;

        }


        return RoundManagerOutput.SUCCESS;

    }

    public RoundManagerOutput removePlayerFromTeam(Player player) {

        if (LootRush.getEventStatus() == EventStatus.INACTIVE) return RoundManagerOutput.EVENT_DIDNT_START;

        if (LootRush.getEventType() != EventType.TEAM) return RoundManagerOutput.INVALID_TYPE;

        if (this.teams.size() == 0) return RoundManagerOutput.PLAYER_NOT_FOUND;

        for (String c: this.teams.keySet()) {

            for (Player p: this.teams.get(c).getPlayers()) {

                if (p.getDisplayName().equalsIgnoreCase(player.getDisplayName())) {
                    this.teams.get(c).removePlayer(player);
                    this.teamsIndex--;

                    if (this.teams.get(c).getPlayers().size() == 0) {
                        this.teams.remove(c);
                    }

                    return RoundManagerOutput.SUCCESS;
                }

            }

        }

        return RoundManagerOutput.PLAYER_NOT_FOUND;

    }

}
