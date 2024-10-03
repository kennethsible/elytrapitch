package fatalflare.elytrapitch;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import static me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption;

enum ScreenPosition {
    BOTTOM_CENTER,
    MIDDLE_CENTER,
    TOP_CENTER
}

@Config(name = ElytraPitch.MOD_ID)
class ModConfig implements ConfigData {
    // First Person Settings
    @ConfigEntry.Category("FP")
    @ConfigEntry.Gui.EnumHandler(option=EnumDisplayOption.BUTTON)
    ScreenPosition screenPositionFP = ScreenPosition.BOTTOM_CENTER;
    @ConfigEntry.Category("FP")
    boolean optimalIndicatorFP = true;
    @ConfigEntry.Category("FP")
    @ConfigEntry.ColorPicker()
    int textColorFP = 0xffffff;
    @ConfigEntry.Category("FP")
    boolean textShadowFP = true;
    @ConfigEntry.Category("FP")
    String hudDelimiterFP = "";
    @ConfigEntry.Category("FP")
    boolean showYawFP = false;
    @ConfigEntry.Category("FP")
    boolean showAltitudeFP = false;
    @ConfigEntry.Category("FP")
    boolean showVelocityFP = false;
    @ConfigEntry.Category("FP")
    boolean showDirectionFP = false;
    @ConfigEntry.Category("FP")
    boolean showDurabilityFP = false;
    @ConfigEntry.Category("FP")
    boolean showElytraItemFP = false;

    // Third Person Settings
    @ConfigEntry.Category("TP")
    @ConfigEntry.Gui.EnumHandler(option=EnumDisplayOption.BUTTON)
    ScreenPosition screenPositionTP = ScreenPosition.MIDDLE_CENTER;
    @ConfigEntry.Category("TP")
    boolean optimalIndicatorTP = true;
    @ConfigEntry.Category("TP")
    @ConfigEntry.ColorPicker()
    int textColorTP = 0xffffff;
    @ConfigEntry.Category("TP")
    boolean textShadowTP = true;
    @ConfigEntry.Category("TP")
    String hudDelimiterTP = "";
    @ConfigEntry.Category("TP")
    boolean showYawTP = false;
    @ConfigEntry.Category("TP")
    boolean showAltitudeTP = false;
    @ConfigEntry.Category("TP")
    boolean showVelocityTP = false;
    @ConfigEntry.Category("TP")
    boolean showDirectionTP = false;
    @ConfigEntry.Category("TP")
    boolean showDurabilityTP = false;
    @ConfigEntry.Category("TP")
    boolean showElytraItemTP = false;

    // Flight Settings
    @ConfigEntry.Category("flightSettings")
    @ConfigEntry.Gui.PrefixText
    int ascendAngle = -40;
    @ConfigEntry.Category("flightSettings")
    int descendAngle = 40;
    @ConfigEntry.Category("flightSettings")
    int glideAngle = 0;
    @ConfigEntry.Category("flightSettings")
    @ConfigEntry.BoundedDiscrete(min=0, max=45)
    int indicatorWidth = 5;
    @ConfigEntry.Category("flightSettings")
    @ConfigEntry.Gui.PrefixText
    double durabilityThreshold = 0.05;
    @ConfigEntry.Category("flightSettings")
    boolean durabilityPercentage = true;
    @ConfigEntry.Category("flightSettings")
    @ConfigEntry.ColorPicker()
    int messageColor = 0xbe0000;
    @ConfigEntry.Category("flightSettings")
    boolean boldMessage = true;
}
