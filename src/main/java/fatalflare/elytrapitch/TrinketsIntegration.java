package fatalflare.elytrapitch;

import eu.pb4.trinkets.api.TrinketAttachment;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TrinketsIntegration {

    public static ItemStack getElytraItem(Player player) {
        TrinketAttachment attachment = TrinketsApi.getAttachment(player);
        for (TrinketSlotAccess slot : attachment.equipped(Items.ELYTRA, true)) {
            return slot.get();
        }
        return ItemStack.EMPTY;
    }
}