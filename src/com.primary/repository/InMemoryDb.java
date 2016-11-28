package com.primary.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDb implements NotesRepository
{
	public static final InMemoryDb instance = new InMemoryDb();

	private final Map<String, Object> volatileDb = new ConcurrentHashMap<>();

	@Override
	public void upsertNote(final String user, final Object note)
	{
		volatileDb.put(user, note);
	}
}
