package com.example.todd.calculator;

import android.support.v4.app.Fragment;

public class CalculatorActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return CalculatorFragment.newInstance();
    }

}
