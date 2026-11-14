package net.timelesswastaken.justputtyexplosive.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.timelesswastaken.justputtyexplosive.JustPuttyExplosiveMod;
import net.timelesswastaken.justputtyexplosive.util.ExplosivePuttyLinkManager;

@Mod.EventBusSubscriber(modid = JustPuttyExplosiveMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExplosivePuttyExplosionHandler {
	@SubscribeEvent
	public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
		Level level = event.getLevel();
		if (level.isClientSide) {
			return;
		}

		for (BlockPos pos : event.getAffectedBlocks()) {
			BlockPos explosionPos = pos.immutable();
			ExplosivePuttyLinkManager.PuttyExplosion explosion = ExplosivePuttyLinkManager.consumePutty(level, explosionPos);
			if (explosion.type() != ExplosivePuttyLinkManager.PuttyType.NONE) {
				boolean fire = explosion.type() == ExplosivePuttyLinkManager.PuttyType.INCENDIARY && !explosion.underwater();
				float power = explosion.type() == ExplosivePuttyLinkManager.PuttyType.HIGH
						? ExplosivePuttyLinkManager.HIGH_EXPLOSION_POWER
						: ExplosivePuttyLinkManager.EXPLOSION_POWER;
				JustPuttyExplosiveMod.queueServerWork(1, () -> ExplosivePuttyLinkManager.explodeAt(level, explosionPos, null, fire, power));
			}
		}
	}
}
