/*
 * This is the source code of Telegram for Android v. 1.7.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2014.
 */

package com.jmv.frre.moduloestudiante.ui.Adapters;

import com.jmv.frre.moduloestudiante.android.AndroidUtilities;
import com.jmv.frre.moduloestudiante.messenger.ConnectionsManager;
import com.jmv.frre.moduloestudiante.messenger.RPCRequest;
import com.jmv.frre.moduloestudiante.messenger.TLObject;
import com.jmv.frre.moduloestudiante.messenger.TLRPC;

import java.util.ArrayList;

public class BaseContactsSearchAdapter extends BaseFragmentAdapter {

    protected ArrayList<TLRPC.User> globalSearch = new ArrayList<TLRPC.User>();
    private long reqId = 0;
    private int lastReqId;
    protected String lastFoundUsername = null;

    public void queryServerSearch(final String query) {
        if (query == null || query.length() < 5) {
            if (reqId != 0) {
                ConnectionsManager.getInstance().cancelRpc(reqId, true);
                reqId = 0;
            }
            globalSearch.clear();
            lastReqId = 0;
            notifyDataSetChanged();
            return;
        }
        TLRPC.TL_contacts_search req = new TLRPC.TL_contacts_search();
        req.q = query;
        req.limit = 50;
        final int currentReqId = ++lastReqId;
        reqId = ConnectionsManager.getInstance().performRpc(req, new RPCRequest.RPCRequestDelegate() {
            @Override
            public void run(final TLObject response, final TLRPC.TL_error error) {
                AndroidUtilities.RunOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (currentReqId == lastReqId) {
                            if (error == null) {
                                TLRPC.TL_contacts_found res = (TLRPC.TL_contacts_found) response;
                                globalSearch = res.users;
                                lastFoundUsername = query;
                                notifyDataSetChanged();
                            }
                        }
                        reqId = 0;
                    }
                });
            }
        }, true, RPCRequest.RPCRequestClassGeneric | RPCRequest.RPCRequestClassFailOnServerErrors);
    }
}
