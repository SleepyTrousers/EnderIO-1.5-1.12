package crazypants.enderio.render;

import net.minecraft.client.resources.model.IBakedModel;

/**
 * A render cache can store one baked model. It is aware on fact that will
 * invalidate the cache and will do so.
 *
 */
public interface IRenderCache {

  void cacheModel(IBakedModel model);

  IBakedModel getCachedModel();

}
