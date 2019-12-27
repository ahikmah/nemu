package com.airri.nemu.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<String> name, email, photo;

    public ProfileViewModel () {
        // mendapatkan instance dari firebase
        FirebaseAuth auth = FirebaseAuth.getInstance();

        name = new MutableLiveData<>();
        name.setValue(auth.getCurrentUser().getDisplayName());

        email = new MutableLiveData<>();
        email.setValue(auth.getCurrentUser().getEmail());

        photo = new MutableLiveData<>();
        photo.setValue(auth.getCurrentUser().getPhotoUrl().toString());
    }

    public LiveData<String> getName() {
        return name;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getPhoto() {return photo; }
}
