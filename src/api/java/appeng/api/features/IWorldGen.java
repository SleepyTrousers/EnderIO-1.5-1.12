package appeng.api.features;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

public interface IWorldGen
{

	public enum WorldGenType
	{
		CertusQuartz, ChargedCertusQuartz, Metorites
	};

	public void disableWorldGenForProviderID(WorldGenType type, Class<? extends WorldProvider> provider);

	public void disableWorldGenForDimension(WorldGenType type, int dimid);

	boolean isWorldGenEnabled(WorldGenType type, World w);

}
