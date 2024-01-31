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
    boolean showIndicatorFP = true;
    @ConfigEntry.Category("FP")
    @ConfigEntry.BoundedDiscrete(min=0, max=45)
    int indicatorWidthFP = 5;
    @ConfigEntry.Category("FP")
    @ConfigEntry.ColorPicker()
    int textColorFP = 0xffffff;
    @ConfigEntry.Category("FP")
    boolean textShadowFP = true;

    // Third Person Settings
    @ConfigEntry.Category("TP")
    @ConfigEntry.Gui.EnumHandler(option=EnumDisplayOption.BUTTON)
    ScreenPosition screenPositionTP = ScreenPosition.MIDDLE_CENTER;
    @ConfigEntry.Category("TP")
    boolean showIndicatorTP = true;
    @ConfigEntry.Category("TP")
    @ConfigEntry.BoundedDiscrete(min=0, max=45)
    int indicatorWidthTP = 5;
    @ConfigEntry.Category("TP")
    @ConfigEntry.ColorPicker()
    int textColorTP = 0xffffff;
    @ConfigEntry.Category("TP")
    boolean textShadowTP = true;
}
