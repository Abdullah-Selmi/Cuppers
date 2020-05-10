package com.abdullah.cuppers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    ConstraintLayout innerConstraintLayout1, innerConstraintLayout2;
    TextView userName, email, phoneNumber, address, dateOfBirth, gender;
    Button logoutButton, editButton, deleteAccountButton, cancelButton, saveButton;
    EditText userNameEditText, emailEditText, phoneNumberEditText, addressEditText,
            dayEditText, monthEditText, yearEditText;
    RadioButton maleRadioButton, femaleRadioButton;
    ProgressBar profileProgressBar;
    View view;
    FirebaseFirestore db;
    ScrollView profileFragmentScrollView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile, container, false);
        FindProfileViews();
        profileProgressBar.setVisibility(View.VISIBLE);
        innerConstraintLayout1.setVisibility(View.VISIBLE);
        innerConstraintLayout2.setVisibility(View.GONE);
        setInformation();
        profileProgressBar.setVisibility(View.GONE);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                innerConstraintLayout2();
                innerConstraintLayout1.setVisibility(View.GONE);
                innerConstraintLayout2.setVisibility(View.VISIBLE);
                profileFragmentScrollView.setScrollY(0);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseKeyboard();
                if (InformationChangeCheck()) {
                    AlertDialog diaBox = CancelDialog();
                    diaBox.show();
                } else {
                    innerConstraintLayout1.setVisibility(View.VISIBLE);
                    innerConstraintLayout2.setVisibility(View.GONE);
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseKeyboard();
                if (CheckInformation()) {
                    AlertDialog diaBox = SaveDialog();
                    diaBox.show();
                }
            }
        });
        return view;
    }

    public boolean InformationChangeCheck() {
        if (maleRadioButton.isChecked()) {
            if (!maleRadioButton.getText().toString().trim().equals(MainActivity.SaveSharedPreference.getGender(getActivity()))) {
                return true;
            }
        }
        if (femaleRadioButton.isChecked()) {
            if (!femaleRadioButton.getText().toString().trim().equals(MainActivity.SaveSharedPreference.getGender(getActivity()))) {
                return true;
            }
        }
        if (!emailEditText.getText().toString().trim().equals(MainActivity.SaveSharedPreference.getEmail(getActivity())) ||
                !userNameEditText.getText().toString().trim().equals(MainActivity.SaveSharedPreference.getUserName(getActivity())) ||
                !phoneNumberEditText.getText().toString().trim().equals(MainActivity.SaveSharedPreference.getPhoneNumber(getActivity())) ||
                !addressEditText.getText().toString().trim().equals(MainActivity.SaveSharedPreference.getAddress(getActivity())) ||
                !(dayEditText.getText().toString().trim() + "/" + monthEditText.getText().toString().trim() + "/" + yearEditText.getText().toString().trim())
                        .equals(MainActivity.SaveSharedPreference.getDateOfBirth(getActivity()))) {
            return true;
        }
        return false;
    }

    public void UploadToDataBase() {
        db.collection(getString(R.string.users))
                .document(MainActivity.SaveSharedPreference.getEmail(getActivity()))
                .update(getString(R.string.username), userNameEditText.getText().toString().trim(),
                        getString(R.string.phone), phoneNumberEditText.getText().toString().trim(),
                        getString(R.string.address), addressEditText.getText().toString().trim(),
                        getString(R.string.date_of_birth), MainActivity.SaveSharedPreference.getDateOfBirth(getActivity()),
                        getString(R.string.gender), MainActivity.SaveSharedPreference.getGender(getActivity()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                        Clickable(true);
                        profileProgressBar.setVisibility(View.GONE);
                        innerConstraintLayout1.setVisibility(View.VISIBLE);
                        innerConstraintLayout2.setVisibility(View.GONE);
                        profileFragmentScrollView.setScrollY(0);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed to save information", Toast.LENGTH_SHORT).show();
                Clickable(true);
                profileProgressBar.setVisibility(View.GONE);
                innerConstraintLayout1.setVisibility(View.VISIBLE);
                innerConstraintLayout2.setVisibility(View.GONE);
                profileFragmentScrollView.setScrollY(0);
            }
        });
    }

    public boolean CheckInformation() {
        if (userNameEditText.getText().toString().trim().equals("")) {
            userNameEditText.setError("This field is required");
            userNameEditText.requestFocus();
            return false;
        } else if (userNameEditText.getText().toString().trim().length() > 30) {
            userNameEditText.setError("Maximum length is 30");
            userNameEditText.requestFocus();
            return false;
        }
        if (phoneNumberEditText.getText().toString().trim().equals("")) {
            phoneNumberEditText.setError("This field is required");
            phoneNumberEditText.requestFocus();
            return false;
        } else if (phoneNumberEditText.getText().toString().trim().length() != 10) {
            phoneNumberEditText.setError("The phone number is short");
            phoneNumberEditText.requestFocus();
            return false;
        }
        if (dayEditText.getText().toString().trim().equals("")) {
            dayEditText.setError("This field is required");
            dayEditText.requestFocus();
            return false;
        } else if (monthEditText.getText().toString().trim().equals("")) {
            monthEditText.setError("This field is required");
            monthEditText.requestFocus();
            return false;
        } else if (yearEditText.getText().toString().trim().equals("")) {
            yearEditText.setError("This field is required");
            yearEditText.requestFocus();
            return false;
        } else {
            if (Integer.parseInt(yearEditText.getText().toString().trim()) % 4 == 0) {
                if (monthEditText.getText().toString().trim().equals("1") || monthEditText.getText().toString().trim().equals("3") || monthEditText.getText().toString().trim().equals("5")
                        || monthEditText.getText().toString().trim().equals("7") || monthEditText.getText().toString().trim().equals("8") || monthEditText.getText().toString().trim().equals("10")
                        || monthEditText.getText().toString().trim().equals("12")) {
                    if (Integer.parseInt(dayEditText.getText().toString().trim()) > 31 || Integer.parseInt(dayEditText.getText().toString().trim()) < 1) {
                        dayEditText.setError("Enter valid day");
                        dayEditText.requestFocus();
                        return false;
                    }
                } else if (monthEditText.getText().toString().trim().equals("4") || monthEditText.getText().toString().trim().equals("6") || monthEditText.getText().toString().trim().equals("9")
                        || monthEditText.getText().toString().trim().equals("11")) {
                    if (Integer.parseInt(dayEditText.getText().toString().trim()) > 30 || Integer.parseInt(dayEditText.getText().toString().trim()) < 1) {
                        dayEditText.setError("Enter valid day");
                        dayEditText.requestFocus();
                        return false;
                    }
                } else if (monthEditText.getText().toString().trim().equals("2")) {
                    if (Integer.parseInt(dayEditText.getText().toString().trim()) > 29 || Integer.parseInt(dayEditText.getText().toString().trim()) < 1) {
                        dayEditText.setError("Enter valid day");
                        dayEditText.requestFocus();
                        return false;
                    }
                } else {
                    monthEditText.setError("Enter valid month");
                    monthEditText.requestFocus();
                    return false;
                }
            } else {
                if (monthEditText.getText().toString().trim().equals("1") || monthEditText.getText().toString().trim().equals("3") || monthEditText.getText().toString().trim().equals("5")
                        || monthEditText.getText().toString().trim().equals("7") || monthEditText.getText().toString().trim().equals("8") || monthEditText.getText().toString().trim().equals("10")
                        || monthEditText.getText().toString().trim().equals("12")) {
                    if (Integer.parseInt(dayEditText.getText().toString().trim()) > 31 || Integer.parseInt(dayEditText.getText().toString().trim()) < 1) {
                        dayEditText.setError("Enter valid day");
                        dayEditText.requestFocus();
                        return false;
                    }
                } else if (monthEditText.getText().toString().trim().equals("4") || monthEditText.getText().toString().trim().equals("6") || monthEditText.getText().toString().trim().equals("9")
                        || monthEditText.getText().toString().trim().equals("11")) {
                    if (Integer.parseInt(dayEditText.getText().toString().trim()) > 30 || Integer.parseInt(dayEditText.getText().toString().trim()) < 1) {
                        dayEditText.setError("Enter valid day");
                        dayEditText.requestFocus();
                        return false;
                    }
                } else if (monthEditText.getText().toString().trim().equals("2")) {
                    if (Integer.parseInt(dayEditText.getText().toString().trim()) > 28 || Integer.parseInt(dayEditText.getText().toString().trim()) < 1) {
                        dayEditText.setError("Enter valid day");
                        dayEditText.requestFocus();
                        return false;
                    }
                } else {
                    monthEditText.setError("Enter valid month");
                    monthEditText.requestFocus();
                    return false;
                }
            }
        }
        if (!maleRadioButton.isChecked() && !femaleRadioButton.isChecked()) {
            Toast.makeText(getActivity(), "Please select your gender", Toast.LENGTH_SHORT).show();
            userNameEditText.requestFocus();
            return false;
        }
        return true;
    }

    public void innerConstraintLayout1() {
        userName.setText(userNameEditText.getText().toString().trim());
//        userName.setTextColor(getResources().getColor(R.color.white));
        MainActivity.SaveSharedPreference.setUserName(getActivity(), userNameEditText.getText().toString().trim());
        phoneNumber.setText(phoneNumberEditText.getText().toString().trim());
//        phoneNumber.setTextColor(getResources().getColor(R.color.white));
        MainActivity.SaveSharedPreference.setPhoneNumber(getActivity(), phoneNumberEditText.getText().toString().trim());
        if (addressEditText.getText().toString().trim().equals("")) {
            address.setText(R.string.no_address);
//            address.setTextColor(getResources().getColor(R.color.outOfStock));
            MainActivity.SaveSharedPreference.setAddress(getActivity(), "");
        } else {
            address.setText(addressEditText.getText().toString().trim());
//            address.setTextColor(getResources().getColor(R.color.white));
            MainActivity.SaveSharedPreference.setAddress(getActivity(), addressEditText.getText().toString().trim());
        }
        dateOfBirth.setText(dayEditText.getText().toString().trim() + "/" + monthEditText.getText().toString().trim() + "/"
                + yearEditText.getText().toString().trim());
//        dateOfBirth.setTextColor(getResources().getColor(R.color.white));
        MainActivity.SaveSharedPreference.setDateOfBirth(getActivity(), dayEditText.getText().toString().trim() + "/" +
                monthEditText.getText().toString().trim() + "/" + yearEditText.getText().toString().trim());
        if (maleRadioButton.isChecked()) {
            gender.setText(maleRadioButton.getText().toString().trim());
            MainActivity.SaveSharedPreference.setGender(getActivity(), maleRadioButton.getText().toString().trim());
        } else {
            gender.setText(femaleRadioButton.getText().toString().trim());
            MainActivity.SaveSharedPreference.setGender(getActivity(), femaleRadioButton.getText().toString().trim());
        }
//        gender.setTextColor(getResources().getColor(R.color.white));
    }

    public void innerConstraintLayout2() {
        if (email.getText().toString().trim().equals(getString(R.string.no_email))) {
            emailEditText.setText("");
        } else {
            emailEditText.setText(email.getText().toString().trim());
            emailEditText.setActivated(false);
        }
        if (userName.getText().toString().trim().equals(getString(R.string.no_user_name))) {
            userNameEditText.setText("");
        } else {
            userNameEditText.setText(userName.getText().toString().trim());
        }
        if (phoneNumber.getText().toString().trim().equals(getString(R.string.no_phone_number))) {
            phoneNumberEditText.setText("");
        } else {
            phoneNumberEditText.setText(phoneNumber.getText().toString().trim());
        }
        if (address.getText().toString().trim().equals(getString(R.string.no_address))) {
            addressEditText.setText("");
        } else {
            addressEditText.setText(address.getText().toString().trim());
        }
        if (dateOfBirth.getText().toString().trim().equals(getString(R.string.no_date_of_birth))) {
            dayEditText.setText("");
            monthEditText.setText("");
            yearEditText.setText("");
        } else {
            String[] dateOfBirtharray = dateOfBirth.getText().toString().trim().split("/");
            dayEditText.setText(dateOfBirtharray[0]);
            monthEditText.setText(dateOfBirtharray[1]);
            yearEditText.setText(dateOfBirtharray[2]);
        }
        if (gender.getText().toString().trim().equals(getString(R.string.no_gender))) {
            maleRadioButton.setChecked(false);
            femaleRadioButton.setChecked(false);
        } else {
            if (gender.getText().toString().trim().equals("male")) {
                maleRadioButton.setChecked(true);
            } else {
                femaleRadioButton.setChecked(true);
            }
        }
    }

    public void FindProfileViews() {
        profileFragmentScrollView = view.findViewById(R.id.profileFragmentScrollView);
        innerConstraintLayout1 = view.findViewById(R.id.innerConstraintLayout1);
        innerConstraintLayout2 = view.findViewById(R.id.innerConstraintLayout2);
        userName = view.findViewById(R.id.userName);
        email = view.findViewById(R.id.email);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        address = view.findViewById(R.id.address);
        dateOfBirth = view.findViewById(R.id.dateOfBirth);
        gender = view.findViewById(R.id.gender);
        logoutButton = view.findViewById(R.id.logoutButton);
        editButton = view.findViewById(R.id.editButton);
        deleteAccountButton = view.findViewById(R.id.deleteAccountButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        saveButton = view.findViewById(R.id.saveButton);
        userNameEditText = view.findViewById(R.id.userNameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText);
        addressEditText = view.findViewById(R.id.addressEditText);
        dayEditText = view.findViewById(R.id.dayEditText);
        monthEditText = view.findViewById(R.id.monthEditText);
        yearEditText = view.findViewById(R.id.yearEditText);
        maleRadioButton = view.findViewById(R.id.maleRadioButton);
        femaleRadioButton = view.findViewById(R.id.femaleRadioButton);
        profileProgressBar = view.findViewById(R.id.profileProgressBar);
        db = FirebaseFirestore.getInstance();
    }

    public void setInformation() {
        if (MainActivity.SaveSharedPreference.getEmail(getActivity()).equals("")) {
            email.setText(getString(R.string.no_email));
//            email.setTextColor(getResources().getColor(R.color.outOfStock));
        } else {
            email.setText(MainActivity.SaveSharedPreference.getEmail(getActivity()));
//            email.setTextColor(getResources().getColor(R.color.white));
        }
        if (MainActivity.SaveSharedPreference.getUserName(getActivity()).equals("")) {
            userName.setText(getString(R.string.no_user_name));
//            userName.setTextColor(getResources().getColor(R.color.outOfStock));
        } else {
            userName.setText(MainActivity.SaveSharedPreference.getUserName(getActivity()));
//            userName.setTextColor(getResources().getColor(R.color.white));
        }
        if (MainActivity.SaveSharedPreference.getPhoneNumber(getActivity()).equals("")) {
            phoneNumber.setText(getString(R.string.no_phone_number));
//            phoneNumber.setTextColor(getResources().getColor(R.color.outOfStock));
        } else {
            phoneNumber.setText(MainActivity.SaveSharedPreference.getPhoneNumber(getActivity()));
//            phoneNumber.setTextColor(getResources().getColor(R.color.white));
        }
        if (MainActivity.SaveSharedPreference.getAddress(getActivity()).equals("")) {
            address.setText(getString(R.string.no_address));
//            address.setTextColor(getResources().getColor(R.color.outOfStock));
        } else {
            address.setText(MainActivity.SaveSharedPreference.getAddress(getActivity()));
//            address.setTextColor(getResources().getColor(R.color.white));
        }
        if (MainActivity.SaveSharedPreference.getDateOfBirth(getActivity()).equals("//")) {
            dateOfBirth.setText(getString(R.string.no_date_of_birth));
//            dateOfBirth.setTextColor(getResources().getColor(R.color.outOfStock));
        } else {
            dateOfBirth.setText(MainActivity.SaveSharedPreference.getDateOfBirth(getActivity()));
//            dateOfBirth.setTextColor(getResources().getColor(R.color.white));
        }
        if (MainActivity.SaveSharedPreference.getGender(getActivity()).equals("")) {
            gender.setText(getString(R.string.no_gender));
//            gender.setTextColor(getResources().getColor(R.color.outOfStock));
        } else {
            gender.setText(MainActivity.SaveSharedPreference.getGender(getActivity()));
//            gender.setTextColor(getResources().getColor(R.color.white));
        }
    }

    public void Clickable(boolean b) {
        if (b) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    public boolean Connected() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }

    public void CloseKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public AlertDialog SaveDialog() {
        final AlertDialog myQuittingDialogBox = new AlertDialog.Builder(getActivity())
                .setTitle("Save")
                .setMessage("Do you want to save the changes ?")
                .setIcon(R.drawable.ic_done)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (Connected()) {
                            innerConstraintLayout1();
                            Clickable(false);
                            profileProgressBar.setVisibility(View.VISIBLE);
                            UploadToDataBase();
                        } else {
                            Toast.makeText(getActivity(), R.string.check_the_internet_connection, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        myQuittingDialogBox.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                myQuittingDialogBox.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.outOfStock));
                myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
            }
        });

        return myQuittingDialogBox;
    }

    public AlertDialog CancelDialog() {
        final AlertDialog myQuittingDialogBox = new AlertDialog.Builder(getActivity())
                .setTitle("Discard Changes")
                .setMessage("Do you want to discard the changes ?")
                .setIcon(R.drawable.ic_cancel)
                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        innerConstraintLayout1.setVisibility(View.VISIBLE);
                        innerConstraintLayout2.setVisibility(View.GONE);
                        dialog.dismiss();
                        profileFragmentScrollView.setScrollY(0);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        myQuittingDialogBox.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                myQuittingDialogBox.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
                myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.outOfStock));
            }
        });

        return myQuittingDialogBox;
    }

}
