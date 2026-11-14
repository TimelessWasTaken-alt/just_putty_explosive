/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.timelesswastaken.justputtyexplosive.init;

import net.timelesswastaken.justputtyexplosive.block.*;
import net.timelesswastaken.justputtyexplosive.JustPuttyExplosiveMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

public class JustPuttyExplosiveModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, JustPuttyExplosiveMod.MODID);
	public static final RegistryObject<Block> EXPLOSIVE_PUTTY;
	public static final RegistryObject<Block> EXPLOSIVE_PUTTY_RIGGED;
	public static final RegistryObject<Block> INCENDIARY_EXPLOSIVE_PUTTY;
	public static final RegistryObject<Block> INCENDIARY_EXPLOSIVE_PUTTY_RIGGED;
	public static final RegistryObject<Block> HIGH_EXPLOSIVE_PUTTY;
	public static final RegistryObject<Block> HIGH_EXPLOSIVE_PUTTY_RIGGED;
	static {
		EXPLOSIVE_PUTTY = REGISTRY.register("explosive_putty", ExplosivePuttyBlock::new);
		EXPLOSIVE_PUTTY_RIGGED = REGISTRY.register("explosive_putty_rigged", ExplosivePuttyRiggedBlock::new);
		INCENDIARY_EXPLOSIVE_PUTTY = REGISTRY.register("incendiary_explosive_putty", IncendiaryExplosivePuttyBlock::new);
		INCENDIARY_EXPLOSIVE_PUTTY_RIGGED = REGISTRY.register("incendiary_explosive_putty_rigged", IncendiaryExplosivePuttyRiggedBlock::new);
		HIGH_EXPLOSIVE_PUTTY = REGISTRY.register("high_explosive_putty", HighExplosivePuttyBlock::new);
		HIGH_EXPLOSIVE_PUTTY_RIGGED = REGISTRY.register("high_explosive_putty_rigged", HighExplosivePuttyRiggedBlock::new);
	}
	// Start of user code block custom blocks
	// End of user code block custom blocks
}