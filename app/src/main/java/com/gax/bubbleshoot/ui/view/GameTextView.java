package com.gax.bubbleshoot.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import com.gax.bubbleshoot.R;

/**
 * Kuch OEM skins (Vivo Funtouch OS, Xiaomi MIUI, Oppo ColorOS) apna khud ka
 * "System Font" Settings me force kar dete hain, jo XML ke android:fontFamily
 * ya app:fontFamily attribute ko silently ignore/override kar deta hai -> app
 * ka custom font load hi nahi hota, system font dikhta hai.
 * <p>
 * Typeface ko yahan CODE se directly {@link #setTypeface(Typeface)} karna is
 * OEM-level override ko bypass kar deta hai, kyunki wo sirf theme/resource
 * resolution path ko hook karte hain, runtime object assignment ko nahi.
 */
public class GameTextView extends AppCompatTextView {

    public GameTextView(Context context) {
        this(context, null);
    }

    public GameTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyGameFont();
    }

    private void applyGameFont() {
        if (isInEditMode()) {
            return;
        }
        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.baloo);
        if (typeface != null) {
            setTypeface(typeface);
        }
    }
}
