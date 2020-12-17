package agency.highlysuspect.dazzle2.client.resource;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.client.resource.provider.ResourceProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DazzleResourcePack implements ResourcePack {
	public DazzleResourcePack(ResourceType type, ResourceManager mgr) {
		this.type = type;
		this.mgr = mgr;
	}
	
	private final List<ResourceProvider> providers = new ArrayList<>();
	private final ResourceManager mgr;
	private final ResourceType type;
	private boolean isInitialized = false;
	
	private void ensureInit() {
		if(isInitialized) return;
		
		if(type == ResourceType.CLIENT_RESOURCES) initAssets(mgr);
		initDatapack(mgr);
		isInitialized = true;
	}
	
	private void initAssets(ResourceManager mgr) {
		tryAddProvider(mgr, ResourceProvider.LampBlockstates::new);
		tryAddProvider(mgr, ResourceProvider.LampItemModels::new);
	}
	
	private void initDatapack(ResourceManager mgr) {
		
	}
	
	private void tryAddProvider(ResourceManager mgr, IOExceptionThrowyFunction<ResourceManager, ResourceProvider> cons) {
		try {
			providers.add(cons.apply(mgr));
		} catch (IOException e) {
			Init.LOGGER.error("Problem initializing a ResourceProvider in the dazzle injected resource pack!", e);
		}
	}
	
	private interface IOExceptionThrowyFunction<A, B> {
		B apply(A a) throws IOException;
	}
	
	///////////////////////////////////////
	
	@Override
	public InputStream openRoot(String fileName) throws IOException {
		ensureInit();
		return null; //used for pack.png, but this pack isn't even displayed in the menu so why bother
	}
	
	@Override
	public InputStream open(ResourceType type, Identifier id) throws IOException {
		ensureInit();
		if(!id.getNamespace().equals(Init.MODID)) return null;
		
		for(ResourceProvider p : providers) {
			Optional<Supplier<InputStream>> input = p.get(id);
			if(input.isPresent()) return input.get().get();
		}
		
		return null;
	}
	
	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
		ensureInit();
		//TODO make an abstraction for providing entries to pass to findResources
		// this is needed for things like recipes im pretty sure, which there is no fixed set of
		Init.log("DazzleResourcePack#findResources " + prefix);
		return Collections.emptyList();
	}
	
	@Override
	public boolean contains(ResourceType type, Identifier id) {
		ensureInit();
		if(!id.getNamespace().equals(Init.MODID)) return false;
		
		for(ResourceProvider p : providers) {
			if(p.get(id).isPresent()) return true;
		}
		
		return false;
	}
	
	@Override
	public Set<String> getNamespaces(ResourceType type) {
		return Collections.singleton(Init.MODID);
	}
	
	@Override
	public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
		ensureInit();
		return null;
	}
	
	@Override
	public String getName() {
		return "Dazzle 2 - Generated resources";
	}
	
	@Override
	public void close() {
		//Nothing to do
	}
}
