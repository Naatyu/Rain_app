package com.rain_app

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    var myDB = DatabaseHelper(this@MainActivity);
    var date = ArrayList<String>();
    var rainvalue = ArrayList<String>();
    var id = ArrayList<String>();
    var customAdapter = CustomAdapter(this@MainActivity, date, rainvalue);
    var rainsumtext: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }


        var valueview = findViewById<RecyclerView>(R.id.valueview);
        val pickdate = findViewById<DatePicker>(R.id.pickDate);
        val editrainnum = findViewById<EditText>(R.id.editRainnum);
        var displaymonth = findViewById<TextView>(R.id.displaymonth);
        rainsumtext = findViewById<TextView>(R.id.Rainsum);

        val monthFormat = SimpleDateFormat("MMMM", Locale.FRANCE)
        val month = Date()
        displaymonth.text = "durant le mois de "+monthFormat.format(month)



        valueview.adapter = customAdapter;
        valueview.layoutManager = LinearLayoutManager(this@MainActivity);
        customAdapter.notifyDataSetChanged();
        mIth.attachToRecyclerView(valueview);

        storeDataInArrays()

        var rainsum = myDB.getRainMonth();
        rainsumtext?.text = rainsum;
;
        val StatsButton = findViewById<ImageButton>(R.id.StatsButton)
        StatsButton.setOnClickListener{
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
            finish()
        }

        val buttonaddrain = findViewById<Button>(R.id.buttonaddrain);
        buttonaddrain.setOnClickListener{
            val day = pickdate.dayOfMonth;
            val month = pickdate.month;
            val year = pickdate.year;
            val calendar = Calendar.getInstance()
            calendar.set(year, month, day)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val datefrompicker = dateFormat.format(calendar.time)

            if (editrainnum.text.toString() != "") {
                myDB.addValue(
                    datefrompicker,
                    Integer.valueOf(editrainnum.text.toString().trim())
                );
            }
            /* Move to newest ID value to get data that was just added*/
            val db: SQLiteDatabase = myDB.getReadableDatabase()
            val cursor = db.rawQuery("SELECT * FROM my_values WHERE _id = (SELECT MAX(_id) FROM my_values)", null)

            date = ArrayList<String>();
            rainvalue = ArrayList<String>();
            id = ArrayList<String>();

            storeDataInArrays()
            customAdapter = CustomAdapter(this@MainActivity, date, rainvalue);
            valueview.adapter = customAdapter;
            customAdapter.notifyDataSetChanged()

            val rainsum = myDB.getRainMonth();
            rainsumtext?.text = rainsum;
        }

    }

    private fun storeDataInArrays(){
        var cursor = myDB.readAllData();
        if(cursor.count == 0){
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show()
        }else{
            while (cursor.moveToNext()){
                id.add(cursor.getString(0));
                date.add(cursor.getString(1));
                rainvalue.add(cursor.getString(2));
            }
        }
    }

    var mIth = ItemTouchHelper(

        object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.RIGHT,
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: ViewHolder, target: ViewHolder
            ): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {

                confirmDialog(viewHolder)

            }
        })

    private fun confirmDialog(viewHolder: ViewHolder){
        var builder = AlertDialog.Builder(this);
        builder.setTitle("Suppresion")
        builder.setMessage("Supprimer cette valeur : ${rainvalue[viewHolder.adapterPosition]} mm du ${date[viewHolder.adapterPosition]} ?")
        builder.setPositiveButton("Oui") { dialog, which ->
            date.removeAt(viewHolder.adapterPosition);
            rainvalue.removeAt(viewHolder.adapterPosition);
            var db = DatabaseHelper(this@MainActivity);
            db.deleteOneRow(id[viewHolder.adapterPosition]);
            id.removeAt(viewHolder.adapterPosition);
            customAdapter.notifyDataSetChanged();
            var rainsum = db.rainMonth;
            rainsumtext?.text = rainsum;
        }
        builder.setNegativeButton("Non") { dialog, which ->
            customAdapter.notifyDataSetChanged()
        }
        customAdapter.notifyDataSetChanged()

        var alert = builder.create()
        alert.show()
    }

}