package app.wt.noolis.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import app.wt.noolis.R;
import app.wt.noolis.utils.Tools;

public class FragmentAbout extends Fragment {

    View parent_view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parent_view = inflater.inflate(R.layout.fragment_about, null);
        prepareAds();
        return parent_view;
    }

    private void prepareAds(){
        AdView mAdView = (AdView) parent_view.findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
    }
}
