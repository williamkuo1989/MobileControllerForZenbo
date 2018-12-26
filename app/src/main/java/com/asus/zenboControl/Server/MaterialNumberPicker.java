package com.asus.zenboControl.Server;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.lang.reflect.Field;

public class MaterialNumberPicker extends NumberPicker {

    private static final float TEXT_SIZE = 50f;
    private float mTextSize;
    private boolean mEnableFocusability;

    public MaterialNumberPicker(Context context) {
        super(context);
        initView();
    }

    public MaterialNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public boolean isFocusabilityEnabled() {
        return mEnableFocusability;
    }

    /**
     * Init number picker by disabling focusability of edit text embedded inside the number picker
     * We also override the edit text filter private attribute by using reflection as the formatter is still buggy while attempting to display the default value
     * This is still an open Google @see <a href="https://code.google.com/p/android/issues/detail?id=35482#c9">issue</a> from 2012
     */
    private void initView() {

        setTextSize(TEXT_SIZE);

        try {
            Field f = NumberPicker.class.getDeclaredField("mInputText");
            f.setAccessible(true);
            EditText inputText = (EditText) f.get(this);
            inputText.setFilters(new InputFilter[0]);
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses reflection to access divider private attribute and override its color
     * Use Color.Transparent if you wish to hide them
     */
    public void setSeparatorColor(int separatorColor) {
//        mSeparatorColor = separatorColor;

        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    pf.set(this, new ColorDrawable(separatorColor));
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void setSelectionDividersDistance(float distance) {

        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDividersDistance")) {
                pf.setAccessible(true);
                try {
                    pf.set(this, (int) distance);
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * Uses reflection to access text color private attribute for both wheel and edit text inside the number picker.
     */
    public void setTextColor(int textColor) {
//        mTextColor = textColor;
        updateTextAttributes();
    }

    /**
     * Uses reflection to access text size private attribute for both wheel and edit text inside the number picker.
     */
    public void setTextSize(float textSize) {
        if (textSize > 0) {
            mTextSize = textSize;
//        setSelectionDividersDistance(textSize);
            updateTextAttributes();
        }
    }

    private void updateTextAttributes() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = NumberPicker.class.getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);

                    Paint wheelPaint = ((Paint) selectorWheelPaintField.get(this));
//                    wheelPaint.setColor(mTextColor);
                    if (mTextSize > 0)
                        wheelPaint.setTextSize(mTextSize);

                    EditText editText = ((EditText) child);
//                    editText.setTextColor(mTextColor);
                    if (mTextSize > 0)
                        editText.setTextSize(pixelsToSp(getContext(), mTextSize));

                    invalidate();
                    break;
                } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setFocusability(boolean isFocusable) {
        mEnableFocusability = isFocusable;
        setDescendantFocusability(isFocusable ? FOCUS_AFTER_DESCENDANTS : FOCUS_BLOCK_DESCENDANTS);
    }

    private float pixelsToSp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().scaledDensity;
    }

    private float spToPixels(Context context, float sp) {
        return sp * context.getResources().getDisplayMetrics().scaledDensity;
    }

}