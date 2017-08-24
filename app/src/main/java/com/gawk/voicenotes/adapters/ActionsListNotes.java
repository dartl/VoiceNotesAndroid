package com.gawk.voicenotes.adapters;

import com.gawk.voicenotes.lists_adapters.NoteRecyclerAdapter;
import com.gawk.voicenotes.lists_adapters.NotificationRecyclerAdapter;

/**
 * Этот интерфейс необходим для того, чтобы в адаптерах {@link NoteRecyclerAdapter} и {@link NotificationRecyclerAdapter}
 * чтобы была возможность вызвать методы фрагментов
 * @author GAWK on 24.02.2017.
 */

public interface ActionsListNotes {
    /**
     * Вызыывает метод, отмечающий выделение заметки
     * @param id идентификатор заметки, которую выделяем
     */
    public boolean selectNote(long id);
    public boolean checkSelectNote(long id);
    public void selectNotification(long id);
}
