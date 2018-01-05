package crazypants.enderio.base;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;

@Mod(modid = "enderio", name = "EnderIO", version = EnderIO.VERSION, guiFactory = "crazypants.enderio.base.config.ConfigFactoryEIO")
public class EnderIODummy {

  @Instance("enderio")
  public static EnderIODummy instance;

  public static @Nonnull EnderIODummy getInstance() {
    return NullHelper.notnullF(instance, "instance is missing");
  }

  @EventHandler
  public void onImc(@Nonnull IMCEvent evt) {
    EnderIO.instance.processImc(evt.getMessages());
  }

}
