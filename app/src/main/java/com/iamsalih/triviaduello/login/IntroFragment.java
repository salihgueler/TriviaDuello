package com.iamsalih.triviaduello.login;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iamsalih.triviaduello.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by muhammedsalihguler on 22.11.17.
 */

public class IntroFragment extends Fragment {

    private static final String KEY_POSITION = "position";

    @BindView(R.id.onboarding_image)
    ImageView onboardingImage;

    @BindView(R.id.onboarding_text)
    TextView onboardingText;

    public static IntroFragment newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        IntroFragment fragment = new IntroFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.intro_page_layout, container, false);
        ButterKnife.bind(this, rootView);

        // Get position of fragment
        int position = getArguments().getInt(KEY_POSITION);

        // Get resources related to position
        int[] colors = getResources().getIntArray(R.array.onboarding_background_colors);
        TypedArray images = getResources().obtainTypedArray(R.array.onboarding_image_resource);
        String[] texts = getResources().getStringArray(R.array.onboarding_string_resource);

        // Assign the values
        rootView.setBackgroundColor(colors[position]);
        onboardingImage.setImageResource(images.getResourceId(position, -1));
        onboardingText.setText(texts[position]);

        return rootView;
    }
}
