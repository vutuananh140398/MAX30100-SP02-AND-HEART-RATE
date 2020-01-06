package com.jmctvs.dieukhienxev2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.List;

public class TabFragment3 extends Fragment implements OnChartValueSelectedListener {

    private View frag_view;
    public static Handler mHandler;

    private Button upBtn;
    private Button clearBtn;
    private Button updateChartBtn;
//    public static int indexE = 1;
    static ArrayList<Integer> data = new ArrayList<>();

    private static int maxPoint = 150;
    private static int numSample = 0;
    private String tmpStr = "S45T45T45T0A";
    private TextView heartRate;
    private TextView spO2;
    private String[] sData;
    String readMessage = null;

    private static CombinedChart mChart;

    boolean dialogIsShowing = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        frag_view = inflater.inflate(R.layout.tab_fragment_3, container, false);

        updateChartBtn = frag_view.findViewById(R.id.btn_background);
        clearBtn = frag_view.findViewById(R.id.btn_clear);
        upBtn = frag_view.findViewById(R.id.fr3Button);

//        clearBtn.setEnabled(false);
//        upBtn.setEnabled(false);
//        updateChartBtn.setEnabled(false);

        mChart = frag_view.findViewById(R.id.combinedChart);
        mChart.getDescription().setEnabled(false);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);
        mChart.setOnChartValueSelectedListener(this);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setEnabled(false);
        rightAxis.setAxisMinimum(45f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(20f);

        final List<String> xLabel = new ArrayList<>();
        for (short i = 0; i < maxPoint ; i++)
        {
            xLabel.add(i + "");
        }
//        xLabel.add("Jan");
//        xLabel.add("Feb");
//        xLabel.add("Mar");
//        xLabel.add("Apr");
//        xLabel.add("May");
//        xLabel.add("Jun");
//        xLabel.add("Jul");
//        xLabel.add("Aug");
//        xLabel.add("Sep");
//        xLabel.add("Oct");
//        xLabel.add("Nov");
//        xLabel.add("Dec");

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter()
        {
            @Override
            public String getFormattedValue(float value, AxisBase axis)
            {
                if (value < 0) return "0";
                return xLabel.get((int) value);
            }
        }
        );

        CombinedData data = new CombinedData();
        LineData lineDatas = new LineData();
        Log.d("test", "11111111111111");
//        lineDatas.addDataSet((ILineDataSet) dataChart(2));
        lineDatas.addDataSet( dataChart(2));


        Log.d("test", "22222222222222");
        data.setData(lineDatas);
        Log.d("test", "3333333333333");
        Log.d("test", lineDatas.getDataSetByIndex(0) + "");

//        xAxis.setAxisMaximum(data.getXMax() + 0.25f);
        mChart.setData(data);
        Log.d("test", "4444444444444444");
        mChart.invalidate();
        Log.d("test", "555555555555555555");
//        setupChart();
        return frag_view;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
//        Toast.makeText(this, "Value: "
//                + e.getY()
//                + ", index: "
//                + h.getX()
//                + ", DataSet index: "
//                + h.getDataSetIndex(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {
    }

    private static LineDataSet dataChart(int indexR) {
        LineData lineData = new LineData();
        Log.d("length","data size " + data.size() +"");
        if(data.size() >= maxPoint)
//        if (numSample >= maxPoint)
        {
            Log.d("length","data size " + data.size() +"");
            data.clear();
            mChart.clear();
            mChart.invalidate();

//            indexE = 1;
            numSample = 0;
        }
        data.add(indexR);

        ArrayList<Entry> entries = new ArrayList();
        for (int index = 0; index < data.size(); index++) {
//            Log.d("test", data.size() +"");
            entries.add(new Entry(index, data.get(index)));
        }

        LineDataSet set = new LineDataSet(entries, "BPM chart");
        set.setColor(Color.GREEN);
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.RED);
        set.setCircleRadius(2f);
        set.setFillColor(Color.GREEN);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        set.setDrawValues(true);
//        set.setValueTextSize(10f);
        set.setDrawValues(false);
        set.setValueTextColor(Color.RED);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineData.addDataSet(set);

        return set;
    }

    //    @Override
//    public void onPointerCaptureChanged(boolean hasCapture) {
//
//    }
    @SuppressLint({"ClickableViewAccessibility", "HandlerLeak"})
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Check connection
        if(MainActivity.mConnectedThread == null)
        {
            Toast.makeText(getActivity(), "Chưa kết nối Bluetooth", Toast.LENGTH_LONG).show();
            clearBtn.setEnabled(false);
            upBtn.setEnabled(false);
            updateChartBtn.setEnabled(false);
        }

        //Update graph
        updateChartBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("test", "graph update");

                CombinedData data = new CombinedData();
                LineData lineDatas = new LineData();

                int i1 = Integer.parseInt(sData[0]);
                int i2 = Integer.parseInt(sData[1]);

//                indexE+=2;
                numSample+=2;
                lineDatas.addDataSet(dataChart(i1));
                lineDatas.addDataSet(dataChart(i2));

                data.setData(lineDatas);
                mChart.setData(data);
                mChart.invalidate();
                Log.d("length", numSample + "");
            }
        });

        //Clear graph, data
        clearBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(), "Clear", Toast.LENGTH_SHORT).show();

                Log.d("test", data.size() + "");

                mChart.clear();
                data.clear();
                mChart.invalidate();

                Log.d("test", data.size() + "");
                updateChartBtn.setEnabled(false);
                upBtn.setEnabled(true);
                dialogIsShowing = false;

            }
        });

        //Start section
        upBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(), "Start", Toast.LENGTH_SHORT).show();

                upBtn.setEnabled(false);
                updateChartBtn.setEnabled(true);
            }
        });

        //Recieve message
        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == MainActivity.MESSAGE_READ){
                    try
                    {
                        readMessage = new String((byte[]) msg.obj, "US-ASCII");
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }

                    Log.d("test","Read " + readMessage );
                    tmpStr += readMessage;

                    // Poor signal
                    if(tmpStr.contains("E"))
                    {
                        //Toast.makeText(getActivity().getApplicationContext(), "Tín hiệu kém, vui lòng thử lại", Toast.LENGTH_LONG).show();
                        if (!dialogIsShowing) {
                            dialogIsShowing = true;
                            showAlertDialogError();
                        }
                        clearBtn.callOnClick();
                        tmpStr = "";
                    }

                    //End of section
                    if(tmpStr.contains("DD"))
                    {
//                        Toast.makeText(getActivity(), "Kết thúc", Toast.LENGTH_LONG).show();
                        if (!dialogIsShowing) {
                            dialogIsShowing = true;
                            showAlertDialogDone();
                        }
                        upBtn.setEnabled(true);
                        updateChartBtn.setEnabled(false);
                        tmpStr = "";
                    }

                    //Start section
                    if(tmpStr.contains("Y"))
                    {
                        Toast.makeText(getActivity(), "Đang tiến hành...", Toast.LENGTH_SHORT).show();
                        upBtn.setEnabled(true);
                        updateChartBtn.setEnabled(false);
                        tmpStr = "";
                    }

                    //Data
                    if(tmpStr.contains("*"))
                    {
                        upBtn.setEnabled(false);
                        updateChartBtn.setEnabled(true);
                        Log.d("test", "tmpS " + tmpStr.substring(0,tmpStr.indexOf('*')));
                        tmpStr = tmpStr.substring(0,tmpStr.indexOf('*'));
                        if (tmpStr.isEmpty() == true)
                        {
                            tmpStr = "";
                        }
                        else if (tmpStr.isEmpty() == false)
                        {
                            if (tmpStr.indexOf("S") == 0 && tmpStr.indexOf("A") >= 8)
                            {
                                String senSorData = tmpStr.substring(1, tmpStr.indexOf('A'));

                                //Count number of regex 'T'
                                int count = 0;
                                for(int i = 0; i < tmpStr.length(); i++)
                                {
                                    if (tmpStr.charAt(i) == 'T')
                                    {
                                        count++;
                                    }
                                }

                                //Only update if 'T' == 3
                                if (count == 3)
                                {
                                    sData = senSorData.split("T");
                                    Log.d("test", "data 0 " + sData[0]);
                                    Log.d("test", "data 1 " + sData[1]);
                                    Log.d("test", "data 2 " + sData[2]);
                                    Log.d("test", "data 3 " + sData[3]);

                                    //int i1 = Integer.parseInt(sData[0]);
                                    //Log.d("test", "i1 " + i1 + '/');

                                    heartRate = frag_view.findViewById(R.id.view_bpm);
                                    spO2 = frag_view.findViewById(R.id.view_spo2);

                                    heartRate.setText(sData[2] + " BPM");
                                    spO2.setText(sData[3] + " %");

                                    tmpStr = "";
                                    updateChartBtn.callOnClick();
                                }
                            }
                            else
                            {
                                tmpStr = "";
                            }
                        }
                    }
                }
                if(msg.what == MainActivity.CONNECTING_STATUS)
                {
                    if(msg.arg1 == 1) {
                        Toast.makeText(getActivity(), "Đã kết nối với " + (String) (msg.obj), Toast.LENGTH_SHORT).show();
                        TabFragment2.setBTStatusText("Đã kết nối với " + (String) (msg.obj));
                        MainActivity.mConnectedThread.start();
                        //Enable button
                        clearBtn.setEnabled(true);
                        upBtn.setEnabled(true);
//                        updateChartBtn.setEnabled(true);
                    }
                    else {
                        Toast.makeText(getActivity(), "Kết nối thất bại", Toast.LENGTH_SHORT).show();
                        TabFragment2.setBTStatusText("Kết nối thất bại");
                        //Disable button until reconnect
                        clearBtn.setEnabled(false);
                        upBtn.setEnabled(false);
                        updateChartBtn.setEnabled(false);
                    }
                }
            }
        };


    }
    public void showAlertDialogDone(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Heart Monitor");
        builder.setMessage("Kết quả \n"  + "Max BPM: " + sData[0] + "\n" + "Min BPM: " + sData[1] + "\n" + "Avg BPM: " + sData[2] + "\n" + "SpO2: " +sData[3]);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {

                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Đo tiếp", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                clearBtn.callOnClick();
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void showAlertDialogError(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Heart Monitor");
        builder.setMessage("Tín hiệu kém, vui lòng thử lại");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
