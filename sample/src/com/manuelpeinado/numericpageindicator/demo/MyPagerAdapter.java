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

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyPagerAdapter extends PagerAdapter {

    private static final int[] COLORS = {
        Color.rgb(192, 128, 128), 
        Color.rgb(128, 192, 128),
        Color.rgb(128, 128, 192),
    };
    
    @Override
    public int getCount() {
        return 20;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TextView item = new TextView(container.getContext());
        item.setText(Integer.toString(position + 1));
        item.setTextColor(Color.argb(192, 255, 255, 255));
        item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50);
        item.setGravity(Gravity.CENTER);
        item.setBackgroundColor(COLORS[position % COLORS.length]);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(item, layoutParams);
        return item;
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

}
