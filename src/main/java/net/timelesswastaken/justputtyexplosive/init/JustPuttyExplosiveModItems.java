/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.timelesswastaken.justputtyexplosive.init;

import net.timelesswastaken.justputtyexplosive.item.DetonatorItem;
import net.timelesswastaken.justputtyexplosive.item.BlastingCapItem;
import net.timelesswastaken.justputtyexplosive.JustPuttyExplosiveMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

public class JustPuttyExplosiveModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, JustPuttyExplosiveMod.MODID);
	public static final RegistryObject<Item> EXPLOSIVE_PUTTY = block(JustPuttyExplosiveModBlocks.EXPLOSIVE_PUTTY);
	public static final RegistryObject<Item> BLASTING_CAP = REGISTRY.register("blasting_cap", () -> new BlastingCapItem());
	public static final RegistryObject<Item> DETONATOR = REGISTRY.register("detonator", () -> new DetonatorItem());
	public static final RegistryObject<Item> EXPLOSIVE_PUTTY_RIGGED = block(JustPuttyExplosiveModBlocks.EXPLOSIVE_PUTTY_RIGGED);
	public static final RegistryObject<Item> INCENDIARY_EXPLOSIVE_PUTTY = block(JustPuttyExplosiveModBlocks.INCENDIARY_EXPLOSIVE_PUTTY);
	public static final RegistryObject<Item> INCENDIARY_EXPLOSIVE_PUTTY_RIGGED = block(JustPuttyExplosiveModBlocks.INCENDIARY_EXPLOSIVE_PUTTY_RIGGED);
	public static final RegistryObject<Item> HIGH_EXPLOSIVE_PUTTY = block(JustPuttyExplosiveModBlocks.HIGH_EXPLOSIVE_PUTTY);
	public static final RegistryObject<Item> HIGH_EXPLOSIVE_PUTTY_RIGGED = block(JustPuttyExplosiveModBlocks.HIGH_EXPLOSIVE_PUTTY_RIGGED);

	// Start of user code block custom items
	// End of user code block custom items
	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
}