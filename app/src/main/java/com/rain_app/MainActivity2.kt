package com.rain_app

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Window
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity


class MainActivity2 : AppCompatActivity() {

    var years = ArrayList<String>();
    var myDB = DatabaseHelper(this@MainActivity2);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main2)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

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
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = parent.getItemAtPosition(position)
                System.out.println((item))
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