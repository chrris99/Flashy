package hu.bme.aut.flashy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import hu.bme.aut.flashy.adapter.CollectionAdapter
import hu.bme.aut.flashy.data.collection.Collection
import hu.bme.aut.flashy.data.collection.CollectionDatabase
import hu.bme.aut.flashy.fragments.NewCollectionDialogFragment
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), CollectionAdapter.CollectionClickListener,
    NewCollectionDialogFragment.NewCollectionDialogListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CollectionAdapter
    private lateinit var database: CollectionDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.ActionBar))

        findViewById<View>(R.id.addCollectionFab).setOnClickListener{
            NewCollectionDialogFragment().show(
                supportFragmentManager,
                NewCollectionDialogFragment.TAG
            )
        }

        database = Room.databaseBuilder(
            applicationContext,
            CollectionDatabase::class.java,
            "collection-list"
        )
            .fallbackToDestructiveMigration()
            .build()

        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.CollectionRecyclerView)
        adapter = CollectionAdapter(this)
        loadItemsInBackground()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadItemsInBackground() {
        thread {
            val collections = database.collectionDao().getAll()
            runOnUiThread {
                adapter.updateCollection(collections)
            }
        }
    }

    override fun onCollectionChanged(collection: Collection) {
        thread {
            database.collectionDao().update(collection)
            Log.d("MainActivity", "Collection update was successful")
        }
    }

    override fun onCollectionCreated(newCollection: Collection) {
        thread {
            val newId = database.collectionDao().insert(newCollection)
            val newCollection = newCollection.copy(
                id = newId
            )
            runOnUiThread {
                adapter.addCollection(newCollection)
            }
        }
    }

    override fun onCollectionRemoved(collection: Collection) {
        thread {
            database.collectionDao().deleteItem(collection)
            Log.d("MainActivity", "Collection was removed succesfully")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.menuSettings -> {
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menuMore -> {
                Toast.makeText(this, "More selected", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}