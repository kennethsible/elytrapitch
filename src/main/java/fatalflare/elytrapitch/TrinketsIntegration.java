package fatalflare.elytrapitch;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;

import java.util.Optional;

public class TrinketsIntegration extends TrinketsApi {

    public static ItemStack getElytraItem(PlayerEntity player) {
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
        if (component.isPresent())
            for (Pair<SlotReference, ItemStack> pair: component.get().getEquipped(Items.ELYTRA))
                return pair.getRight();
        return null;
    }
}
