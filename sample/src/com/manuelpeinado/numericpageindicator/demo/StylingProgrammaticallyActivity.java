/*
 * Copyright (C) 2013 Manuel Peinado
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.manuelpeinado.numericpageindicator.demo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.manuelpeinado.numericpageindicator.NumericPageIndicator;

public class StylingProgrammaticallyActivity extends SherlockActivity {
    private ViewPager viewPager;
    private NumericPageIndicator pageIndicator;

    @Override   
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new MyPagerAdapter());
        pageIndicator = (NumericPageIndicator) findViewById(R.id.pageIndicator);
        pageIndicator.setViewPager(viewPager);
        
        pageIndicator.setTextTemplate("Page: #i");
        pageIndicator.setPreviousButtonText("Back");
        pageIndicator.setNextButtonText("Forth");
        pageIndicator.setTextColor(Color.argb(128, 255, 255, 255));
        pageIndicator.setPageNumberTextColor(Color.argb(192, 255, 255, 255));
        pageIndicator.setPageNumberTextBold(false);
        final float scale = getResources().getDisplayMetrics().density;
        pageIndicator.setTextSize((int)(12 * scale + 0.5f));
        pageIndicator.setTopPadding((int)(7 * scale + 0.5f));
        pageIndicator.setBottomPadding((int)(7 * scale + 0.5f));
        pageIndicator.setBackgroundColor(Color.rgb(64, 96, 64));
        pageIndicator.setPressedButtonColor(Color.argb(128, 255, 255, 255));
        pageIndicator.setShowChangePageButtons(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent parentActivityIntent = new Intent(this, HomeActivity.class);
            parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(parentActivityIntent);
            finish();
            return true;
        }
        return false;
    }
}
