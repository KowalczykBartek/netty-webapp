package com.primary.repository;

public interface NotesRepository
{
	void upsertNote(String user, Object note);

	Object getNote(String user);
}
