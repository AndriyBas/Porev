package com.oyster.lab02.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.oyster.lab02.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andriybas on 10/12/14.
 */
public class EtAdapter extends ArrayAdapter<Integer> {

    final LayoutInflater mInflater;
    List<List<Double>> x;


    public EtAdapter(Context context, List<Integer> objects) {
        super(context, 0, objects);
        mInflater = LayoutInflater.from(context);

        x = new ArrayList<>(objects.size());

        for (int i = 0; i < objects.size(); i++) {
            List<Double> a = new ArrayList<>(2);
            a.add(25.0);
            a.add(75.0);
            x.add(a);
        }
    }

    public List<Pair<Double, Double>> getX() {

        List<Pair<Double, Double>> xMinMax = new ArrayList<>();

        for (List<Double> l : x) {
            xMinMax.add(new Pair<Double, Double>(l.get(0), l.get(1)));
        }

        return xMinMax;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // ATTENTION : do not reuse this peace of shit
//        if (convertView == null) {
        convertView = mInflater.inflate(R.layout.item, parent, false);
//        }

        int p = position + 1;
        EditText etMin = (EditText) convertView.findViewById(R.id.et_min);

        // horrible peace of shit
        etMin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double val = 0.0;
                try {
                    val = Double.parseDouble(s.toString().trim());
                } catch (Exception e) {
                }

                x.get(position).set(0, val);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        EditText etMax = (EditText) convertView.findViewById(R.id.et_max);
        etMax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double val = 0.0;
                try {
                    val = Double.parseDouble(s.toString().trim());
                } catch (Exception e) {
                }

                x.get(position).set(1, val);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        TextView tvMin = (TextView) convertView.findViewById(R.id.tv_min);
        TextView tvMax = (TextView) convertView.findViewById(R.id.tv_max);
        tvMin.setText("x" + String.valueOf(p) + " min");
        tvMax.setText("x" + String.valueOf(p) + " max");

        return convertView;
    }
}
