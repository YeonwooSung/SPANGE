package com.technonia.spange;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ManagerActivity extends AppCompatActivity {

    private ArrayList<UserAcceptance> userList = new ArrayList<>();

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
        //TODO need to test with not-hardcoded data!
        String[] userNames = {"사용자1", "사용자2", "사용자3", "사용자4", null};
        boolean[] acceptances = {true, true, false, true, false};
        int lastAcceptedUserNum = 0;

        ArrayList<UserAcceptance> tempList = new ArrayList<>();
        final int MAX_USER_NUM = 5;

        for (int i = 0; i < MAX_USER_NUM; i++) {
            String userName = userNames[i];
            String userID = userNames[i];
            boolean acceptance = acceptances[i];

            // check if there are no more users
            if (userName == null) {
                tempList.add(null);
                continue;
            }

            UserAcceptance user = new UserAcceptance(userName, userID, acceptance);

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
        userList.addAll(tempList);
    }

    private void updateUserCards() {
        for (int i = 0; i < userList.size(); i++) {
            UserAcceptance user = userList.get(i);

            // user == null when there is no more registered user
            if (user == null) {
                updateBackgroundImageOfLinearLayout_empty(i);
                continue;
            }

            String userName = user.getUserName();
            updateUserName(i, userName);

            if (!user.isAccepted()) {
                updateBackgroundImageOfLinearLayout_notAccepted(i);
            }
        }
    }

    private void terminate() {
        finish();
    }

    private void updateUserAcceptance_removeUser(int index) {
        userList.remove(index);
        userList.add(null);
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

        layout.setBackground(bg);
        button.setImageResource(R.drawable.button_accept);

        addOnClickListenerToAcceptButton(num, button);
    }

    private void addOnClickListenerToAcceptButton(int num, ImageButton button) {
        final ImageButton targetButton = button;
        final int number = num;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do nothing if the user is accepted
                if (userList.get(number).isAccepted()) return;

                acceptUser(number);
                targetButton.setImageResource(R.drawable.button_accepted);
            }
        });
    }

    private void acceptUser(int num) {
        // make the user accepted
        UserAcceptance user = userList.get(num);
        user.setAcceptance(true);

        String userID = user.getUserID();
        String deviceID = Utils.getDeviceID();
        String baseURL = getString(R.string.baseURL);

        // send request via network to change the user status as accepted
        NetworkUtils.sendRequestToAcceptUser(baseURL, userID, deviceID);
    }

    private void updateUserName(int num, String userName) {
        TextView textView;
        ImageButton button;

        switch (num) {
            case 0:
                textView = findViewById(R.id.manager_activity_grid_layout_row0_text);
                button = findViewById(R.id.manager_activity_exit_button_row0);
                break;
            case 1:
                textView = findViewById(R.id.manager_activity_grid_layout_row1_text);
                button = findViewById(R.id.manager_activity_exit_button_row1);
                break;
            case 2:
                textView = findViewById(R.id.manager_activity_grid_layout_row2_text);
                button = findViewById(R.id.manager_activity_exit_button_row2);
                break;
            case 3:
                textView = findViewById(R.id.manager_activity_grid_layout_row3_text);
                button = findViewById(R.id.manager_activity_exit_button_row3);
                break;
            case 4:
                textView = findViewById(R.id.manager_activity_grid_layout_row4_text);
                button = findViewById(R.id.manager_activity_exit_button_row4);
                break;
            default:
                Log.e("InvalidArgument", "Invalid argument: " + num);
                return;
        }

        textView.setText(userName);

        String user_id = userList.get(num).getUserID();
        addEventListenerToExitButton(num, userName, user_id, button);
    }

    private void addEventListenerToExitButton(final int num, String userName, String user_id, ImageButton button) {
        final String userNameStr = userName;
        final String userID = user_id;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogForDeleteUser(num,
                        userNameStr,
                        userID
                );
            }
        });
    }

    private void showAlertDialogForDeleteUser(int num, String userName, String userID) {
        getApplicationContext();

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View dialogLayout = inflater.inflate(R.layout.dialog_for_delete_user, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogLayout);

        // create alert dialog
        final AlertDialog d = builder.create();

        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Window window = d.getWindow();
        assert window != null;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.CENTER);

        d.show();
        d.setContentView(R.layout.dialog_for_delete_user);

        // set text
        final TextView tv = (TextView) d.findViewById(R.id.delete_user_text_view);
        final String ALERT_MESSAGE_BODY_TEXT = " 님을 사용자 목록에서 삭제하시겠어요?";
        String textBody = userName + ALERT_MESSAGE_BODY_TEXT;
        tv.setText(textBody);

        final int index = num;
        final String user_id = userID;


        // add event listener to the button to handle onClick event

        final Button yesButton = d.findViewById(R.id.delete_user_button_yes);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String baseURL = getString(R.string.baseURL);
                NetworkUtils.sendRequestToDeleteUserInfo(baseURL, user_id, Utils.getDeviceID());

                // dismiss the alert dialog
                d.dismiss();

                // update the User cards
                updateUserAcceptance_removeUser(index);
                updateUserCards();
            }
        });

        final Button noButton = d.findViewById(R.id.delete_user_button_no);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), getText(R.string.cancel_delete_user), Toast.LENGTH_SHORT).show();
                d.dismiss();
            }
        });
    }
}
