package fatalflare.elytrapitch;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Optional;

public class TrinketsIntegration extends TrinketsApi {

    public static ItemStack getElytraItem(Player player) {
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
        if (component.isPresent())
            for (Tuple<SlotReference, ItemStack> pair: component.get().getEquipped(Items.ELYTRA))
                return pair.getB();
        return ItemStack.EMPTY;
    }
}