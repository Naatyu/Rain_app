package com.rain_app

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class MainActivity2 : AppCompatActivity() {

    var years = ArrayList<String>();
    var myDB = DatabaseHelper(this@MainActivity2);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); //For night mode theme
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main2)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        val barview = findViewById<HorizontalBarChart>(R.id.barchart)
        
        val HomeButton = findViewById<ImageButton>(R.id.HomeButton)
        val dropButton = findViewById<Spinner>(R.id.annee)
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this@MainActivity2,
            android.R.layout.simple_spinner_item, years
        )
        add_years()
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropButton.adapter = adapter

        dropButton.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = parent.getItemAtPosition(position)
                val month_data = ArrayList<BarEntry>();
                var db: SQLiteDatabase = myDB.readableDatabase
                var month_pos = 11

                for (month in 1..12){
                    val cursor = db.rawQuery(
                        "SELECT SUM(rain_value) " +
                            "FROM my_values " +
                            "WHERE substr(date,1,7) == "+'"'+item+'-'+month+'"', null)
                    while (cursor.moveToNext()){
                        if (cursor.getString(0) == null){
                            month_data.add(BarEntry((month_pos).toFloat(), 0f))
                        } else {
                            month_data.add(BarEntry((month_pos).toFloat(), cursor.getFloat(0)))
                        }
                    }
                    month_pos -= 1
                }

                /* Set data to bars */
                val bardataset = BarDataSet(month_data, "Monthly data")
                val barData = BarData(bardataset)
                barData.setValueTextColor(Color.WHITE)
                barData.setValueTextSize(16f)
                barview.setData(barData)

                barview.axisLeft.axisMinimum = 0f

                /* Bar design */
                barview.setDrawBarShadow(false);
                barview.setDrawValueAboveBar(true);
                barview.setPinchZoom(false);
                barview.setDrawGridBackground(false);

                barview.xAxis.setDrawGridLines(false)
                barview.description.isEnabled = false;
                barview.legend.isEnabled = false;

                val xAxisLabels = listOf("D", "N", "O", "S", "A",
                                         "J", "J", "M", "A", "M", "F", "J")
                barview.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM)
                barview.xAxis.setLabelCount(12)
                barview.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
                barview.xAxis.textColor = Color.WHITE
                barview.xAxis.textSize = 16f
                bardataset.color = Color.rgb(3, 119, 165)

                barview.axisLeft.isEnabled = false
                barview.axisRight.isEnabled = false


                barview.invalidate()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })


        HomeButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun add_years(){
        var db: SQLiteDatabase = myDB.readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT strftime('%Y', date) year " +
                                     "FROM my_values " +
                                     "ORDER BY year DESC", null)
        while (cursor.moveToNext()){
            years.add(cursor.getString(0));
        }
    }
}