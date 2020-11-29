package hu.bme.aut.flashy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import hu.bme.aut.flashy.adapter.CollectionAdapter
import hu.bme.aut.flashy.data.collection.Collection
import hu.bme.aut.flashy.data.collection.CollectionDatabase
import hu.bme.aut.flashy.data.flashcard.FlashcardDatabase
import hu.bme.aut.flashy.fragments.EditCollectionDialogFragment
import hu.bme.aut.flashy.fragments.NewCollectionDialogFragment
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), CollectionAdapter.CollectionClickListener,
    NewCollectionDialogFragment.NewCollectionDialogListener,
    EditCollectionDialogFragment.EditCollectionDialogListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CollectionAdapter
    private lateinit var notificatonBuilder: NotificationCompat.Builder
    private lateinit var collectionDatabase: CollectionDatabase
    private lateinit var flashcardDatabase: FlashcardDatabase

    var flashcardCount : Int = 1

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

        collectionDatabase = Room.databaseBuilder(
            applicationContext,
            CollectionDatabase::class.java,
            "collection-list"
        )
            .fallbackToDestructiveMigration()
            .build()

        flashcardDatabase = Room.databaseBuilder(
            applicationContext,
            FlashcardDatabase::class.java,
            "flashcard-list"
        )
            .fallbackToDestructiveMigration()
            .build()

        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        loadItemsInBackground()
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
            val collections = collectionDatabase.collectionDao().getAll()
            runOnUiThread {
                adapter.updateCollection(collections)
            }
        }
    }

    override fun onCollectionChanged(changedCollection: Collection) {
        thread {
            collectionDatabase.collectionDao().update(changedCollection)
            Log.d("MainActivity", "Collection update was successful")
            loadItemsInBackground()
        }
    }

    override fun onCollectionCreated(newCollection: Collection) {
        thread {
            val newId = collectionDatabase.collectionDao().insert(newCollection)
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
            // Remove flashcards in collection
            val flashcardsInCollection = flashcardDatabase.flashcardDao().getAll(collection.id!!)
            if (flashcardsInCollection.isNotEmpty()) {
                for (flashcard in flashcardsInCollection) {
                    flashcardDatabase.flashcardDao().deleteItem(flashcard)
                }
            }


            // Remove collection
            collectionDatabase.collectionDao().deleteItem(collection)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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