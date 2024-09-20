package fatalflare.elytrapitch;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerEntity;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

public class ElytraPitch implements ModInitializer {
    public static final String MOD_ID = "elytrapitch";
	public static final String MOD_NAME = "Elytra Pitch";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static KeyBinding keyBinding1;
	private static KeyBinding keyBinding2;
	private static KeyBinding keyBinding3;
	private static KeyBinding keyBinding4;
	private static KeyBinding keyBinding5;
	private static boolean togglePitch = true;
	private static boolean pitchLocked = false;
	private static boolean inFlight = false;
	private static float lockedPitch;

	@Override
	public void onInitialize() {
		AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
		ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

		keyBinding1 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Toggle Pitch",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_H,
				MOD_NAME
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyBinding1.wasPressed()) {
				togglePitch = !togglePitch;
				String message = togglePitch ? "Flight HUD Visible" : "Flight HUD Hidden";
				if (client.player != null)
					client.player.sendMessage(Text.literal(message), true);
			}
		});

		keyBinding2 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Lock Pitch",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_R,
				MOD_NAME
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyBinding2.wasPressed()) {
				pitchLocked = !pitchLocked;
				String message = pitchLocked ? "Flight Pitch Locked" : "Flight Pitch Unlocked";
				if (client.player != null && config.assistedFlight) {
					if (pitchLocked)
						lockedPitch = client.player.getPitch();
					if (client.player != null)
						client.player.sendMessage(Text.literal(message), true);
				}
			}
		});

		keyBinding3 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Snap to Ascend",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_B,
				MOD_NAME
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyBinding3.wasPressed()) {
				if (client.player != null && config.assistedFlight && client.player.isFallFlying())
					if (pitchLocked) lockedPitch = config.ascendAngle;
					else client.player.setPitch(config.ascendAngle);
			}
		});

		keyBinding4 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Snap to Descend",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_V,
				MOD_NAME
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyBinding4.wasPressed()) {
				if (client.player != null && config.assistedFlight && client.player.isFallFlying())
					if (pitchLocked) lockedPitch = config.descendAngle;
					else client.player.setPitch(config.descendAngle);
			}
		});

		keyBinding5 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Snap to Glide",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_N,
				MOD_NAME
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyBinding5.wasPressed()) {
				if (client.player != null && config.assistedFlight && client.player.isFallFlying())
					if (pitchLocked) lockedPitch = config.glideAngle;
					else client.player.setPitch(config.glideAngle);
			}
		});

		DecimalFormat df = new DecimalFormat("#.#");
		HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
			PlayerEntity player = MinecraftClient.getInstance().player;
			if (player == null || !player.isFallFlying() || !togglePitch) {
				if (inFlight) inFlight = false;
				return;
			}
			if (config.assistedFlight && pitchLocked) {
				if (!inFlight) {
					lockedPitch = player.getPitch();
					inFlight = true;
				}
				player.setPitch(lockedPitch);
			}

			MinecraftClient minecraft = MinecraftClient.getInstance();
			ScreenPosition screenPosition;
			int textColor;
			boolean optimalIndicator, textShadow, showYaw, showVelocity, showAltitude, showDirection, showDurability;
			if (minecraft.gameRenderer.getCamera().isThirdPerson()) {
				screenPosition = config.screenPositionTP;
				optimalIndicator = config.optimalIndicatorTP;
				textColor = config.textColorTP;
				textShadow = config.textShadowTP;
				showYaw = config.showYawTP;
				showVelocity = config.showVelocityTP;
				showAltitude = config.showAltitudeTP;
				showDirection = config.showDirectionTP;
				showDurability = config.showDurabilityTP;
			} else {
				screenPosition = config.screenPositionFP;
				optimalIndicator = config.optimalIndicatorFP;
				textColor = config.textColorFP;
				textShadow = config.textShadowFP;
				showYaw = config.showYawFP;
				showVelocity = config.showVelocityFP;
				showAltitude = config.showAltitudeFP;
				showDirection = config.showDirectionFP;
				showDurability = config.showDurabilityFP;
			}

			int pitch = (int) player.getPitch();
			String displayString = pitch + "°";
			if (config.assistedFlight && pitchLocked) {
				displayString = "(" + displayString + ")";
			} if (showYaw) {
				int yaw = (int) player.getYaw() % 180;
				if (Math.ceil(Math.abs(player.getYaw() / 180)) % 2 == 0) {
					if (yaw > 0)
						yaw -= 180;
					else
						yaw += 180;
				}
				displayString += " " + yaw + "°";
			} if (showAltitude) {
				int altitude = (int) player.getY() - player.getWorld().getSeaLevel();
				displayString += " " + altitude + "m";
			} if (showVelocity) {
				int velocity = (int) (player.getVelocity().length() * 20);
				displayString += " " + velocity + "㎧";
			} if (showDirection) {
				switch (player.getMovementDirection().getName()) {
					case "north":
						displayString += " N";
						break;
					case "south":
						displayString += " S";
						break;
					case "east":
						displayString += " E";
						break;
					case "west":
						displayString += " W";
						break;
				}
			} if (showDurability) {
				ItemStack elytraItem = null;
				for (ItemStack stack : player.getArmorItems()) {
					if (stack.getName().getString().equals("Elytra"))
						elytraItem = stack;
				}
				if (elytraItem != null) {
					double durability = (double) (elytraItem.getMaxDamage() - elytraItem.getDamage()) / elytraItem.getMaxDamage();
					if (durability < config.durabilityWarning) {
						MutableText text = Text.literal("Elytra Durability Warning");
						text.setStyle(text.getStyle().withColor(config.warningTextColor).withBold(true));
						player.sendMessage(text, true);
					}
					displayString += " " + df.format(durability * 100) + "%";
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

			drawContext.drawText(textRenderer, displayString, xPos, yPos, textColor, textShadow);
		});
	}
}