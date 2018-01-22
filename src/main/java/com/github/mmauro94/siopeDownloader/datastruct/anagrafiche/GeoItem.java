package com.github.mmauro94.siopeDownloader.datastruct.anagrafiche;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface GeoItem {

	@Nullable
	GeoItem getParent();

	@Nullable
	Collection<@NotNull ? extends GeoItem> getChildren();

	@NotNull
	String getNome();
}
