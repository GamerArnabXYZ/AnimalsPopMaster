package com.gax.bubbleshoot.util;

import android.view.View;
import android.view.ViewGroup;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * True edge-to-edge chahiye -> game ka background status/nav bar ke peeche
 * bhi full-bleed dikhna chahiye, sirf specific HUD elements (top score bar,
 * bottom booster/ad buttons) ko un bars ke peeche chhupne se bachana hai.
 * <p>
 * Isliye poore root container ko pad/margin nahi karte (usse background bhi
 * shrink ho jata), sirf targeted views ko hi status/nav bar ki height jitna
 * extra margin/padding dete hain.
 */
public final class InsetUtils {

    private InsetUtils() {
    }

    /** Fixed-height bar (apna background drawable hai) ko neeche shift karta hai. */
    public static void addTopMarginInset(View view) {
        if (view == null) return;
        int originalMargin = ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).topMargin;
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            lp.topMargin = originalMargin + top;
            v.setLayoutParams(lp);
            return insets;
        });
    }

    /** Bottom-anchored single view (jaise AdView ya ek button) ko upar shift karta hai. */
    public static void addBottomMarginInset(View view) {
        if (view == null) return;
        int originalMargin = ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).bottomMargin;
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            lp.bottomMargin = originalMargin + bottom;
            v.setLayoutParams(lp);
            return insets;
        });
    }

    /**
     * Full-screen wrapper container (jiska khud ka background nahi hai, sirf
     * children ko position karne ke liye hai) ke andar "parent bottom" se
     * anchored saare children ko ek hi call mein upar shift kar deta hai,
     * bottom PADDING ke through (ConstraintLayout children "parent" ki
     * padding respect karte hain).
     */
    public static void addBottomPaddingInset(View wrapperView) {
        if (wrapperView == null) return;
        int originalPadding = wrapperView.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(wrapperView, (v, insets) -> {
            int bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), originalPadding + bottom);
            return insets;
        });
    }
}
