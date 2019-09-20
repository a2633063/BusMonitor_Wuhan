package com.zyc.busmonitor;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zyc.buslist.BusList;
import com.zyc.buslist.BusStation;

import java.util.ArrayList;
import java.util.List;


public class BusFragment extends Fragment {

    private List<BusStation> mDataList = new ArrayList<>();

    public BusFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bus, container, false);


        mDataList.clear();
        BusStation b;
        b = new BusStation("高");
        mDataList.add(b);
        b = new BusStation("高新");
        mDataList.add(b);
        b = new BusStation("高新六");
        mDataList.add(b);
        b = new BusStation("高新六路");
        mDataList.add(b);
        b = new BusStation("高新六路流");
        mDataList.add(b);
        b = new BusStation("高新六路流芳");
        mDataList.add(b);
        b = new BusStation("高新六路流芳大");
        mDataList.add(b);
        b = new BusStation("高新六路流芳大道");
        mDataList.add(b);
        b = new BusStation("高新六路AVV流芳大道高");
        mDataList.add(b);
        b = new BusStation("高新六A路流芳大道高高");
        mDataList.add(b);
        b = new BusStation("高新六AV路流芳大道高高高");
        mDataList.add(b);
        b = new BusStation("高新六路汪田村");
        mDataList.add(b);
        b = new BusStation("高新六路康一路");
        mDataList.add(b);
        b = new BusStation("高新六路佛祖岭一路");
        mDataList.add(b);
        b = new BusStation("高新六路光谷三路");
        mDataList.add(b);
        b = new BusStation("光谷三路高新四路");
        mDataList.add(b);
        b = new BusStation("光谷三路大吕村");
        mDataList.add(b);
        b = new BusStation("高新二路大吕路");
        mDataList.add(b);
        b = new BusStation("高新二路光谷四路");
        mDataList.add(b);
        b = new BusStation("高新二路驿山南路");
        mDataList.add(b);
        b = new BusStation("高新二路光谷六路");
        mDataList.add(b);
        b = new BusStation("高新二路高科园路");
        b.setPass(1);
        mDataList.add(b);
        b = new BusStation("高新二路高科园二路");
        mDataList.add(b);
        b = new BusStation("高新二路光谷七路");
        mDataList.add(b);
        b = new BusStation("高新二路生物园路");
        mDataList.add(b);
        b = new BusStation("光谷八路蔡吴村");
        b.setArrive(1);
        mDataList.add(b);
        b = new BusStation("桥北路教师小区");
        b.setPass(2);
        mDataList.add(b);
        b = new BusStation("桥北路三眼桥");
        mDataList.add(b);
        b = new BusStation("豹澥公交停车场");
        mDataList.add(b);
        BusList busList =view.findViewById(R.id.busList);
        busList.setDataList(mDataList);
        busList.notifyDataSetChanged();

        return view;
    }



}
