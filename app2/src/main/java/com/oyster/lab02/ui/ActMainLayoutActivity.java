package com.oyster.lab02.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.oyster.lab02.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


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


    }
}
