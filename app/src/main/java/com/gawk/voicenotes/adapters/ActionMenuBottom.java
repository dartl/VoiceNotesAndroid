package com.gawk.voicenotes.adapters;

import java.util.ArrayList;

/**
 * Created by GAWK on 30.08.2017.
 */

public interface ActionMenuBottom {

    public void updateList();

    public void deleteItemList(long id, boolean stateRemoveAllNotification, ArrayList selectItems);

    public void shareItemList(long id, ArrayList selectItems);

    public void refreshSelectedList();
}
