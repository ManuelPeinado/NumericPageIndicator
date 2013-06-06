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
package com.manuelpeinado.numericpageindicator;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.viewpagerindicator.PageIndicator;

/**
 * Class that implements a ViewPager indicator fully compatible with with Jake Wharton's 
 * ViewPagerIndicator library. 
 * <p>
 * It displays the number of the current page, and (optionally) the total number of pages.
 * This information is formatted using a customizable template.
 * <p>
 * It can also display buttons to go to the previous page and to the next page.
 */
public class NumericPageIndicator extends View implements PageIndicator {
    protected static final String TAG = NumericPageIndicator.class.getSimpleName();
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;
    private int mCurrentPage = -1;
    private float mPageOffset;
    private int mScrollState;
    private final Paint mPaintText = new Paint();
    private final Paint mPaintPageNumberText = new Paint();
    private final Paint mPaintButtonBackground = new Paint();
    private int mColorText;
    private float mPaddingTop;
    private float mPaddingBottom;
    private float mTextBottom;
    private int mTextHeight;
    private final Rect mRectPreviousText = new Rect();
    private final Rect mRectNextText = new Rect();
    private final Rect mRectPrevious = new Rect();
    private final Rect mRectNext = new Rect();
    private final Rect mRectStartText = new Rect();
    private final Rect mRectEndText = new Rect();
    private final Rect mRectStart = new Rect();
    private final Rect mRectEnd = new Rect();
    private boolean mPreviousDown;
    private boolean mNextDown;
    private boolean mStartDown;
    private boolean mEndDown;
    private float mWidthNextText;
    private float mWidthPreviousText;
    private float mWidthStartText;
    private float mWidthEndText;
    private int mColorPressedButton;
    private boolean mShowChangePageButtons;
    private boolean mShowStartEndButtons;
    private int mColorPageNumberText;
    private boolean mPageNumberTextBold;
    private String mTextTemplate;
    private String mTextTemplateFirstPart;
    private String mTextTemplateSecondPart;
    private String mTextFirstPart;
    private String mTextLastPart;
    private String mTextPreviousButton;
    private String mTextNextButton;
    private String mTextStartButton;
    private String mTextEndButton;
    private static final String TEMPLATE_PAGE_NUMBER_PLACEHOLDER = "#i";
    private static final String TEMPLATE_PAGE_COUNT_PLACEHOLDER = "#N";

    public NumericPageIndicator(Context context) {
        this(context, null);
    }

    public NumericPageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.numericPageIndicatorStyle);
    }

    @SuppressWarnings("deprecation")
    public NumericPageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) {
            return;
        }

        // Load defaults from resources
        final Resources res = getResources();
        final int defaultTextColor = res.getColor(R.color.default_page_number_indicator_text_color);
        final int defaultPageNumberTextColor = res.getColor(R.color.default_page_number_indicator_page_number_text_color);
        final boolean defaultPageNumberTextBold = res.getBoolean(R.bool.default_page_number_indicator_page_number_text_bold);
        final int defaultButtonPressedColor = res.getColor(R.color.default_page_number_indicator_pressed_button_color);
        final float defaultTopPadding = res.getDimension(R.dimen.default_page_number_indicator_top_padding);
        final float defaultBottomPadding = res.getDimension(R.dimen.default_page_number_indicator_bottom_padding);
        final float defaultTextSize = res.getDimension(R.dimen.default_page_number_indicator_text_size);
        final boolean defaultShowChangePageButtons = res.getBoolean(R.bool.default_page_number_indicator_show_change_page_buttons);
        final boolean defaultShowStartEndButtons = res.getBoolean(R.bool.default_page_number_indicator_show_start_end_buttons);
        
        // Retrieve styles attributes
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumericPageIndicator, defStyle, 0);

        mTextTemplate = a.getString(R.styleable.NumericPageIndicator_textTemplate);
        if (mTextTemplate == null) {
            mTextTemplate = res.getString(R.string.default_page_number_indicator_text_template);
            ;
        }
        parseTextTemplate();

        mTextStartButton = a.getString(R.styleable.NumericPageIndicator_startButtonText);
        if (mTextStartButton == null) {
            mTextStartButton = res.getString(R.string.default_page_number_indicator_start_button_text);
        }
        mTextEndButton = a.getString(R.styleable.NumericPageIndicator_endButtonText);
        if (mTextEndButton == null) {
            mTextEndButton = res.getString(R.string.default_page_number_indicator_end_button_text);
        }
        mTextPreviousButton = a.getString(R.styleable.NumericPageIndicator_previousButtonText);
        if (mTextPreviousButton == null) {
            mTextPreviousButton = res.getString(R.string.default_page_number_indicator_previous_button_text);
        }
        mTextNextButton = a.getString(R.styleable.NumericPageIndicator_nextButtonText);
        if (mTextNextButton == null) {
            mTextNextButton = res.getString(R.string.default_page_number_indicator_next_button_text);
        }

        mColorText = a.getColor(R.styleable.NumericPageIndicator_android_textColor, defaultTextColor);
        mColorPageNumberText = a.getColor(R.styleable.NumericPageIndicator_pageNumberTextColor, defaultPageNumberTextColor);
        mPageNumberTextBold = a.getBoolean(R.styleable.NumericPageIndicator_pageNumberTextBold, defaultPageNumberTextBold);
        mColorPressedButton = a.getColor(R.styleable.NumericPageIndicator_pressedButtonColor, defaultButtonPressedColor);
        mPaddingTop = a.getDimension(R.styleable.NumericPageIndicator_android_paddingTop, defaultTopPadding);
        mPaddingBottom = a.getDimension(R.styleable.NumericPageIndicator_android_paddingBottom, defaultBottomPadding);
        mPaintText.setColor(mColorText);
        mShowChangePageButtons = a.getBoolean(R.styleable.NumericPageIndicator_showChangePageButtons, defaultShowChangePageButtons);
        mShowStartEndButtons = a.getBoolean(R.styleable.NumericPageIndicator_showStartEndButtons, defaultShowStartEndButtons);
        
        mPaintButtonBackground.setColor(mColorPressedButton);
        final float textSize = a.getDimension(R.styleable.NumericPageIndicator_android_textSize, defaultTextSize);
        mPaintText.setTextSize(textSize);
        mPaintText.setAntiAlias(true);

        mPaintPageNumberText.setColor(mColorPageNumberText);
        mPaintPageNumberText.setTextSize(textSize);
        mPaintPageNumberText.setAntiAlias(true);
        if (mPageNumberTextBold) {
            mPaintPageNumberText.setTypeface(Typeface.DEFAULT_BOLD);
        }

        final Drawable background = a.getDrawable(R.styleable.NumericPageIndicator_android_background);
        if (background != null) {
            setBackgroundDrawable(background);
        }

        a.recycle();
    }

    /**
     * @return The template used to generate the text used by the indicator to show the page
     *  number and (optionally) the page count
     */
    public String getTextTemplate() {
        return mTextTemplate;
    }

    /**
     * Sets the template used to generate the text used by the indicator to show the page
     *  number and (optionally) the page count.<p>The provided template must contain the text
     *  #i, which will be replaced by the page number. If can also contain the optional 
     *  text #N, which will be replaced by the number of pages. <p>For example: "Page #i of #N"
     * @param textTemplate The desired text template
     */
    public void setTextTemplate(String textTemplate) {
        mTextTemplate = textTemplate;
        parseTextTemplate();
        updateText();
        invalidate();
    }

    /**
     * Returns the text of the "start page" button
     */
    public String getStartButtonText() {
        return mTextStartButton;
    }

    /**
     * Sets the text of the "start page" button
     * @param textStartButton The desired text
     */
    public void setStartButtonText(String textStartButton) {
        this.mTextStartButton = textStartButton;
        invalidate();
    }

    /**
     * Returns the text of the "next page" button
     */
    public String getNextButtonText() {
        return mTextNextButton;
    }

    /**
     * Sets the text of the "next page" button
     * @param textPreviousButton The desired text
     */
    public void setNextButtonText(String textNextButton) {
        this.mTextNextButton = textNextButton;
        invalidate();
    }

    /**
     * Returns the text of the "previous page" button
     */
    public String getPreviousButtonText() {
        return mTextPreviousButton;
    }

    /**
     * Sets the text of the "previous page" button
     * @param textPreviousButton The desired text
     */
    public void setPreviousButtonText(String textPreviousButton) {
        this.mTextPreviousButton = textPreviousButton;
        invalidate();
    }
    
    /**
     * Returns the text of the "end page" button
     */
    public String getEndButtonText() {
        return mTextEndButton;
    }

    /**
     * Sets the text of the "end page" button
     * @param textStartButton The desired text
     */
    public void setEndButtonText(String textEndButton) {
        this.mTextEndButton = textEndButton;
        invalidate();
    }

    public int getTextColor() {
        return mColorText;
    }

    public void setTextColor(int textColor) {
        mPaintText.setColor(textColor);
        mColorText = textColor;
        invalidate();
    }

    public int getPageNumberTextColor() {
        return mColorText;
    }

    public void setPageNumberTextColor(int pageNumberTextColor) {
        mPaintPageNumberText.setColor(pageNumberTextColor);
        mColorPageNumberText = pageNumberTextColor;
        invalidate();
    }

    public boolean isPageNumberTextBold() {
        return mPageNumberTextBold;
    }

    public void setPageNumberTextBold(boolean pageNumberTextBold) {
        mPageNumberTextBold = pageNumberTextBold;
        final Typeface typeface = mPageNumberTextBold ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT;
        mPaintPageNumberText.setTypeface(typeface);
        invalidate();
    }

    public int getPressedButtonColor() {
        return mColorPressedButton;
    }

    public void setPressedButtonColor(int color) {
        mPaintButtonBackground.setColor(color);
        mColorPressedButton = color;
        invalidate();
    }

    public float getTextSize() {
        return mPaintText.getTextSize();
    }

    /**
     * Set the default text size to the given value, interpreted as "pixels"
     * pixel" units.
     * 
     * @param textSize
     *            The text size in pixels
     */
    public void setTextSize(float textSize) {
        mPaintText.setTextSize(textSize);
        mPaintPageNumberText.setTextSize(textSize);
        invalidate();
    }

    public float getTopPadding() {
        return this.mPaddingTop;
    }

    public void setTopPadding(float topPadding) {
        mPaddingTop = topPadding;
        invalidate();
    }

    public float getBottomPadding() {
        return this.mPaddingBottom;
    }

    public void setBottomPadding(float bottomPadding) {
        mPaddingBottom = bottomPadding;
        invalidate();
    }

    public void setTypeface(Typeface typeface) {
        mPaintText.setTypeface(typeface);
        invalidate();
    }

    public Typeface getTypeface() {
        return mPaintText.getTypeface();
    }

    public boolean isShowChangePageButtons() {
        return mShowChangePageButtons;
    }

    public void setShowChangePageButtons(boolean showChangePageButtons) {
        this.mShowChangePageButtons = showChangePageButtons;
        invalidate();
    }
    
    public boolean isShowStartEndButtons() {
        return mShowStartEndButtons;
    }

    public void setShowStartEndButtons(boolean showStartEndButtons) {
        this.mShowStartEndButtons = showStartEndButtons;
        invalidate();
    }

    /**
     * Parses a template (e.g. "Page #i of #N) splitting it in two parts: the
     * one before the page number "Page " and the one after (" of #N"). This is
     * necessary because when we draw the text we'll draw the page number
     * independently, as it has to fade out when the uses swipes to a new page
     * 
     * @param template The text template to be parsed (e.g. "Page #i of #N)
     */
    private void parseTextTemplate() {
        final String placeholder = TEMPLATE_PAGE_NUMBER_PLACEHOLDER;
        final int indexOfPageNumber = mTextTemplate.indexOf(placeholder);
        if (indexOfPageNumber == -1) {
            throw new RuntimeException("The template must contain the page number placeholder \"" + placeholder + "\"");
        }
        mTextTemplateFirstPart = mTextTemplate.substring(0, indexOfPageNumber);
        mTextTemplateSecondPart = mTextTemplate.substring(indexOfPageNumber + placeholder.length());
    }

    /**
     * Updates the two "fixed" parts of the indicator text. The first part is
     * the one before the page number (e.g. "Page ") and the second part is the
     * one after (" of #N"). Updating consists simply on replacing the #N part
     * by the actual page count. So in the example above the outcome would be:
     * <li>First part: "Page " (unmodified) <li>Second part: " of 20" ("#N"
     * replaced by "20")
     */
    private void updateText() {
        final int pageCount = mViewPager.getAdapter().getCount();
        final String textPageCount = Integer.toString(pageCount);
        final String placeholder = TEMPLATE_PAGE_COUNT_PLACEHOLDER;
        mTextFirstPart = mTextTemplateFirstPart.replace(placeholder, textPageCount);
        mTextLastPart = mTextTemplateSecondPart.replace(placeholder, textPageCount);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mViewPager == null) {
            return;
        }
        final int count = mViewPager.getAdapter().getCount();
        if (count == 0) {
            return;
        }

        // mCurrentPage is -1 on first start and after orientation changed. If
        // so, retrieve the correct index from viewpager.
        if (mCurrentPage == -1 && mViewPager != null) {
            mCurrentPage = mViewPager.getCurrentItem();
        }

        // Handle the first time we draw the view, when onMeasure has not been
        // called yet
        if (mTextFirstPart == null) {
            updateText();
        }

        // Draw the main text (e.g. "Page 1 of 20"). The hardest part is drawing
        // the page
        // number itself, because of the animated effect in which the current
        // page fades
        // out and the next one fades in. In order to implement this effect we
        // are forced to
        // draw the text in four "chunks": the first part ("Page "), the current
        // page
        // number ("1"), the next page number ("2"), and the last part
        // (" of 20").
        // To implement the fade in and fade out animations we simply change the
        // alpha
        // of the page number text, relative to the view pager scroll

        final float currentPageWeight = 1 - mPageOffset;
        final float nextPageWeight = mPageOffset;
        final float firstPartWidth = mPaintText.measureText(mTextFirstPart);
        final String currentPageNumber = Integer.toString(mCurrentPage + 1);
        final String nextPageNumber = Integer.toString(mCurrentPage + 2);
        final float currentPageNumberWidth = mPaintText.measureText(currentPageNumber);
        final float nextPageNumberWidth = mPaintText.measureText(nextPageNumber);
        final float pageNumberWidth = currentPageWeight * currentPageNumberWidth + nextPageWeight * nextPageNumberWidth;
        final float lastPartWidth = mPaintText.measureText(mTextLastPart);
        final float totalWidth = firstPartWidth + pageNumberWidth + lastPartWidth;
        float currentX = (getWidth() - totalWidth) / 2;
        canvas.drawText(mTextFirstPart, currentX, mTextBottom, mPaintText);
        currentX += firstPartWidth;
        final float pageNumberCenterX = currentX + pageNumberWidth / 2;

        final int startAlpha = Color.alpha(mColorPageNumberText);
        final int endAlpha = 0;
        final float currentPageNumberAlpha = currentPageWeight * startAlpha + nextPageWeight * endAlpha;

        mPaintPageNumberText.setAlpha((int) currentPageNumberAlpha);
        canvas.drawText(currentPageNumber, pageNumberCenterX - currentPageNumberWidth / 2, mTextBottom, mPaintPageNumberText);

        final float nextPageNumberAlpha = nextPageWeight * startAlpha + currentPageWeight * endAlpha;
        mPaintPageNumberText.setAlpha((int) nextPageNumberAlpha);
        canvas.drawText(nextPageNumber, pageNumberCenterX - nextPageNumberWidth / 2, mTextBottom, mPaintPageNumberText);

        currentX += pageNumberWidth;
        canvas.drawText(mTextLastPart, currentX, mTextBottom, mPaintText);

        // Draw the "start" and "end" buttons
        if (mShowStartEndButtons) {
            final int textStartAlpha = Color.alpha(mColorText);
            final int textEndAlpha = 0;
            if (mCurrentPage != 0 && mStartDown) {
                canvas.drawRect(mRectStart, mPaintButtonBackground);
            }
            if (mCurrentPage != count - 1 && mEndDown) {
                canvas.drawRect(mRectEnd, mPaintButtonBackground);
            }
            if (mCurrentPage == 0) {
                mPaintText.setAlpha((int) (nextPageWeight * textStartAlpha + currentPageWeight * textEndAlpha));
            }
            canvas.drawText(mTextStartButton, mRectStart.centerX() - mWidthStartText / 2, mRectStartText.bottom, mPaintText);
            mPaintText.setAlpha(Color.alpha(mColorText));
            if (mCurrentPage < count - 1) {
                if (mCurrentPage == count - 2) {
                    mPaintText.setAlpha((int) (currentPageWeight * textStartAlpha + nextPageWeight * textEndAlpha));
                }
                canvas.drawText(mTextEndButton, mRectEnd.centerX() - mWidthEndText / 2, mRectEndText.bottom, mPaintText);
                mPaintText.setAlpha(Color.alpha(mColorText));
            }
        }

        
        // Draw the "next" and "previous" buttons
        if (mShowChangePageButtons) {
            final int textStartAlpha = Color.alpha(mColorText);
            final int textEndAlpha = 0;
            if (mCurrentPage != 0 && mPreviousDown) {
                canvas.drawRect(mRectPrevious, mPaintButtonBackground);
            } 
            if (mCurrentPage != count - 1 && mNextDown) {
                canvas.drawRect(mRectNext, mPaintButtonBackground);
            }
            if (mCurrentPage == 0) {
                mPaintText.setAlpha((int) (nextPageWeight * textStartAlpha + currentPageWeight * textEndAlpha));
            }
            canvas.drawText(mTextPreviousButton, mRectPrevious.centerX() - mWidthPreviousText / 2, mRectPreviousText.bottom, mPaintText);
            mPaintText.setAlpha(Color.alpha(mColorText));
            if (mCurrentPage < count - 1) {
                if (mCurrentPage == count - 2) {
                    mPaintText.setAlpha((int) (currentPageWeight * textStartAlpha + nextPageWeight * textEndAlpha));
                }
                canvas.drawText(mTextNextButton, mRectNext.centerX() - mWidthNextText / 2, mRectNextText.bottom, mPaintText);
                mPaintText.setAlpha(Color.alpha(mColorText));
            }
        }
    }

    @Override
    public void setViewPager(ViewPager view) {
        if (mViewPager == view) {
            return;
        }
        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(null);
        }
        if (view.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = view;
        mViewPager.setOnPageChangeListener(this);
        invalidate();
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    @Override
    public void notifyDataSetChanged() {
        invalidate();
    }

    @Override
    public void setCurrentItem(int item) {
        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        mViewPager.setCurrentItem(item);
        mCurrentPage = item;
        updateText();
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mScrollState = state;

        if (mListener != null) {
            mListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mCurrentPage = position;
        mPageOffset = positionOffset;
        invalidate();

        if (mListener != null) {
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
            mCurrentPage = position;
            invalidate();
        }

        if (mListener != null) {
            mListener.onPageSelected(position);
        }
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        float height;
        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            mTextHeight = (int) (mPaintText.descent() - mPaintText.ascent());
            height = mTextHeight + mPaddingTop + mPaddingBottom;
        }
        final int measuredHeight = (int) height;
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        final float horizontalPadding = -mPaintText.ascent();
        mTextBottom = h - mPaddingBottom - mPaintText.descent();

        float leftOffset = 0;
        float rightOffset = 0;
        if (mShowStartEndButtons) {
            mWidthStartText = mPaintText.measureText(mTextStartButton);
            final float startButtonWidth = mWidthStartText + 2 * horizontalPadding;
            mWidthEndText = mPaintText.measureText(mTextEndButton);
            final float endButtonWidth = mWidthEndText + 2 * horizontalPadding;
            mRectStartText.set((int) horizontalPadding, (int) mPaddingTop, (int) startButtonWidth, (int) mTextBottom);
            mRectEndText.set((int) (w - endButtonWidth), (int) mPaddingTop, (int) (w - horizontalPadding), (int) mTextBottom);
            mRectStart.set(0, 0, (int) startButtonWidth, h);
            mRectEnd.set((int) (w - startButtonWidth), 0, w, h);
            leftOffset = startButtonWidth;
            rightOffset = endButtonWidth;
        }
        
        if (mShowChangePageButtons) {
            mWidthPreviousText = mPaintText.measureText(mTextPreviousButton);
            final float previousButtonWidth = mWidthPreviousText + 2 * horizontalPadding;
            mWidthNextText = mPaintText.measureText(mTextNextButton);
            final float nextButtonWidth = mWidthNextText + 2 * horizontalPadding;
            mRectPreviousText.set((int) (horizontalPadding + leftOffset), (int) mPaddingTop, (int) (previousButtonWidth + leftOffset), (int) mTextBottom);
            mRectNextText.set((int) (w - nextButtonWidth - rightOffset), (int) mPaddingTop, (int) (w - horizontalPadding - rightOffset), (int) mTextBottom);
            mRectPrevious.set((int)leftOffset, 0, (int) (leftOffset + previousButtonWidth), h);
            mRectNext.set((int) (w - nextButtonWidth - rightOffset), 0, (int)(w - rightOffset), h);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mShowChangePageButtons) {
            return false;
        }
        if ((mViewPager == null) || (mViewPager.getAdapter().getCount() == 0)) {
            return false;
        }
        final int action = event.getAction() & MotionEventCompat.ACTION_MASK;
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            // See if the pointer is within the bounds one of the buttons
            // In that case, change the state of the button to pressed and
            // repaint
            if (isEventOnRect(event, mRectPrevious)) {
                mPreviousDown = true;
                invalidate();
            }
            else if (isEventOnRect(event, mRectNext)) {
                mNextDown = true;
                invalidate();
            }
            else if (isEventOnRect(event, mRectStart)) {
                mStartDown = true;
                invalidate();
            }
            else if (isEventOnRect(event, mRectEnd)) {
                mEndDown = true;
                invalidate();
            }
            break;
        case MotionEvent.ACTION_MOVE:
            // See if we've exited the bounds of a pressed button before the
            // pointer was released
            // In that case, change the state of the button to normal and
            // repaint
            if (mPreviousDown) {
                if (!isEventOnRect(event, mRectPrevious)) {
                    mPreviousDown = false;
                    invalidate();
                }
            } else if (mNextDown) {
                if (!isEventOnRect(event, mRectNext)) {
                    mNextDown = false;
                    invalidate();
                }
            }
            else if (mStartDown) {
                if (!isEventOnRect(event, mRectStart)) {
                    mStartDown = false;
                    invalidate();
                }
            } else if (mEndDown) {
                if (!isEventOnRect(event, mRectEnd)) {
                    mEndDown = false;
                    invalidate();
                }
            }
            break;
        case MotionEvent.ACTION_UP:
            // See if we've released the pointer while still inside the pressed
            // button
            // In that case, perform the action associated to the button
            if (mPreviousDown) {
                openPreviousPage();
            } else if (mNextDown) {
                openNextPage();
            } else if (mStartDown) {
                openStartPage();
            } else if (mEndDown) {
                openEndPage();
            }
            // Deliberate fall-through
        case MotionEvent.ACTION_CANCEL:
            // After an UP or CANCEL action we change the state of the buttons
            // to normal and repaint
            if (mPreviousDown || mNextDown || mStartDown || mEndDown) {
                mStartDown = mEndDown = mNextDown = mPreviousDown = false;
                invalidate();
            }
            break;
        }
        return true;
    }

    private void openStartPage() {
        int count = mViewPager.getAdapter().getCount();
        if (count == 0) {
            Log.w(TAG, "Trying to open start page when page count is 0");
            return;
        }
        mViewPager.setCurrentItem(0, true);
    }

    private void openPreviousPage() {
        int currentPage = mViewPager.getCurrentItem();
        if (currentPage == 0) {
            Log.w(TAG, "Trying to open previous page when current page is 0");
            return;
        }
        mViewPager.setCurrentItem(currentPage - 1, true);
    }

    private void openNextPage() {
        int currentPage = mViewPager.getCurrentItem();
        if (currentPage == mViewPager.getAdapter().getCount() - 1) {
            Log.w(TAG, "Trying to open next page when current page is already the last one");
            return;
        }
        mViewPager.setCurrentItem(currentPage + 1, true);
    }
    
    private void openEndPage() {
        int count = mViewPager.getAdapter().getCount();
        if (count == 0) {
            Log.w(TAG, "Trying to open end page when page count is 0");
            return;
        }
        mViewPager.setCurrentItem(count - 1, true);
    }


    private boolean isEventOnRect(MotionEvent event, Rect rect) {
        return rect.contains((int) event.getX(), (int) event.getY());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mCurrentPage = savedState.currentPage;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPage = mCurrentPage;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int currentPage;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPage = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPage);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
