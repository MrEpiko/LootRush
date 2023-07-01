package me.mrepiko.lootrush.manager;

import lombok.Getter;
import lombok.Setter;
import me.mrepiko.lootrush.LootRush;

public enum RoundManagerOutput {

    PLAYER_NOT_FOUND("§e§lLoot§6§lRush §e| §7Player not found."),
    SUCCESS("§e§lLoot§6§lRush §e| §7Action successfully done."),
    MAXIMUM_PLAYERS_REACHED(LootRush.getMessages().get("player-cap-reached")),
    ALREADY_REGISTERED (LootRush.getMessages().get("already-registered")),
    INVALID_TYPE("§e§lLoot§6§lRush §e| §7Invalid event type selected."),
    EVENT_DIDNT_START(LootRush.getMessages().get("event-not-started"));

    @Getter @Setter private String message;

    RoundManagerOutput(String message) {
        this.message = message;
    }

}
