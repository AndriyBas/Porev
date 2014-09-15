package com.oyster.lab01.ui.fragment;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.oyster.lab01.R;
import com.oyster.lab01.utils.Experiment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    @InjectView(R.id.frag_mf_action_button)
    Button mActionButton;

    @InjectView(R.id.textView0)
    EditText ETa0;

    @InjectView(R.id.textView1)
    EditText ETa1;

    @InjectView(R.id.textView2)
    EditText ETa2;

    @InjectView(R.id.textView3)
    EditText ETa3;

    @InjectView(R.id.frag_mf_table_layout)
    TableLayout mTableLayout;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean enabled = ETa0.getText().toString().trim().length() *
                    ETa1.getText().toString().trim().length() *
                    ETa2.getText().toString().trim().length() *
                    ETa3.getText().toString().trim().length() > 0;
            mActionButton.setEnabled(enabled);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_mf_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ETa0.addTextChangedListener(mTextWatcher);
        ETa1.addTextChangedListener(mTextWatcher);
        ETa2.addTextChangedListener(mTextWatcher);
        ETa3.addTextChangedListener(mTextWatcher);
    }

    @OnClick(R.id.frag_mf_action_button)
    void onActionBtnClick() {

        double a0 = Double.parseDouble(ETa0.getText().toString().trim());
        double a1 = Double.parseDouble(ETa1.getText().toString().trim());
        double a2 = Double.parseDouble(ETa2.getText().toString().trim());
        double a3 = Double.parseDouble(ETa3.getText().toString().trim());

        Experiment exp = new Experiment(a0, a1, a2, a3);

        fillTable(exp);

        Toast.makeText(getActivity(), "generate", Toast.LENGTH_SHORT)
                .show();
    }

    private TextView tv(String str, int color) {
        TextView x = new TextView(getActivity());
        x.setText(str);
        x.setBackgroundResource(color);
        return x;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void fillTable(Experiment exp) {

        mTableLayout.removeAllViews();

        Context context = getActivity();
        double[][] x = exp.table;

        TableRow columnRow = new TableRow(context);
        columnRow.addView(tv("  ", android.R.color.transparent));
        for (int i = 1; i <= 3; i++) {
            columnRow.addView(tv("X " + (i), android.R.color.holo_orange_light));
        }

        columnRow.addView(tv("Y", android.R.color.holo_orange_light));
        for (int i = 1; i <= 3; i++) {
            columnRow.addView(tv("XH " + (i), android.R.color.holo_orange_light));
        }

        mTableLayout.addView(columnRow, 0);

        for (int i = 0; i < x.length; i++) {
            TableRow row = new TableRow(context);
            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            String m = "    " + (i + 1) + " :";
            if (i == 8) {
                m = "    x0 :";
            } else if (i == 9) {
                m = "    dx :";
            }
            row.addView(tv(m, i % 2 == 1 ? android.R.color.white : android.R.color.darker_gray));

            for (int j = 0; j < x[i].length; j++) {
                String msg = (x[i][j] == Double.MIN_VALUE ? " " : (String.format("%.2f", x[i][j])));
                int color = i % 2 == 0 ? android.R.color.holo_blue_dark : android.R.color.holo_blue_light;
                if (exp.yFunRow == i) {
                    color = android.R.color.holo_red_light;
                }
                row.addView(tv(msg, color));
            }

            mTableLayout.addView(row, i + 1);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
