package crazypants.enderio.integration.ftbl;

import crazypants.enderio.integration.ftbl.cmd.TestCommand;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = EnderIOIntegrationFtbl.MODID, name = EnderIOIntegrationFtbl.MODNAME, version = EnderIOIntegrationFtbl.VERSION)
public class EnderIOIntegrationFtbl {

	public static final String MODID = "enderio-integration-ftbl";
	public static final String MODNAME = "Tutorial Mod";
	public static final String VERSION = "1.0.0";

	@Instance
	public static EnderIOIntegrationFtbl instance = new EnderIOIntegrationFtbl();

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {

	}

	@EventHandler
	public void init(FMLInitializationEvent e) {

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {

	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		// register server commands

		event.registerServerCommand(new TestCommand());
	}
}