package net.timelesswastaken.justputtyexplosive.event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.timelesswastaken.justputtyexplosive.JustPuttyExplosiveMod;
import net.timelesswastaken.justputtyexplosive.init.JustPuttyExplosiveModSounds;
import net.timelesswastaken.justputtyexplosive.item.DetonatorItem;

@Mod.EventBusSubscriber(modid = JustPuttyExplosiveMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DetonatorEquipHandler {
	private static final Map<UUID, Boolean> WAS_HOLDING_DETONATOR = new HashMap<>();

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player) || player.level().isClientSide) {
			return;
		}

		boolean holding = isDetonator(player.getMainHandItem()) || isDetonator(player.getOffhandItem());
		boolean wasHolding = WAS_HOLDING_DETONATOR.getOrDefault(player.getUUID(), false);
		if (holding && !wasHolding) {
			player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
					JustPuttyExplosiveModSounds.DETONATOR_EQUIP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		}
		WAS_HOLDING_DETONATOR.put(player.getUUID(), holding);
	}

	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
		WAS_HOLDING_DETONATOR.remove(event.getEntity().getUUID());
	}

	private static boolean isDetonator(ItemStack stack) {
		return stack.getItem() instanceof DetonatorItem;
	}
}
