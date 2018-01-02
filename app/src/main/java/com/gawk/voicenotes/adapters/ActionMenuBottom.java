package com.gawk.voicenotes.adapters;

import java.util.ArrayList;

/**
 * Created by GAWK on 30.08.2017.
 */

public interface ActionMenuBottom {

    void updateList();

    void deleteItemList(long id, boolean stateRemoveAllNotification, ArrayList selectItems);

    void shareItemList(long id, ArrayList selectItems);

    void editedItemList(long id);

    void refreshSelectedList(int position);

    void filterNotes(long category_id);
}
