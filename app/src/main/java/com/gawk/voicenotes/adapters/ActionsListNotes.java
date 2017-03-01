package com.gawk.voicenotes.adapters;

/**
 * Этот интерфейс необходим для того, чтобы в адаптерах {@link NoteCursorAdapter} и {@link NotificationCursorAdapter}
 * чтобы была возможность вызвать методы фрагментов
 * @author GAWK on 24.02.2017.
 */

public interface ActionsListNotes {
    /**
     * Вызывает показ диалога, которые спрашивает "Действительно ли они хотят удалить?"
     * @param id идентификатор заметки, которую удаляем
     * @param state определяет удаляем выбранные заметки или только одну
     */
    public void showDialogDelete(final long id, final int state);
    /**
     * Вызыывает метод, отмечающий выделение заметки
     * @param id идентификатор заметки, которую выделяем
     * @param checked состояние выделения
     */
    public void selectNote(long id, boolean checked);
}
