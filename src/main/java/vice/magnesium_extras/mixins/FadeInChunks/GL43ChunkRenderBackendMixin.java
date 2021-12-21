package vice.magnesium_extras.mixins.FadeInChunks;

import me.jellysquid.mods.sodium.client.gl.device.CommandList;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkCameraContext;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkGraphicsState;
import me.jellysquid.mods.sodium.client.render.chunk.backends.multidraw.MultidrawChunkRenderBackend;
import me.jellysquid.mods.sodium.client.render.chunk.backends.multidraw.ChunkDrawParamsVector;
import me.jellysquid.mods.sodium.client.render.chunk.backends.multidraw.MultidrawGraphicsState;
import me.jellysquid.mods.sodium.client.render.chunk.lists.ChunkRenderListIterator;
import me.jellysquid.mods.sodium.client.render.chunk.passes.BlockRenderPass;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static vice.magnesium_extras.features.FadeInChunks.ChunkDrawParamsVectorExt.ext;
import static vice.magnesium_extras.features.FadeInChunks.ChunkGraphicsStateExt.ext;

@Mixin(value = MultidrawChunkRenderBackend.class, remap = false)
public abstract class GL43ChunkRenderBackendMixin extends ChunkRenderShaderBackendMixin<MultidrawGraphicsState> {
    @Shadow @Final private ChunkDrawParamsVector uniformBufferBuilder;

    @Inject(method = "setupDrawBatches",
            at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/backends/multidraw/ChunkDrawParamsVector;pushChunkDrawParams(FFF)V", shift = At.Shift.AFTER))
    private void pushChunkDrawParamFadeInProgress(CommandList i, ChunkRenderListIterator<MultidrawGraphicsState> it, ChunkCameraContext visible, CallbackInfo ci) {
        float progress = ext(it.getGraphicsState()).getFadeInProgress(this.currentTime);
        ext(this.uniformBufferBuilder).pushChunkDrawParamFadeIn(progress);
    }

    @ModifyArg(method = "upload", at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/ChunkRenderContainer;setGraphicsState(Lme/jellysquid/mods/sodium/client/render/chunk/passes/BlockRenderPass;Lme/jellysquid/mods/sodium/client/render/chunk/ChunkGraphicsState;)V", ordinal = 0))
    private ChunkGraphicsState setLoadTime(BlockRenderPass pass, ChunkGraphicsState newState) {
        ChunkGraphicsState oldState = ext(newState).getContainer().getGraphicsState(pass);
        ext(newState).setLoadTime(oldState == null ? this.currentTime : ext(oldState).getLoadTime());
        return newState;
    }
}
