package net.timelesswastaken.justputtyexplosive.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.timelesswastaken.justputtyexplosive.JustPuttyExplosiveMod;
import net.timelesswastaken.justputtyexplosive.blockentity.ExplosivePuttyBlockEntity;

public class JustPuttyExplosiveModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, JustPuttyExplosiveMod.MODID);
	public static final RegistryObject<BlockEntityType<ExplosivePuttyBlockEntity>> EXPLOSIVE_PUTTY_RIGGED = REGISTRY.register("explosive_putty_rigged",
			() -> BlockEntityType.Builder.of(ExplosivePuttyBlockEntity::new, JustPuttyExplosiveModBlocks.EXPLOSIVE_PUTTY_RIGGED.get(), JustPuttyExplosiveModBlocks.INCENDIARY_EXPLOSIVE_PUTTY_RIGGED.get(), JustPuttyExplosiveModBlocks.HIGH_EXPLOSIVE_PUTTY_RIGGED.get()).build(null));
}
