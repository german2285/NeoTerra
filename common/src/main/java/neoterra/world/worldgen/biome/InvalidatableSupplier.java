package neoterra.world.worldgen.biome;

import com.google.common.base.Supplier;

public final class InvalidatableSupplier<T> implements Supplier<T> {

	private final Supplier<T> delegate;
	private volatile boolean cached;
	private volatile T value;

	public InvalidatableSupplier(Supplier<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	public T get() {
		if (!this.cached) {
			synchronized (this) {
				if (!this.cached) {
					this.value = this.delegate.get();
					this.cached = true;
				}
			}
		}
		return this.value;
	}

	public void invalidate() {
		synchronized (this) {
			this.cached = false;
			this.value = null;
		}
	}
}
