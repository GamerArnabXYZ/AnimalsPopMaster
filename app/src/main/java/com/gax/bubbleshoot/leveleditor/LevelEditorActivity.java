package com.gax.bubbleshoot.leveleditor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gax.bubbleshoot.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Debug-only visual level editor. BuildConfig.DEBUG check MenuFragment,
 * MapFragment aur MyGameFragment mein hai (release build users tak kabhi
 * nahi pahunchega).
 * <p>
 * Do modes:
 * - CREATE: Map screen ke "+" button se, blank grid, naya level number
 * - EDIT: Gameplay screen ke pencil icon se, us waqt chal rahe level ka
 *   data.xml se maujooda grid load karke pre-fill karta hai
 * <p>
 * "Save Level File" -> XML snippet /Android/data/com.gax.bubbleshoot/files/
 * mein save hota hai. Wo file open karke content copy karo, paste karo ->
 * main data.xml mein sahi jagah daal dunga.
 */
public class LevelEditorActivity extends Activity {

    public static final String EXTRA_MODE = "extra_mode";
    public static final String EXTRA_LEVEL_NUM = "extra_level_num";
    public static final String MODE_CREATE = "create";
    public static final String MODE_EDIT = "edit";

    private static final int COLUMN = 11;

    private EditorCell[] mCells;
    private HexGridLayout mHexGrid;

    /** Map screen ke "+" button se call karo -> blank naya level. */
    public static void startCreate(Context context, int suggestedLevelNum) {
        Intent intent = new Intent(context, LevelEditorActivity.class);
        intent.putExtra(EXTRA_MODE, MODE_CREATE);
        intent.putExtra(EXTRA_LEVEL_NUM, suggestedLevelNum);
        context.startActivity(intent);
    }

    /** Gameplay ke pencil icon se call karo -> current level pre-loaded milega. */
    public static void startEdit(Context context, int levelNum) {
        Intent intent = new Intent(context, LevelEditorActivity.class);
        intent.putExtra(EXTRA_MODE, MODE_EDIT);
        intent.putExtra(EXTRA_LEVEL_NUM, levelNum);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_editor);

        mHexGrid = findViewById(R.id.hex_grid);

        String mode = getIntent().getStringExtra(EXTRA_MODE);
        int levelNum = getIntent().getIntExtra(EXTRA_LEVEL_NUM, 21);
        ((EditText) findViewById(R.id.edit_level_num)).setText(String.valueOf(levelNum));

        if (MODE_EDIT.equals(mode)) {
            LevelData existing = readLevelFromAssets(levelNum);
            if (existing != null) {
                populateFrom(existing);
            } else {
                Toast.makeText(this, "Level " + levelNum + " data.xml mein nahi mila, blank grid.", Toast.LENGTH_LONG).show();
                buildGrid(readInt(R.id.edit_rows, 10));
            }
        } else {
            buildGrid(readInt(R.id.edit_rows, 10));
        }

        findViewById(R.id.btn_generate_grid).setOnClickListener(v -> {
            int rows = readInt(R.id.edit_rows, 10);
            buildGrid(rows);
        });

        findViewById(R.id.btn_save_level).setOnClickListener(v -> saveLevelFile());
    }

    private void buildGrid(int rows) {
        if (rows < 1) rows = 1;
        mCells = new EditorCell[rows * COLUMN];
        for (int i = 0; i < mCells.length; i++) {
            mCells[i] = EditorCell.EMPTY;
        }
        mHexGrid.rebuild(mCells, () -> { });
    }

    private void populateFrom(LevelData data) {
        ((EditText) findViewById(R.id.edit_level_type)).setText(data.levelType);
        ((EditText) findViewById(R.id.edit_target)).setText(String.valueOf(data.target));
        ((EditText) findViewById(R.id.edit_moves)).setText(String.valueOf(data.player.length()));

        int rows = data.bubble.length() / COLUMN;
        ((EditText) findViewById(R.id.edit_rows)).setText(String.valueOf(rows));

        mCells = new EditorCell[data.bubble.length()];
        for (int i = 0; i < mCells.length; i++) {
            mCells[i] = EditorCell.fromCode(data.bubble.charAt(i));
        }
        mHexGrid.rebuild(mCells, () -> { });
    }

    /** data.xml (assets) se ek level ka raw data nikalta hai - simple regex based, XML format khud predictable hai. */
    private LevelData readLevelFromAssets(int levelNum) {
        try (InputStream is = getAssets().open("data.xml")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append('\n');
            String content = sb.toString();

            String tag = "level" + levelNum;
            Matcher blockMatcher = Pattern.compile("<" + tag + ">(.*?)</" + tag + ">", Pattern.DOTALL).matcher(content);
            if (!blockMatcher.find()) return null;
            String block = blockMatcher.group(1);

            String levelType = extractTag(block, "level_type", "pop");
            int target = Integer.parseInt(extractTag(block, "target", "50"));
            String player = extractTag(block, "player", "r").trim();
            String bubbleRaw = extractTag(block, "bubble", "");
            String bubble = bubbleRaw.replaceAll("[^0-9a-zA-Z+]", "");

            LevelData data = new LevelData();
            data.levelType = levelType;
            data.target = target;
            data.player = player;
            data.bubble = bubble;
            return data;
        } catch (IOException e) {
            return null;
        }
    }

    private String extractTag(String block, String tagName, String fallback) {
        Matcher m = Pattern.compile("<" + tagName + ">(.*?)</" + tagName + ">", Pattern.DOTALL).matcher(block);
        return m.find() ? m.group(1) : fallback;
    }

    private void saveLevelFile() {
        int levelNum = readInt(R.id.edit_level_num, 21);
        String levelType = readText(R.id.edit_level_type, "pop");
        int target = readInt(R.id.edit_target, 50);
        int moves = readInt(R.id.edit_moves, 40);

        StringBuilder bubbleFlat = new StringBuilder();
        for (EditorCell cell : mCells) {
            bubbleFlat.append(cell.code);
        }

        List<Character> availableColors = new ArrayList<>();
        String flat = bubbleFlat.toString();
        for (char colorChar : new char[]{'r', 'y', 'b', 'g'}) {
            if (flat.indexOf(colorChar) >= 0 || flat.indexOf(Character.toUpperCase(colorChar)) >= 0) {
                availableColors.add(colorChar);
            }
        }
        if (availableColors.isEmpty()) {
            availableColors.add('r');
        }
        Random random = new Random();
        StringBuilder player = new StringBuilder();
        for (int i = 0; i < moves; i++) {
            player.append(availableColors.get(random.nextInt(availableColors.size())));
        }

        StringBuilder gridPretty = new StringBuilder();
        int totalRows = mCells.length / COLUMN;
        for (int r = 0; r < totalRows; r++) {
            gridPretty.append("            ");
            for (int c = 0; c < COLUMN; c++) {
                gridPretty.append(mCells[r * COLUMN + c].code);
            }
            gridPretty.append("\n");
        }

        String xml = String.format(Locale.US,
                "    <level%d>\n" +
                        "        <level_type>%s</level_type>\n" +
                        "        <target>%d</target>\n" +
                        "        <player>%s</player>\n" +
                        "        <bubble>\n" +
                        "%s" +
                        "        </bubble>\n" +
                        "    </level%d>",
                levelNum, levelType, target, player, gridPretty, levelNum);

        File outFile = new File(getExternalFilesDir(null), "level" + levelNum + ".txt");
        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            fos.write(xml.getBytes());
            String msg = "Saved: " + outFile.getAbsolutePath();
            ((TextView) findViewById(R.id.txt_save_result)).setText(msg);
            Toast.makeText(this, "Level saved! Copy the file's content and paste it in chat.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            ((TextView) findViewById(R.id.txt_save_result)).setText("Save failed: " + e.getMessage());
        }
    }

    private int readInt(int viewId, int fallback) {
        try {
            EditText edit = findViewById(viewId);
            return Integer.parseInt(edit.getText().toString().trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    private String readText(int viewId, String fallback) {
        EditText edit = findViewById(viewId);
        String text = edit.getText().toString().trim();
        return text.isEmpty() ? fallback : text;
    }

    private static class LevelData {
        String levelType;
        int target;
        String player;
        String bubble;
    }
}
