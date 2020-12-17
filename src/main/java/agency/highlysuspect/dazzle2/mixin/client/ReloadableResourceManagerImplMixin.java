package agency.highlysuspect.dazzle2.mixin.client;

import agency.highlysuspect.dazzle2.client.resource.DazzleResourcePack;
import net.minecraft.resource.*;
import net.minecraft.util.Unit;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(value = ReloadableResourceManagerImpl.class, priority = 990)
public abstract class ReloadableResourceManagerImplMixin {
	@Shadow @Final private static Logger LOGGER;
	@Shadow @Final private ResourceType type;
	@Shadow public native void addPack(ResourcePack resourcePack);
	
	@Inject(method = "beginMonitoredReload",
		at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
	private void injectPack(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs, CallbackInfoReturnable<ResourceReloadMonitor> cir) {
		LOGGER.info("Dazzle 2 is injecting a resource pack \uD83E\uDD2B");
		addPack(new DazzleResourcePack(type, (ResourceManager) this));
	}
}
