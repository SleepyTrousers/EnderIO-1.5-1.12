package crazypants.enderio.config;

import static crazypants.enderio.config.Config.config;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config.Section;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;

@SuppressWarnings({"rawtypes"})
public class GuiConfigFactoryEIO extends GuiConfig {

    public GuiConfigFactoryEIO(GuiScreen parentScreen) {
        super(
                parentScreen,
                getConfigElements(parentScreen),
                EnderIO.MODID,
                false,
                false,
                EnderIO.lang.localize("config.title"));
    }

    private static List<IConfigElement> getConfigElements(GuiScreen parent) {
        List<IConfigElement> list = new ArrayList<IConfigElement>();
        String prefix = EnderIO.lang.addPrefix("config.");

        for (Section section : Config.sections) {
            list.add(new ConfigElement<ConfigCategory>(
                    config.getCategory(section.lc()).setLanguageKey(prefix + section.lang)));
        }

        return list;
    }
}
