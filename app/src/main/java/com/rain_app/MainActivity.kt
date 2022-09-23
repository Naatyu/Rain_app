package com.rain_app

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    var myDB = DatabaseHelper(this@MainActivity);
    var date = ArrayList<String>();
    var rainvalue = ArrayList<String>();
    var id = ArrayList<String>();
    var customAdapter = CustomAdapter(this@MainActivity, date, rainvalue);

    private lateinit var rainsumtext: TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }


        var valueview = findViewById<RecyclerView>(R.id.valueview);
        var editdate = findViewById<EditText>(R.id.editDate);
        var editrainnum = findViewById<EditText>(R.id.editRainnum);
        rainsumtext = findViewById<TextView>(R.id.Rainsum) as TextView

        var currentDate = SimpleDateFormat("dd/MM/yyyy")
        editdate.setText(currentDate.format(Date()))

        valueview.adapter = customAdapter;
        valueview.layoutManager = LinearLayoutManager(this@MainActivity);
        customAdapter.notifyDataSetChanged();
        mIth.attachToRecyclerView(valueview);
;
        val StatsButton = findViewById<ImageButton>(R.id.StatsButton)
        StatsButton.setOnClickListener{
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
            finish()
        }

        var buttonaddrain = findViewById<Button>(R.id.buttonaddrain);
        buttonaddrain.setOnClickListener{
            myDB.addValue(editdate.text.toString().trim(),
                          Integer.valueOf(editrainnum.text.toString().trim()));
            var cursor = myDB.readAllData();
            cursor.moveToLast()
            id.add(cursor.getString(0));
            date.add(cursor.getString(1));
            rainvalue.add(cursor.getString(2));

            var rainsum = myDB.getRainMonth();
            rainsumtext.text = rainsum;
        }

        storeDataInArrays()

        var rainsum = myDB.getRainMonth();
        rainsumtext.text = rainsum;
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
            ItemTouchHelper.RIGHT,) {
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
            var rainsum = db.getRainMonth();
            rainsumtext.text = rainsum;
        }
        builder.setNegativeButton("Non") { dialog, which ->
            customAdapter.notifyDataSetChanged()
        }
        customAdapter.notifyDataSetChanged()

        var alert = builder.create()
        alert.show()
    }

}