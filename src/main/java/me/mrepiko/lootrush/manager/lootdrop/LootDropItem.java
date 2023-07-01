package me.mrepiko.lootrush.manager.lootdrop;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public class LootDropItem {

    @Getter private final ItemStack itemStack;
    @Getter private final int spotsMin;
    @Getter private final int spotsMax;
    @Getter private final int amountMin;
    @Getter private final int amountMax;

    public LootDropItem(ItemStack itemStack, int spotsMin, int spotsMax, int amountMin, int amountMax) {
        this.itemStack = itemStack;
        this.spotsMin = spotsMin;
        this.spotsMax = spotsMax;
        this.amountMin = amountMin;
        this.amountMax = amountMax;
    }

}
