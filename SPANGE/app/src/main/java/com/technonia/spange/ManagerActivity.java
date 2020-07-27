package com.technonia.spange;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

public class ManagerActivity extends AppCompatActivity {
    private final int MAX_USER_NUM = 5;

    private ArrayList<UserAcceptance> userList = new ArrayList<UserAcceptance>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        initScreen();
    }

    private void initScreen() {
        initUserList();
        updateUserCards();
        addEventListenerToBackButton();
    }

    private void initUserList() {
        String[] userNames = {"사용자1", "사용자2", "사용자3", "사용자4", null};
        boolean[] acceptances = {true, true, false, true, false};
        int lastAcceptedUserNum = 0;

        ArrayList<UserAcceptance> tempList = new ArrayList<>();

        for (int i = 0; i < MAX_USER_NUM; i++) {
            String userName = userNames[i];
            boolean acceptance = acceptances[i];

            // check if there are no more users
            if (userName == null) {
                tempList.add(null);
                continue;
            }

            UserAcceptance user = new UserAcceptance(userName, acceptance);

            // check if the current user is accepted
            if (acceptance) {
                // Use conditional statement to sort the user acceptance list
                // Basically, the accepted users must appear before than the non-accepted users.
                // Thus, by checking the index of latest accepted user, this method will sort the user acceptance list.
                if (lastAcceptedUserNum != i) {
                    userList.add(lastAcceptedUserNum, user);
                } else {
                    userList.add(user);
                }

                lastAcceptedUserNum += 1;
            } else {
                userList.add(user);
            }
        }

        // add users in the tempList to the userList
        for (UserAcceptance user : tempList) {
            userList.add(user);
        }
    }

    private void updateUserCards() {
        for (int i = 0; i < userList.size(); i++) {
            UserAcceptance user = userList.get(i);

            // user == null when there is no more registered user
            if (user == null) {
                updateBackgroundImageOfLinearLayout_empty(i);
                continue;
            }

            if (!user.isAccepted()) {
                updateBackgroundImageOfLinearLayout_notAccepted(i);
            }

            updateUserName(i, user.getUserName());
        }
    }

    private void terminate() {
        finish();
    }

    private void addEventListenerToBackButton() {
        ImageButton backButton = findViewById(R.id.manager_activity_go_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                terminate();
            }
        });
    }

    private void updateBackgroundImageOfLinearLayout_empty(int num) {
        LinearLayout layout;
        Drawable bg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.blank_empty);

        switch (num) {
            case 0:
                layout = findViewById(R.id.manager_activity_grid_layout_row0);
                break;
            case 1:
                layout = findViewById(R.id.manager_activity_grid_layout_row1);
                break;
            case 2:
                layout = findViewById(R.id.manager_activity_grid_layout_row2);
                break;
            case 3:
                layout = findViewById(R.id.manager_activity_grid_layout_row3);
                break;
            case 4:
                layout = findViewById(R.id.manager_activity_grid_layout_row4);
                break;
            default:
                Log.e("InvalidArgument", "Invalid argument: " + num);
                return;
        }

        layout.setBackground(bg);
        layout.removeAllViews();
    }

    private void updateBackgroundImageOfLinearLayout_notAccepted(int num) {
        LinearLayout layout;
        ImageButton button;

        switch (num) {
            case 0:
                layout = findViewById(R.id.manager_activity_grid_layout_row0);
                button = findViewById(R.id.manager_activity_img_button_row0);
                break;
            case 1:
                layout = findViewById(R.id.manager_activity_grid_layout_row1);
                button = findViewById(R.id.manager_activity_img_button_row1);
                break;
            case 2:
                layout = findViewById(R.id.manager_activity_grid_layout_row2);
                button = findViewById(R.id.manager_activity_img_button_row2);
                break;
            case 3:
                layout = findViewById(R.id.manager_activity_grid_layout_row3);
                button = findViewById(R.id.manager_activity_img_button_row3);
                break;
            case 4:
                layout = findViewById(R.id.manager_activity_grid_layout_row4);
                button = findViewById(R.id.manager_activity_img_button_row4);
                break;
            default:
                Log.e("InvalidArgument", "Invalid argument: " + num);
                return;
        }

        Drawable bg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.blank_fill_unaccepted);
        Drawable button_bg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_accept);

        layout.setBackground(bg);
        button.setImageResource(R.drawable.button_accept);
    }

    private void updateUserName(int num, String userName) {
        TextView textView;

        switch (num) {
            case 0:
                textView = findViewById(R.id.manager_activity_grid_layout_row0_text);
                break;
            case 1:
                textView = findViewById(R.id.manager_activity_grid_layout_row1_text);
                break;
            case 2:
                textView = findViewById(R.id.manager_activity_grid_layout_row2_text);
                break;
            case 3:
                textView = findViewById(R.id.manager_activity_grid_layout_row3_text);
                break;
            case 4:
                textView = findViewById(R.id.manager_activity_grid_layout_row4_text);
                break;
            default:
                Log.e("InvalidArgument", "Invalid argument: " + num);
                return;
        }

        textView.setText(userName);
    }
}
