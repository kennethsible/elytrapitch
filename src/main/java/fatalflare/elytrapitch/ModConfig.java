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

@Config(name = "elytrapitch")
class ModConfig implements ConfigData {
    @ConfigEntry.Gui.EnumHandler(option=EnumDisplayOption.BUTTON)
    ScreenPosition screenPosition = ScreenPosition.BOTTOM_CENTER;

    @ConfigEntry.BoundedDiscrete(min=0, max=45)
    int indicatorWidth = 5;
}
