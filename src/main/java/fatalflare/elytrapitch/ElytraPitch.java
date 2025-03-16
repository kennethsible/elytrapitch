package fatalflare.elytrapitch;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerEntity;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private double startHeight;
	private double oldApex;
	private int oldApexDiff;
	private double oldYVelocity;

	@Override
	public void onInitialize() {
		AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
		ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

		keyBinding1 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Toggle Flight HUD",
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
				"Toggle Pitch Lock",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				MOD_NAME
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyBinding2.wasPressed()) {
				pitchLocked = !pitchLocked;
				String message = pitchLocked ? "Flight Pitch Locked" : "Flight Pitch Unlocked";
				if (client.player != null) {
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
				GLFW.GLFW_KEY_UNKNOWN,
				MOD_NAME
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyBinding3.wasPressed()) {
				if (client.player != null && client.player.isFallFlying())
					if (pitchLocked) lockedPitch = config.ascendAngle;
					else client.player.setPitch(config.ascendAngle);
			}
		});

		keyBinding4 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Snap to Descend",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				MOD_NAME
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyBinding4.wasPressed()) {
				if (client.player != null && client.player.isFallFlying())
					if (pitchLocked) lockedPitch = config.descendAngle;
					else client.player.setPitch(config.descendAngle);
			}
		});

		keyBinding5 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Snap to Glide",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				MOD_NAME
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyBinding5.wasPressed()) {
				if (client.player != null && client.player.isFallFlying())
					if (pitchLocked) lockedPitch = config.glideAngle;
					else client.player.setPitch(config.glideAngle);
			}
		});

		HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
			PlayerEntity player = MinecraftClient.getInstance().player;
			if (player == null || !player.isFallFlying() || !togglePitch) {
				if (inFlight) inFlight = false;
				if (player != null){
					startHeight = player.getY();
					oldApex = player.getY();
				}
				return;
			}
			if (pitchLocked) {
				if (!inFlight) {
					lockedPitch = player.getPitch();
					inFlight = true;
				}
				player.setPitch(lockedPitch);
			}

			MinecraftClient minecraft = MinecraftClient.getInstance();
			ScreenPosition screenPosition;
			String hudDelimiter;
			int textColor;
			boolean optimalIndicator, textShadow, showYaw, showVelocity, showAltitude, showDirection, showDurability, showElytraItem;
			if (minecraft.gameRenderer.getCamera().isThirdPerson()) {
				screenPosition = config.screenPositionTP;
				optimalIndicator = config.optimalIndicatorTP;
				textColor = config.textColorTP;
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
				textColor = config.textColorFP;
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
					if (yaw > 0)
						yaw -= 180;
					else
						yaw += 180;
				}
				displayString += delimiterString + yaw + "°";
			} if (showAltitude) {
				int altitude = (int) player.getY() - player.getWorld().getSeaLevel();
				displayString += delimiterString + altitude + "m";
			} if (showVelocity) {
				int velocity = (int) (player.getVelocity().length() * 20);
				displayString += delimiterString + velocity + "㎧";
			} if (showDirection) {
				switch (player.getMovementDirection().getName()) {
					case "north":
						displayString += delimiterString + "N";
						break;
					case "south":
						displayString += delimiterString + "S";
						break;
					case "east":
						displayString += delimiterString + "E";
						break;
					case "west":
						displayString += delimiterString + "W";
						break;
				}
			}



			ItemStack elytraItem = null;
			if (FabricLoader.getInstance().isModLoaded("trinkets"))
				elytraItem = TrinketsIntegration.getElytraItem(player);
			if (elytraItem == null)
				for (ItemStack stack : player.getArmorItems())
					if (stack.getItem() == Items.ELYTRA)
						elytraItem = stack;
			if (elytraItem != null) {
				double durability = (double) (elytraItem.getMaxDamage() - elytraItem.getDamage()) / elytraItem.getMaxDamage();
				if (durability < config.durabilityThreshold) {
					MutableText text = Text.literal("Elytra Durability Warning");
					text.setStyle(text.getStyle().withColor(config.messageColor));
					if (config.boldMessage)
						text.setStyle(text.getStyle().withBold(true));
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
			MutableText displayText = Text.literal(displayString);
			MutableText advancedStatsText = Text.empty();
			if(config.showLastApexGain|| config.showLastApexHeight || config.showOverallGain){
				double yVelocity = player.getVelocity().getY();
				if(config.showLastApexHeight) advancedStatsText.append(Text.literal(delimiterString + (int) oldApex).formatted(Formatting.GRAY));
				if(config.showLastApexGain){
					Formatting apexColor = oldApexDiff >= 0 ? Formatting.GREEN : Formatting.RED;
					String apexIndicator = oldApexDiff >= 0 ? "+" : "";
					advancedStatsText.append(Text.literal(delimiterString + apexIndicator + oldApexDiff).formatted(apexColor));
				}
				if(config.showOverallGain){
					Formatting startColor = oldApex-startHeight >= 0 ? Formatting.GREEN : Formatting.RED;
					String startIndicator = oldApex-startHeight >= 0 ? "+" : "";
					advancedStatsText.append(Text.literal(delimiterString + startIndicator + (int)(oldApex-startHeight)).formatted(startColor));
				}
				if(!config.advancedStatsInNewLine){
					displayText.append(advancedStatsText);
				}
				if (oldYVelocity > 0 && yVelocity < 0){
					oldApexDiff = (int)(player.getY()- oldApex);
					oldApex = player.getY();
				}
				oldYVelocity = player.getVelocity().getY();
			}

			int optimalPitch = pitch > 0 ? config.descendAngle : config.ascendAngle;
			if (optimalIndicator && Math.abs(Math.abs(pitch) - Math.abs(optimalPitch)) <= config.indicatorWidth)
				displayText = Text.literal("[ ").append(displayText).append(" ]");

			Window mainWindow = minecraft.getWindow();
			TextRenderer textRenderer = minecraft.textRenderer;
			int xWidth = minecraft.textRenderer.getWidth(displayText);
			int yHeight = minecraft.textRenderer.fontHeight;
			int xPos = (mainWindow.getScaledWidth() - xWidth) / 2;
			int yPos = switch (screenPosition) {
				case BOTTOM_CENTER -> mainWindow.getScaledHeight() - yHeight * 10;
				case MIDDLE_CENTER -> mainWindow.getScaledHeight() / 2 - yHeight * 5;
				case TOP_CENTER -> yHeight * 5;
            };
			if (showElytraItem && elytraItem != null)
				drawContext.drawItem(elytraItem, (mainWindow.getScaledWidth() - 16) / 2, yPos - 16);
			drawContext.drawText(textRenderer, displayText, xPos, yPos, textColor, textShadow);
			if (config.advancedStatsInNewLine){
				int advXWidth = minecraft.textRenderer.getWidth(advancedStatsText);
				drawContext.drawText(textRenderer, advancedStatsText, xPos+xWidth/2-advXWidth/2, yPos+yHeight, textColor, textShadow);
			}
//			double yVelocity = player.getVelocity().getY();
//			Formatting apexColor = oldApexDiff >= 0 ? Formatting.GREEN : Formatting.RED;
//			String apexIndicator = oldApexDiff >= 0 ? "++" : "-";
//			MutableText apexText = Text.literal(String.valueOf((int) oldApex));
//			MutableText diffText = Text.literal((apexIndicator) + oldApexDiff).formatted(apexColor);
//			Formatting startColor = oldApex-startHeight >= 0 ? Formatting.GREEN : Formatting.RED;
//			String startIndicator = oldApex-startHeight >= 0 ? "++" : "-";
//			MutableText startDiffText = Text.literal((startIndicator) + (int)(oldApex-startHeight)).formatted(startColor);
//			MutableText combined = apexText.append("; ").append(diffText).append("; ").append(startDiffText);
//			drawContext.drawText(textRenderer, combined, xPos, yPos+yHeight, textColor, textShadow);
//			if (oldYVelocity > 0 && yVelocity < 0){
//				oldApexDiff = (int)(player.getY()- oldApex);
//				oldApex = player.getY();
//			}
//			oldYVelocity = player.getVelocity().getY();
		});
	}
}