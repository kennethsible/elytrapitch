//package fatalflare.elytrapitch;
//
//import io.wispforest.accessories.api.AccessoriesCapability;
//import io.wispforest.accessories.api.slot.SlotEntryReference;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.Items;
//
//public class AccessoriesIntegration {
//
//    public static ItemStack getElytraItem(PlayerEntity player) {
//        AccessoriesCapability capability = AccessoriesCapability.get(player);
//        if (capability != null) {
//            SlotEntryReference reference = capability.getFirstEquipped(Items.ELYTRA);
//            if (reference != null)
//                return reference.stack();
//        }
//        return ItemStack.EMPTY;
//    }
//}