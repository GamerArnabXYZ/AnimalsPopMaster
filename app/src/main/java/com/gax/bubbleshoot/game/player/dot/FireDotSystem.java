package com.gax.bubbleshoot.game.player.dot;

import com.gax.bubbleshoot.R;
import com.gax.bubbleshoot.game.player.booster.FireBubble;
import com.nativegame.nattyengine.Game;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class FireDotSystem extends DotSystem {

    public FireDotSystem(FireBubble fireBubble, Game game) {
        super(fireBubble, game);
        setDotBitmap(R.drawable.dot_fire);
    }

    @Override
    protected void setDotPosition(Dot dot, float x, float y) {
        if (x <= dot.mMinX || x >= dot.mMaxX) {
            // We do not need reflection in fire dot
            dot.setPosition(-dot.mWidth, -dot.mHeight);
        } else {
            dot.setPosition(x, y);
        }
    }

}
