package fatalflare.elytrapitch;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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

	private static KeyBinding keyBinding1, keyBinding2, keyBinding3, keyBinding4, keyBinding5;

	private ModConfig config;

	private static final Identifier ELYTRA_PITCH_LAYER = Identifier.of(MOD_ID, "elytra-pitch-layer");
	private static final KeyBinding.Category ELYTRA_PITCH_CATEGORY = KeyBinding.Category.create(ELYTRA_PITCH_LAYER);

	@Override
	public void onInitialize() {
		AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
		config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
		registerKeyBindings();
		registerTickEvents();
		HudElementRegistry.addLast(ELYTRA_PITCH_LAYER, this::render);
	}

	private void registerKeyBindings() {
		keyBinding1 = KeyBindingHelper.registerKeyBinding(new KeyBinding("Toggle Flight HUD", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, ELYTRA_PITCH_CATEGORY));
		keyBinding2 = KeyBindingHelper.registerKeyBinding(new KeyBinding("Toggle Pitch Lock", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, ELYTRA_PITCH_CATEGORY));
		keyBinding3 = KeyBindingHelper.registerKeyBinding(new KeyBinding("Snap to Ascend", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, ELYTRA_PITCH_CATEGORY));
		keyBinding4 = KeyBindingHelper.registerKeyBinding(new KeyBinding("Snap to Descend", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, ELYTRA_PITCH_CATEGORY));
		keyBinding5 = KeyBindingHelper.registerKeyBinding(new KeyBinding("Snap to Glide", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, ELYTRA_PITCH_CATEGORY));
	}

	private void registerTickEvents() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyBinding1.wasPressed()) {
				togglePitch = !togglePitch;
				String message = togglePitch ? "Flight HUD Visible" : "Flight HUD Hidden";
				if (client.player != null) client.player.sendMessage(Text.literal(message), true);
			}
			while (keyBinding2.wasPressed()) {
				pitchLocked = !pitchLocked;
				String message = pitchLocked ? "Flight Pitch Locked" : "Flight Pitch Unlocked";
				if (client.player != null) {
					if (pitchLocked) lockedPitch = client.player.getPitch();
					client.player.sendMessage(Text.literal(message), true);
				}
			}
			while (keyBinding3.wasPressed()) {
				if (client.player != null && client.player.isGliding()) {
					if (pitchLocked) lockedPitch = config.ascendAngle;
					else client.player.setPitch(config.ascendAngle);
				}
			}
			while (keyBinding4.wasPressed()) {
				if (client.player != null && client.player.isGliding()) {
					if (pitchLocked) lockedPitch = config.descendAngle;
					else client.player.setPitch(config.descendAngle);
				}
			}
			while (keyBinding5.wasPressed()) {
				if (client.player != null && client.player.isGliding()) {
					if (pitchLocked) lockedPitch = config.glideAngle;
					else client.player.setPitch(config.glideAngle);
				}
			}

			PlayerEntity player = client.player;
			if (player == null) return;

			if (pitchLocked && player.isGliding()) {
				if (!inFlight) {
					lockedPitch = player.getPitch();
					inFlight = true;
				}
				player.setPitch(lockedPitch);
			} else if (!player.isGliding() && inFlight) {
				inFlight = false;
			}
		});
	}

	private void render(DrawContext drawContext, RenderTickCounter tickCounter) {
		MinecraftClient minecraft = MinecraftClient.getInstance();
		PlayerEntity player = minecraft.player;

		if (player == null || !player.isGliding() || !togglePitch) {
			return;
		}

		ScreenPosition screenPosition;
		String hudDelimiter;
		int textColor;
		boolean optimalIndicator, textShadow, showYaw, showVelocity, showAltitude, showDirection, showDurability, showElytraItem;

		if (minecraft.gameRenderer.getCamera().isThirdPerson()) {
			screenPosition = config.screenPositionTP;
			optimalIndicator = config.optimalIndicatorTP;
			textColor = config.textColorTP | 0xFF000000;;
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
			textColor = config.textColorFP | 0xFF000000;;
			textShadow = config.textShadowFP;
			hudDelimiter = config.hudDelimiterFP;
			showYaw = config.showYawFP;
			showVelocity = config.showVelocityFP;
			showAltitude = config.showAltitudeFP;
			showDirection = config.showDirectionFP;
			showDurability = config.showDurabilityFP;
			showElytraItem = config.showElytraItemFP;
		}

		int pitch = (int) player.getPitch();
		String displayString = pitch + "°";
		String delimiterString = hudDelimiter.isEmpty() ? " " : " " + hudDelimiter + " ";
		if (pitchLocked) {
			displayString = "(" + displayString + ")";
		} if (showYaw) {
			int yaw = (int) player.getYaw() % 180;
			if (Math.ceil(Math.abs(player.getYaw() / 180)) % 2 == 0) {
				if (yaw > 0) yaw -= 180;
				else yaw += 180;
			}
			displayString += delimiterString + yaw + "°";
		} if (showAltitude) {
			int altitude = (int) player.getY() - player.getEntityWorld().getSeaLevel();
			displayString += delimiterString + altitude + "m";
		} if (showVelocity) {
			int velocity = (int) (player.getVelocity().length() * 20);
			displayString += delimiterString + velocity + "㎧";
		} if (showDirection) {
			displayString += delimiterString + player.getMovementDirection().name().substring(0, 1).toUpperCase();
		}

		ItemStack elytraItem = ItemStack.EMPTY;
		if (FabricLoader.getInstance().isModLoaded("trinkets")) {
			elytraItem = TrinketsIntegration.getElytraItem(player);
		}
//		if (FabricLoader.getInstance().isModLoaded("accessories")) {
//			elytraItem = AccessoriesIntegration.getElytraItem(player);
//		}
		if (elytraItem.isEmpty()) {
			ItemStack chestItem = player.getEquippedStack(EquipmentSlot.CHEST);
			if (chestItem.isOf(Items.ELYTRA)) {
				elytraItem = chestItem;
			}
		}
		if (!elytraItem.isEmpty()) {
			double durability = (double) (elytraItem.getMaxDamage() - elytraItem.getDamage()) / elytraItem.getMaxDamage();
			if (durability < config.durabilityThreshold) {
				MutableText text = Text.literal("Elytra Durability Warning");
				text.setStyle(text.getStyle().withColor(config.messageColor));
				if (config.boldMessage) text.setStyle(text.getStyle().withBold(true));
				player.sendMessage(text, true);
			}
			if (showDurability) {
				if (config.durabilityPercentage)
					displayString += delimiterString + String.format("%.1f", durability * 100) + "%";
				else if (elytraItem.getDamage() > 0)
					displayString += delimiterString + "-" + elytraItem.getDamage();
				else
					displayString += delimiterString + elytraItem.getDamage();
			}
		}

		int optimalPitch = pitch > 0 ? config.descendAngle : config.ascendAngle;
		if (optimalIndicator && Math.abs(Math.abs(pitch) - Math.abs(optimalPitch)) <= config.indicatorWidth)
			displayString = "[ " + displayString + " ]";

		Window mainWindow = minecraft.getWindow();
		TextRenderer textRenderer = minecraft.textRenderer;
		int xWidth = minecraft.textRenderer.getWidth(displayString);
		int yHeight = minecraft.textRenderer.fontHeight;
		int xPos = (mainWindow.getScaledWidth() - xWidth) / 2;
		int yPos = switch (screenPosition) {
			case BOTTOM_CENTER -> mainWindow.getScaledHeight() - yHeight * 10;
			case MIDDLE_CENTER -> mainWindow.getScaledHeight() / 2 - yHeight * 5;
			case TOP_CENTER -> yHeight * 5;
		};

		if (showElytraItem && !elytraItem.isEmpty())
			drawContext.drawItem(elytraItem, (mainWindow.getScaledWidth() - 16) / 2, yPos - 16);
		drawContext.drawText(textRenderer, displayString, xPos, yPos, textColor, textShadow);
	}
}