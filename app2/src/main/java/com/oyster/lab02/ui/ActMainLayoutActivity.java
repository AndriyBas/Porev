package com.oyster.lab02.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.oyster.lab02.R;
import com.oyster.lab02.utils.Experiment;

import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class ActMainLayoutActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main_layout);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.frag_container, new PlaceholderFragment())
                    .commit();
        }

//        List<Pair<Double, Double>> x = new ArrayList<>();
//        x.add(new Pair<Double, Double>(25.0, 75.0));
//        x.add(new Pair<Double, Double>(5.0, 40.0));
//
//        Experiment ex = new Experiment(x);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        @InjectView(R.id.spinner)
        Spinner mSpinner;

        @InjectView(R.id.list)
        ListView mList;

        @InjectView(R.id.frag_mf_table_layout)
        TableLayout mTableLayout;

        @InjectView(R.id.eq_one)
        TextView mEqOne;

        @InjectView(R.id.eq_two)
        TextView mEqTwo;

        @InjectView(R.id.fisher)
        TextView mFisher;

        boolean DEBUG_MODE = true;


        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.frag_main_layout, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            ButterKnife.inject(this, view);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            ButterKnife.reset(this);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, Arrays.asList(2, 3, 4, 5, 6, 7, 8));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinner.setAdapter(adapter);

            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    setUpAdapter(position + 2);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            setUpAdapter(2);
        }

        void setUpAdapter(int n) {
            List<Integer> list = new ArrayList<>(n);
            for (int i = 0; i < n; i++)
                list.add(1);

            EtAdapter adapter = new EtAdapter(getActivity(), list);

            mList.setAdapter(adapter);
        }

        @OnClick(R.id.frag_mf_action_button)
        void onActionClick() {
            try {

                EtAdapter adapter = (EtAdapter) mList.getAdapter();

                List<Pair<Double, Double>> xMinMax = adapter.getX();

                Experiment experiment = new Experiment(xMinMax);
                showResults(experiment);

            } catch (Exception ex) {
                Toast.makeText(getActivity(), "WTF ??? ", Toast.LENGTH_SHORT)
                        .show();
                ex.printStackTrace();
            }
        }

        private TextView tv(String str, int color) {
            TextView x = new TextView(getActivity());
            x.setText(str);
            x.setBackgroundResource(color);
            return x;
        }

        void showResults(Experiment ex) {
            mTableLayout.removeAllViews();

            List<List<Double>> x = ex.getX();
            List<List<Double>> y = ex.getY();


            Context context = getActivity();

            TableRow columnRow = new TableRow(context);
            columnRow.addView(tv("  ", android.R.color.transparent));
            for (int i = 1; i <= ex.getK(); i++) {
                columnRow.addView(tv("X " + (i), android.R.color.holo_orange_light));
            }

            for (int i = 1; i <= y.size(); i++) {
                columnRow.addView(tv("Y " + (i), android.R.color.holo_green_light));
            }


            mTableLayout.addView(columnRow, 0);

            for (int i = 0; i < x.size(); i++) {
                TableRow row = new TableRow(context);
                row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                String m = "    " + (i + 1) + " :";

                row.addView(tv(m, i % 2 == 1 ? android.R.color.white : android.R.color.darker_gray));

                for (int j = 0; j < x.get(i).size(); j++) {
                    double val = x.get(i).get(j);
                    String msg = (val == Double.MIN_VALUE ? " " : (String.format("%.2f", val)));
                    int color = i % 2 == 0 ? android.R.color.holo_blue_dark : android.R.color.holo_blue_light;
                    row.addView(tv(msg, color));
                }

                for (int j = 0; j < y.size(); j++) {
                    double val = y.get(j).get(i);
                    String msg = (val == Double.MIN_VALUE ? " " : (String.format("%.2f", val)));
                    int color = i % 2 == 0 ? android.R.color.holo_blue_dark : android.R.color.holo_blue_light;
                    row.addView(tv(msg, color));
                }

//                for (int j = 0; j < x[i].length; j++) {
//                    String msg = (x[i][j] == Double.MIN_VALUE ? " " : (String.format("%.2f", x[i][j])));
//                    int color = i % 2 == 0 ? android.R.color.holo_blue_dark : android.R.color.holo_blue_light;
//                    if (exp.yFunRow == i) {
//                        color = android.R.color.holo_red_light;
//                    }
//                    row.addView(tv(msg, color));
//                }

                mTableLayout.addView(row, i + 1);
            }

            SimpleMatrix sm = ex.getNormCoefficients();
            StringBuilder oneRes = new StringBuilder();
            oneRes.append(String.format("%.3f", sm.get(0, 0)));
            for (int i = 1; i < sm.numRows(); i++) {
                oneRes.append(String.format("  +  %.3f * x%d", sm.get(i, 0), i));
            }
            mEqOne.setText(oneRes.toString());


            SimpleMatrix nsm = ex.getNaturalCoefficients();
            StringBuilder twoRes = new StringBuilder();
            twoRes.append(String.format("%.3f", nsm.get(0, 0)));
            for (int i = 1; i < nsm.numRows(); i++) {
                twoRes.append(String.format("  +  %.3f * x%d", nsm.get(i, 0), i));
            }
            mEqTwo.setText(twoRes.toString());

            mFisher.setText("" + ex.fisher());



        }


    }
}
