package com.iamsalih.triviaduello.settings.profile;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iamsalih.triviaduello.AppConstants;
import com.iamsalih.triviaduello.R;
import com.iamsalih.triviaduello.TriviaDuelloApplication;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by muhammedsalihguler on 09.12.17.
 */

public class ProfileActivity extends AppCompatActivity {

    @Inject
    FirebaseUser user;

    @Inject
    FirebaseDatabase firebaseDatabase;

    @BindView(R.id.profile_picture)
    ImageView profilePicture;

    @BindView(R.id.delete_account_button)
    Button deleteAccount;

    @BindView(R.id.user_name_text)
    TextView userNameText;

    @BindView(R.id.loading_indicator_holder)
    RelativeLayout loadingIndicatorHolder;

    private static final int RC_PHOTO_PICKER = 1;
    private StorageReference mChatPhotosStorageReference;
    private FirebaseStorage mFirebaseStorage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_fragment_view);
        ButterKnife.bind(this);
        TriviaDuelloApplication.firebaseComponent.inject(this);
        mFirebaseStorage = FirebaseStorage.getInstance();
        mChatPhotosStorageReference = mFirebaseStorage.getReference().child(user.getUid());
        checkIfPhotoFolderExists();
        userNameText.setText(String.format(getString(R.string.user_name_in_profile),
                TextUtils.isEmpty(user.getDisplayName()) ? user.getEmail() : user.getDisplayName()));
    }

    private void checkIfPhotoFolderExists() {
        mChatPhotosStorageReference.child(AppConstants.PROFILE_PHOTO_KEY).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(ProfileActivity.this).load(task.getResult()).
                            listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    loadingIndicatorHolder.setVisibility(View.GONE);
                                    profilePicture.setImageResource(R.drawable.ic_add_a_photo_black);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    loadingIndicatorHolder.setVisibility(View.GONE);
                                    return false;
                                }
                            }).into(profilePicture);
                } else {
                    loadingIndicatorHolder.setVisibility(View.GONE);
                    profilePicture.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_a_photo_black));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingIndicatorHolder.setVisibility(View.GONE);
                profilePicture.setImageResource(R.drawable.ic_add_a_photo_black);
            }
        });
    }

    @OnClick(R.id.profile_picture)
    public void addProfileImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            loadingIndicatorHolder.setVisibility(View.VISIBLE);
            profilePicture.setVisibility(View.INVISIBLE);
            Uri selectedImageUri = data.getData();

            // Get a reference to store file at chat_photos/<FILENAME>
            StorageReference photoRef = mChatPhotosStorageReference.child(AppConstants.PROFILE_PHOTO_KEY);

            // Upload file to Firebase Storage
            photoRef.putFile(selectedImageUri)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // When the image has successfully uploaded, we get its download URL
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            Glide.with(ProfileActivity.this).load(downloadUrl).
                                    listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            loadingIndicatorHolder.setVisibility(View.GONE);
                                            profilePicture.setVisibility(View.VISIBLE);
                                            profilePicture.setImageResource(R.drawable.ic_add_a_photo_black);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            loadingIndicatorHolder.setVisibility(View.GONE);
                                            profilePicture.setVisibility(View.VISIBLE);
                                            return false;
                                        }
                                    }).into(profilePicture);
                        }
                    });
        }
    }

    @OnClick(R.id.delete_account_button)
    public void deleteAccount() {
        user.delete()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Timber.d( "User account deleted.");
                    }
                }
            });
    }
}
