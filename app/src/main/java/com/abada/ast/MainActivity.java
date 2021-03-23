package com.abada.ast;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getName();
    static String input = ""; // to save input when lose focus
    EditText textField;//input field
    Button clear,back,result;
    public static final String PREFS_NAME = "ASTPrefsFile1";
    static int count = 0;//to not "don'tshow again" button after closing result
    public CheckBox dontShowAgain;
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button b = (Button) v;
            autoAdd(b.getText().toString());
            Log.i("CLick", "onClick: " + b.getText().toString());
        }
    };
    View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            try {
                Button b = (Button) v;
                String out = "";
                switch (b.getId()) {
                    case R.id.and:
                        out = "AND";
                        break;
                    case R.id.or:
                        out = "OR";
                        break;
                    case R.id.not:
                        out = "NOT";
                        break;
                    case R.id.one_side:
                        out = "ONE SIDE";
                        break;
                    case R.id.two_side:
                        out = "TWO SIDE";
                        break;
                    case R.id.xor:
                        out = "XOR";
                        break;
                    case R.id.back:
                        out = "Remove the last character";
                        break;
                    case R.id.clear:
                        out = "Remove all";
                        break;
                    default:
                        out = "What!!";
                }
                Toast.makeText(MainActivity.this, out, Toast.LENGTH_SHORT).show();
            }catch (Exception e){}
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        myCheckPermission();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //input editText settings


        ConstraintLayout layout = findViewById(R.id.main_layout);
        int layoutLength = layout.getChildCount();
        for (int i = 0; i < layoutLength; i++) {
            View v = layout.getChildAt(i);
            if(v.getClass().getName().equals("androidx.appcompat.widget.AppCompatEditText"))
                continue;
            v.setOnClickListener(clickListener);
            v.setOnLongClickListener(longClickListener);
        }
        textField = findViewById(R.id.input);
        textField.setMaxLines(2);
        if (!input.equals(""))
            textField.setText(input);

         result = findViewById(R.id.result);
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textField.getText().toString().isEmpty()) {
                    return;
                }
                while (
                        textField.getText().length() > 0 && (textField.getText().charAt(textField.getText().length() - 1) == '(' ||
                                TruthTable.OPS.contains(textField.getText().charAt(textField.getText().length() - 1) + "")
                        )) {
                    textField.setText(textField.getText().toString().substring(0, textField.getText().length() - 1));
                }
                while (!check(textField.getText().toString())) {
                    autoAdd(")");
                }
                if (textField.getText().toString().isEmpty()) {
                    AlertDialog.Builder adp = new AlertDialog.Builder(MainActivity.this);
                    adp.setMessage(Html.fromHtml("<html><h1>error</h1><br><h2><strong>Don't laugh on me I catched it *_-</strong></h2>-_-</html> "));
                    return;
                }

                Intent intent = new Intent(MainActivity.this, resultActivity.class);
                intent.putExtra("input", textField.getText().toString());
                startActivity(intent);

            }
        });

        Button clear = findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textField.setText("");
            }
        });
        Button back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String out = textField.getText().toString();
                if (out.length() == 0)
                    return;
                out = out.substring(0, out.length() - 1);
                textField.setText(out);
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        input = textField.getText().toString();
    }

    @Override
    protected void onResume() {
        if (count > 0) {
            super.onResume();
            return;
        }
        count++;
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        LayoutInflater adbInflater = LayoutInflater.from(this);
        View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String skipMessage = settings.getString("skipMessage", "NOT checked");

        dontShowAgain = (CheckBox) eulaLayout.findViewById(R.id.skip);
        adb.setView(eulaLayout);
        adb.setTitle("How to write parentheses?");
        adb.setMessage("click volume up to put ( \n    " +
                "volume down to put )");

        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String checkBoxResult = "NOT checked";

                if (dontShowAgain.isChecked()) {
                    checkBoxResult = "checked";
                }

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("skipMessage", checkBoxResult);
                editor.commit();
                return;
            }
        });

        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String checkBoxResult = "NOT checked";
                if (dontShowAgain.isChecked()) {
                    checkBoxResult = "checked";
                }
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("skipMessage", checkBoxResult);
                editor.commit();
                return;
            }
        });

        if (!skipMessage.equals("checked")) {
            adb.show();
        }
        super.onResume();
    }

    private boolean autoAdd(String button) {
        if (textField.getText().toString().isEmpty() || textField.getText().equals("the case please")) {
            if (button.equals("¬") || button.equals("(") || button.matches("[a-z]")) {
                textField.setText(button);
                return true;
            }
        } else {
            String l = textField.getText().charAt(textField.getText().length() - 1) + "";
            if (button.equals("¬") && ("(" + TruthTable.OPS.substring(1)).contains(l)
                    || TruthTable.OPS.substring(1).contains(button) && !TruthTable.OPS.contains(l) && !l.equals("(")
                    || button.equals("(") && ("(" + TruthTable.OPS).contains(l)
                    || button.equals(")") && !("(" + TruthTable.OPS).contains(l) && !check(textField.getText().toString())
                    || button.matches("[a-z]") && ("(" + TruthTable.OPS).contains(l)) {
                textField.setText(textField.getText() + button);
                return true;
            }
        }
        return false;
    }

    private boolean check(String in) {
        int a = 0;
        for (int i = 0; i < in.length(); i++) {
            if (in.charAt(i) == '(') {
                a++;
            } else if (in.charAt(i) == ')') {
                a--;
            }
        }
        if (a == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    autoAdd("(");
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    autoAdd(")");
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    void myCheckPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }
}
