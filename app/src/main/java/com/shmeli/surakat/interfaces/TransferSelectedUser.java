package com.shmeli.surakat.interfaces;

import com.shmeli.surakat.model.User;

/**
 * Created by Serghei Ostrovschi on 11/20/17.
 */

public interface TransferSelectedUser {

    void onTransferSelectedUserSuccess(int      targetFragmentCode,
                                       String   selectedUserKey,
                                       User     selectedUser);

    void onTransferSelectedUserError(String error);
}
