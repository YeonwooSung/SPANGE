package com.technonia.spange.manager;

import com.technonia.spange.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SessionManager {
    private String adminUserName;

    public SessionManager(String adminUserName) {
        this.adminUserName = adminUserName;
    }

    public void parseResponse(String res, ArrayList<UserAcceptance> userList) {
        JSONArray jsonArray = Utils.parseResponseArray(res);
        int len = jsonArray.length();
        int lastAccepted = 0;
        final int MAX_USER_NUM = 5;

        if (!userList.isEmpty()) return;

        for (int i = 0; i < len; i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String username = jsonObject.getString("username");
                int acceptance_num = jsonObject.getInt("acceptance");
                boolean acceptance = (acceptance_num != 0);

                UserAcceptance userAcceptance = new UserAcceptance(username, acceptance);

                // check if current user is an accepted user
                if (acceptance) {
                    // check last accepted user's index to sort the list of accepted users
                    if (lastAccepted != i) {
                        userList.add(lastAccepted, userAcceptance);
                    } else {
                        userList.add(userAcceptance);
                    }
                    lastAccepted += 1;
                } else {
                    userList.add(userAcceptance);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                userList.add(null);
            }
        }

        int remaining = MAX_USER_NUM - len;
        for (int i = 0; i < remaining; i++) {
            userList.add(null);
        }

        // put admin user at the head of the list
        for (UserAcceptance userAcceptance : userList) {
            if (userAcceptance.getUserID().equals(adminUserName)) {
                UserAcceptance user = userAcceptance;
                userList.remove(user);
                userList.add(0, user);
                break;
            }
        }
    }
}
