package com.gax.bubbleshoot.leveleditor;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gax.bubbleshoot.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Debug-only visual level editor. BuildConfig.DEBUG check + secret trigger
 * MenuFragment mein hai (release build users tak kabhi nahi pahunchega).
 * <p>
 * Grid tap karke design karo -> "Save Level File" dabao -> XML snippet
 * /Android/data/com.gax.bubbleshoot/files/levelN.txt mein save hoga.
 * Wo file open karke content copy karo, mujhe paste karo -> main use
 * data.xml mein sahi jagah daal dunga.
 */
public class LevelEditorActivity extends Activity {

    private static final int COLUMN = 11;

    private EditorCell[] mCells;
    private GridCellAdapter mAdapter;
    private RecyclerView mRecyclerGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_editor);

        mRecyclerGrid = findViewById(R.id.recycler_grid);
        mRecyclerGrid.setLayoutManager(new GridLayoutManager(this, COLUMN));
        mRecyclerGrid.setNestedScrollingEnabled(false);

        int initialRows = readInt(R.id.edit_rows, 10);
        buildGrid(initialRows);

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
        mAdapter = new GridCellAdapter(mCells);
        mRecyclerGrid.setAdapter(mAdapter);
    }

    private void saveLevelFile() {
        int levelNum = readInt(R.id.edit_level_num, 21);
        String levelType = readText(R.id.edit_level_type, "pop");
        int target = readInt(R.id.edit_target, 50);
        int moves = readInt(R.id.edit_moves, 40);

        // Bubble grid string (row by row, no separators - BubbleSystem splits
        // purely by TOTAL_COLUMN=11, so length MUST be a multiple of 11)
        StringBuilder bubbleFlat = new StringBuilder();
        for (EditorCell cell : mCells) {
            bubbleFlat.append(cell.code);
        }

        // Player queue: sirf un colors se banao jo grid mein actually maujood
        // hain, warna un-winnable shot mil sakta hai
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

        // Pretty-print grid rows (matches data.xml's existing indentation style)
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
}
