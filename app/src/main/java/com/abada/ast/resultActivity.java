package com.abada.ast;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


@SuppressWarnings("ALL")
public class resultActivity extends AppCompatActivity {
    TableLayout tableLayout;
    FloatingActionButton fab;
    public CheckBox dontShowAgain;
    final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Pair<Integer, Integer> tag = (Pair<Integer, Integer>) v.getTag();
            int rowNumber = tag.first, colNumber = tag.second;
            int rowCount = tableLayout.getChildCount();
            if (rowNumber == 0) {
                for (int i = 0; i < rowCount; i++) {
                    TableRow row = (TableRow) tableLayout.getChildAt(i);
                    Log.i("", "onClick: " + row.getChildCount());
                    TextView tv = (TextView) row.getChildAt(colNumber);
                    if (tv.getBackground().getConstantState() == getres(R.drawable.default_cell_shape))
                        tv.setBackgroundResource(R.drawable.col_cell_shape);
                    else if ( tv.getBackground().getConstantState()==getres(R.drawable.col_cell_shape))
                        tv.setBackgroundResource(R.drawable.default_cell_shape);
                    else if( tv.getBackground().getConstantState()==getres(R.drawable.xy_cell_shape))
                        tv.setBackgroundResource(R.drawable.row_cell_shape);
                    else
                        tv.setBackgroundResource(R.drawable.xy_cell_shape);
                }
            } else {
                Log.i("", "onClick: row" + rowNumber + " " + rowCount);
                TableRow row = (TableRow) tableLayout.getChildAt(rowNumber);
                int rowLength = row.getChildCount();
                for (int i = 0; i < rowLength; i++) {
                    TextView tv = (TextView) row.getChildAt(i);
                    if (tv.getBackground().getConstantState() == getres(R.drawable.default_cell_shape))
                        tv.setBackgroundResource(R.drawable.row_cell_shape);
                    else if ( tv.getBackground().getConstantState()==getres(R.drawable.row_cell_shape))
                        tv.setBackgroundResource(R.drawable.default_cell_shape);
                    else if( tv.getBackground().getConstantState()==getres(R.drawable.xy_cell_shape))
                        tv.setBackgroundResource(R.drawable.col_cell_shape);
                    else
                        tv.setBackgroundResource(R.drawable.xy_cell_shape);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setTitle("Result");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        tableLayout = findViewById(R.id.result_table);

        Intent t = getIntent();
        String in = t.getStringExtra("input");
        final TruthTable truthTable = new TruthTable(in);
        ProgressDialog dialog = ProgressDialog.show(this, "Loading", "Wait while loading...");
        String out[][] = truthTable.getSchedule();
        for (int i = 0; i < out.length; i++)
            add(out[i], i);
        dialog.dismiss();
        fab = findViewById(R.id.fabsave);
        fab.setOnClickListener(v-> {
                ImgSaver s = new ImgSaver(resultActivity.this, tableLayout);
                s.doit();
        });

    }
    void add(String[] value, int i) {
        TableRow row = new TableRow(this);
        row.setClickable(true);
        if (i != 0)
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));
        for (int j = 0; j < value.length; j++) {
            TextView tv = new TextView(this);
            tv.setText(" " + value[j] + " ");
            tv.setTextAppearance(getApplicationContext(), R.style.output);
            tv.setBackgroundResource(R.drawable.default_cell_shape);
            tv.setGravity(Gravity.CENTER);
            tv.setOnClickListener(clickListener);
            tv.setTag(new Pair<Integer, Integer>(i, j));
            row.addView(tv);
        }
        tableLayout.addView(row, i);
    }
    private Drawable.ConstantState getres(int id){
       return getResources().getDrawable(id).getConstantState();
    }
}