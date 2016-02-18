package com.jaquadro.minecraft.storagedrawers.api.config;

/**
 * The main hub for user-managed mod configuration.
 */
public interface IUserConfig
{
    /**
     * Configuration options related to third party addon packs for Storage Drawers.
     */
    IAddonConfig addonConfig ();

    /**
     * Configuration options related to individual blocks.
     */
    IBlockConfig blockConfig ();

    /**
     * Configuration options related to third party mod integration.
     */
    IIntegrationConfig integrationConfig ();
}
