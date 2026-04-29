package neoterra.world.worldgen.biome;

import com.google.common.base.Supplier;

import neoterra.NTCommon;

public final class InvalidatableSupplier<T> implements Supplier<T> {

	private final Supplier<T> delegate;
	private volatile boolean cached;
	private volatile T value;

	public InvalidatableSupplier(Supplier<T> delegate) {
		NTCommon.debug("InvalidatableSupplier: created wrapping {}", delegate.getClass().getName());
		this.delegate = delegate;
	}

	@Override
	public T get() {
		if (!this.cached) {
			synchronized (this) {
				if (!this.cached) {
					NTCommon.debug("InvalidatableSupplier: materializing delegate {}", this.delegate.getClass().getName());
					this.value = this.delegate.get();
					this.cached = true;
				}
			}
		}
		return this.value;
	}

	public void invalidate() {
		synchronized (this) {
			NTCommon.debug("InvalidatableSupplier: invalidating (was cached={})", this.cached);
			this.cached = false;
			this.value = null;
		}
	}
}
