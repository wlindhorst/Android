package com.texadev.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by Bill on 2/27/2018.
 */

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
