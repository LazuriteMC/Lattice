package dev.lazurite.lattice.api.entity;

/**
 * When implemented by an {@link net.minecraft.entity.Entity},
 * allows it to determine whether or not to render itself and the
 * {@link net.minecraft.client.network.ClientPlayerEntity}
 * when set as the {@link net.minecraft.client.MinecraftClient#cameraEntity}.
 */
public interface Viewable {

    /**
     * Allows the {@link net.minecraft.entity.Entity}
     * to control whether to render itself when set as the
     * {@link net.minecraft.client.MinecraftClient#cameraEntity}.
     * @return {@code true} if it should be rendered in first person.
     */
    boolean shouldRenderSelf();

    /**
     * Allows the {@link net.minecraft.entity.Entity}
     * to control whether to render the {@link net.minecraft.client.network.ClientPlayerEntity}
     * when set as the {@link net.minecraft.client.MinecraftClient#cameraEntity}.
     * @return {@code true} if the {@link net.minecraft.client.network.ClientPlayerEntity}
     * should be rendered in first person.
     */
    boolean shouldRenderPlayer();
}
