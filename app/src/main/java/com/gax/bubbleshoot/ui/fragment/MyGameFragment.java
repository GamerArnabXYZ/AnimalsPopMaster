package com.gax.bubbleshoot.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.gax.bubbleshoot.BuildConfig;
import com.gax.bubbleshoot.R;
import com.gax.bubbleshoot.game.MyGame;
import com.gax.bubbleshoot.leveleditor.LevelEditorActivity;
import com.gax.bubbleshoot.sound.MySoundEvent;
import com.gax.bubbleshoot.ui.UIEffect;
import com.gax.bubbleshoot.util.InsetUtils;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.GameView;
import com.nativegame.nattyengine.ui.GameFragment;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class MyGameFragment extends GameFragment implements View.OnClickListener {

    private static final String LEVEL = "level";

    private Game mGame;
    private int mLevel;

    public MyGameFragment() {
        // Required empty public constructor
    }

    public static MyGameFragment newInstance(int level) {
        MyGameFragment fragment = new MyGameFragment();
        Bundle args = new Bundle();
        args.putInt(LEVEL, level);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLevel = getArguments().getInt(LEVEL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    protected void onLayoutCreated() {
        // Edge-to-edge: background full-bleed rehta hai, sirf HUD bar (status
        // bar), game_view (HUD ke neeche), aur booster buttons (nav bar) ko
        // clear rakhte hain.
        InsetUtils.addTopMarginInset(getView().findViewById(R.id.layout_state));
        InsetUtils.addTopMarginInset(getView().findViewById(R.id.game_view));
        InsetUtils.addBottomPaddingInset(getView().findViewById(R.id.layout_booster));
        InsetUtils.addTopMarginInset(getView().findViewById(R.id.btn_edit_level));

        // Debug-only: is level ko edit karne ke liye pencil icon
        ImageButton btnEditLevel = getView().findViewById(R.id.btn_edit_level);
        if (BuildConfig.DEBUG) {
            btnEditLevel.setVisibility(View.VISIBLE);
            btnEditLevel.setOnClickListener(v ->
                    LevelEditorActivity.startEdit(getGameActivity(), mLevel));
        }

        // Init pause button
        ImageButton btnPause = (ImageButton) getGameActivity().findViewById(R.id.btn_pause);
        UIEffect.createButtonEffect(btnPause);
        btnPause.setOnClickListener(this);

        // Start game
        mGame = new MyGame(getGameActivity(), (GameView) getView().findViewById(R.id.game_view), mLevel);
        mGame.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGame.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGame.stop();
    }

    @Override
    public boolean onBackPressed() {
        mGame.pause();
        return true;
    }

    @Override
    public void onClick(View view) {
        getGameActivity().getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
        mGame.pause();
    }

}
