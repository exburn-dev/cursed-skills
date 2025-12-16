package com.jujutsu.systems.entitydata;

public interface EntityComponent extends EntityServerData {
    default void onLoaded() {}
}
