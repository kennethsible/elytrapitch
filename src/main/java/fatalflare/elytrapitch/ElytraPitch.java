package fatalflare.elytrapitch;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElytraPitch implements ModInitializer {
	public static final String MOD_ID = "elytrapitch";
	public static final String MOD_NAME = "Elytra Pitch";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private boolean togglePitch = true;
	private boolean pitchLocked = false;
	private boolean inFlight = false;
	private float lockedPitch;

	private static KeyMapping keyBinding1, keyBinding2, keyBinding3, keyBinding4, keyBinding5;

	private ModConfig config;

	private static final Identifier ELYTRA_PITCH_LAYER = Identifier.fromNamespaceAndPath(MOD_ID, "elytra-pitch-layer");
	private static final KeyMapping.Category ELYTRA_PITCH_CATEGORY = KeyMapping.Category.register(ELYTRA_PITCH_LAYER);

	@Override
	public void onInitialize() {
		AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
		config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
		registerKeyBindings();
		registerTickEvents();
		HudElementRegistry.addLast(ELYTRA_PITCH_LAYER, this::render);
	}

	private void registerKeyBindings() {
		keyBinding1 = KeyMappingHelper.registerKeyMapping(new KeyMapping("Toggle Flight HUD", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, ELYTRA_PITCH_CATEGORY));
		keyBinding2 = KeyMappingHelper.registerKeyMapping(new KeyMapping("Toggle Pitch Lock", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, ELYTRA_PITCH_CATEGORY));
		keyBinding3 = KeyMappingHelper.registerKeyMapping(new KeyMapping("Snap to Ascend", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, ELYTRA_PITCH_CATEGORY));
		keyBinding4 = KeyMappingHelper.registerKeyMapping(new KeyMapping("Snap to Descend", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, ELYTRA_PITCH_CATEGORY));
		keyBinding5 = KeyMappingHelper.registerKeyMapping(new KeyMapping("Snap to Glide", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, ELYTRA_PITCH_CATEGORY));
	}

	private void registerTickEvents() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyBinding1.consumeClick()) {
				togglePitch = !togglePitch;
				String message = togglePitch ? "Flight HUD Visible" : "Flight HUD Hidden";
				if (client.player != null) client.player.sendOverlayMessage(Component.literal(message));
			}
			while (keyBinding2.consumeClick()) {
				pitchLocked = !pitchLocked;
				String message = pitchLocked ? "Flight Pitch Locked" : "Flight Pitch Unlocked";
				if (client.player != null) {
					if (pitchLocked) lockedPitch = client.player.getXRot();
					client.player.sendOverlayMessage(Component.literal(message));
				}
			}
			while (keyBinding3.consumeClick()) {
				if (client.player != null && client.player.isFallFlying()) {
					if (pitchLocked) lockedPitch = config.ascendAngle;
					else client.player.setXRot(config.ascendAngle);
				}
			}
			while (keyBinding4.consumeClick()) {
				if (client.player != null && client.player.isFallFlying()) {
					if (pitchLocked) lockedPitch = config.descendAngle;
					else client.player.setXRot(config.descendAngle);
				}
			}
			while (keyBinding5.consumeClick()) {
				if (client.player != null && client.player.isFallFlying()) {
					if (pitchLocked) lockedPitch = config.glideAngle;
					else client.player.setXRot(config.glideAngle);
				}
			}

			Player player = client.player;
			if (player == null) return;

			if (pitchLocked && player.isFallFlying()) {
				if (!inFlight) {
					lockedPitch = player.getXRot();
					inFlight = true;
				}
				player.setXRot(lockedPitch);
			} else if (!player.isFallFlying() && inFlight) {
				inFlight = false;
			}
		});
	}

	private void render(GuiGraphicsExtractor drawContext, DeltaTracker tickCounter) {
		Minecraft minecraft = Minecraft.getInstance();
		Player player = minecraft.player;

		if (player == null || !player.isFallFlying() || !togglePitch) {
			return;
		}

		ScreenPosition screenPosition;
		String hudDelimiter;
		int textColor;
		boolean optimalIndicator, textShadow, showYaw, showVelocity, showAltitude, showDirection, showDurability, showElytraItem;

		if (minecraft.gameRenderer.getMainCamera().isDetached()) {
			screenPosition = config.screenPositionTP;
			optimalIndicator = config.optimalIndicatorTP;
			textColor = config.textColorTP | 0xFF000000;
			textShadow = config.textShadowTP;
			hudDelimiter = config.hudDelimiterTP;
			showYaw = config.showYawTP;
			showVelocity = config.showVelocityTP;
			showAltitude = config.showAltitudeTP;
			showDirection = config.showDirectionTP;
			showDurability = config.showDurabilityTP;
			showElytraItem = config.showElytraItemTP;
		} else {
			screenPosition = config.screenPositionFP;
			optimalIndicator = config.optimalIndicatorFP;
			textColor = config.textColorFP | 0xFF000000;
			textShadow = config.textShadowFP;
			hudDelimiter = config.hudDelimiterFP;
			showYaw = config.showYawFP;
			showVelocity = config.showVelocityFP;
			showAltitude = config.showAltitudeFP;
			showDirection = config.showDirectionFP;
			showDurability = config.showDurabilityFP;
			showElytraItem = config.showElytraItemFP;
		}

		int pitch = (int) player.getXRot();
		String displayString = pitch + "°";
		String delimiterString = hudDelimiter.isEmpty() ? " " : " " + hudDelimiter + " ";
		if (pitchLocked) {
			displayString = "(" + displayString + ")";
		} if (showYaw) {
			int yaw = (int) player.getYRot() % 180;
			if (Math.ceil(Math.abs(player.getYRot() / 180)) % 2 == 0) {
				if (yaw > 0) yaw -= 180;
				else yaw += 180;
			}
			displayString += delimiterString + yaw + "°";
		} if (showAltitude) {
			int altitude = (int) player.getY() - player.level().getSeaLevel();
			displayString += delimiterString + altitude + "m";
		} if (showVelocity) {
			int velocity = (int) (player.getDeltaMovement().length() * 20);
			displayString += delimiterString + velocity + "㎧";
		} if (showDirection) {
			displayString += delimiterString + player.getMotionDirection().name().substring(0, 1).toUpperCase();
		}

		ItemStack elytraItem = ItemStack.EMPTY;
		if (FabricLoader.getInstance().isModLoaded("trinkets")) {
			elytraItem = TrinketsIntegration.getElytraItem(player);
		}
//		if (FabricLoader.getInstance().isModLoaded("accessories")) {
//			elytraItem = AccessoriesIntegration.getElytraItem(player);
//		}
		if (elytraItem.isEmpty()) {
			ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);
			if (chestItem.is(Items.ELYTRA)) {
				elytraItem = chestItem;
			}
		}
		if (!elytraItem.isEmpty()) {
			double durability = (double) (elytraItem.getMaxDamage() - elytraItem.getDamageValue()) / elytraItem.getMaxDamage();
			if (durability < config.durabilityThreshold) {
				MutableComponent text = Component.literal("Elytra Durability Warning");
				text.setStyle(text.getStyle().withColor(config.messageColor));
				if (config.boldMessage) text.setStyle(text.getStyle().withBold(true));
				player.sendOverlayMessage(text);
			}
			if (showDurability) {
				if (config.durabilityPercentage)
					displayString += delimiterString + String.format("%.1f", durability * 100) + "%";
				else if (elytraItem.getDamageValue() > 0)
					displayString += delimiterString + "-" + elytraItem.getDamageValue();
				else
					displayString += delimiterString + elytraItem.getDamageValue();
			}
		}

		int optimalPitch = pitch > 0 ? config.descendAngle : config.ascendAngle;
		if (optimalIndicator && Math.abs(Math.abs(pitch) - Math.abs(optimalPitch)) <= config.indicatorWidth)
			displayString = "[ " + displayString + " ]";

		Window mainWindow = minecraft.getWindow();
		Font textRenderer = minecraft.font;
		int xWidth = minecraft.font.width(displayString);
		int yHeight = minecraft.font.lineHeight;
		int xPos = (mainWindow.getGuiScaledWidth() - xWidth) / 2;
		int yPos = switch (screenPosition) {
			case BOTTOM_CENTER -> mainWindow.getGuiScaledHeight() - yHeight * 10;
			case MIDDLE_CENTER -> mainWindow.getGuiScaledHeight() / 2 - yHeight * 5;
			case TOP_CENTER -> yHeight * 5;
		};

		if (showElytraItem && !elytraItem.isEmpty())
			drawContext.item(elytraItem, (mainWindow.getGuiScaledWidth() - 16) / 2, yPos - 16);
		drawContext.text(textRenderer, displayString, xPos, yPos, textColor, textShadow);
	}
}