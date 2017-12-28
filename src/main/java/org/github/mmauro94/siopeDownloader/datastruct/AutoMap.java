package org.github.mmauro94.siopeDownloader.datastruct;

import org.github.mmauro94.siopeDownloader.utils.CSVRecordParser;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public abstract class AutoMap<K, V> implements Collection<V> {

	public interface AutoMapCreator<T extends AutoMap<?, ?>> {
		T create();
	}

	@NotNull
	private final HashMap<K, V> map = new HashMap<>();

	public AutoMap() {
	}

	@NotNull
	protected abstract K getKey(@NotNull V value);

	@NotNull
	public V get(@NotNull K key) {
		if (!map.containsKey(key)) {
			throw new IllegalStateException("Value with key " + key + " not found");
		}
		return map.get(key);
	}

	public void put(@NotNull V value) {
		map.put(getKey(value), value);
	}

	public boolean hasKey(@NotNull K key) {
		return map.containsKey(key);
	}

	public boolean hasValue(@NotNull V key) {
		return map.containsValue(key);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return map.containsValue(o);
	}

	@NotNull
	@Override
	public Iterator<V> iterator() {
		return map.values().iterator();
	}

	@NotNull
	@Override
	public Object[] toArray() {
		return map.values().toArray();
	}

	@NotNull
	@Override
	public <T> T[] toArray(@NotNull T[] a) {
		return map.values().toArray(a);
	}

	@Override
	public boolean add(V v) {
		if (hasValue(v)) {
			put(v);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean remove(Object o) {
		return map.values().remove(o);
	}

	@Override
	public boolean containsAll(@NotNull Collection<?> c) {
		return map.values().containsAll(c);
	}

	@Override
	public boolean addAll(@NotNull Collection<? extends V> c) {
		boolean changed = false;
		for (V v : c) {
			changed |= add(v);
		}
		return changed;
	}

	@Override
	public boolean removeAll(@NotNull Collection<?> c) {
		return map.values().removeAll(c);
	}

	@Override
	public boolean retainAll(@NotNull Collection<?> c) {
		return map.values().retainAll(c);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public int size() {
		return map.size();
	}

	public static <K, V, T extends AutoMap<K, V>> T parse(@NotNull List<CSVRecord> records, @NotNull CSVRecordParser<V> recordParser, @NotNull AutoMapCreator<T> mapCreator) {
		T map = mapCreator.create();
		for (CSVRecord record : records) {
			map.put(recordParser.parse(record));
		}
		return map;
	}

	public static <K, V, T extends AutoMap<K, V>> T parse(@NotNull CSVParser parser, @NotNull CSVRecordParser<V> recordParser, @NotNull AutoMapCreator<T> mapCreator) throws IOException {
		return parse(parser.getRecords(), recordParser, mapCreator);
	}
}
