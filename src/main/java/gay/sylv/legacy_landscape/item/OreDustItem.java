package gay.sylv.legacy_landscape.item;

import gay.sylv.legacy_landscape.data_attachment.LegacyAttachments;
import gay.sylv.legacy_landscape.data_attachment.LegacyChunkType;
import gay.sylv.legacy_landscape.networking.client_bound.LegacyChunkPayload;
import gay.sylv.legacy_landscape.util.Constants;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

// not an orespawn reference :)
public class OreDustItem extends TooltipItem {
	public OreDustItem(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
		BlockState state = context.getLevel().getBlockState(context.getClickedPos());
		if (state.isAir()) return InteractionResult.PASS;

		LevelChunk chunk = context.getLevel().getChunkAt(context.getClickedPos());
		Player player = Objects.requireNonNull(context.getPlayer());

		// Prevent Adventure players from interacting with chunks.
		if (!player.mayBuild() && !chunk.hasData(LegacyAttachments.ALLOW_ADVENTURE_MODE)) {
			if (!context.getLevel().isClientSide()) {
				player.sendSystemMessage(Constants.ALLOW_ADVENTURE_MODE_OFF);
			}
			return InteractionResult.PASS;
		}

		if (!chunk.hasData(LegacyAttachments.LEGACY_CHUNK)) {
			context.getLevel().playSound(context.getPlayer(), Objects.requireNonNull(context.getPlayer()), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.0F, 1.25F);

			if (!context.getLevel().isClientSide()) {
				LegacyAttachments.setChunkData(
					(ServerLevel) context.getLevel(),
					chunk,
					LegacyAttachments.LEGACY_CHUNK,
					LegacyChunkType.LEGACY,
					data -> new LegacyChunkPayload(chunk.getPos(), Optional.of(data))
				);

				context.getItemInHand().consume(1, context.getPlayer());
			}

			return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
		}

		return InteractionResult.PASS;
	}
}
