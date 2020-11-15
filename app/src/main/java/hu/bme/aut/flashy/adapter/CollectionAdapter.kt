package hu.bme.aut.flashy.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.flashy.FlashcardActivity
import hu.bme.aut.flashy.MainActivity
import hu.bme.aut.flashy.R
import hu.bme.aut.flashy.data.collection.Collection


class CollectionAdapter(private val listener: MainActivity) :
    RecyclerView.Adapter<CollectionAdapter.CollectionHolder>() {

    private val collections = mutableListOf<Collection>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionHolder {
        val collectionView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.collection_item, parent, false)
        return CollectionHolder(collectionView)
    }

    override fun onBindViewHolder(holder: CollectionHolder, position: Int) {
        val collection = collections[position]
        holder.nameTextView.text = collection.name
        holder.descriptionTextView.text = collection.description

        holder.collection = collection
    }

    override fun getItemCount(): Int {
        return collections.size
    }

    fun addCollection(collection: Collection) {
        collections.add(collection)
        notifyItemInserted(collections.size - 1)
    }

    fun updateCollection(collections: List<Collection>) {
        this.collections.clear()
        this.collections.addAll(collections)
        notifyDataSetChanged()
    }

    fun removeCollection(position: Int) {
        val collection = collections[position]
        collections.removeAt(position);
        notifyItemRemoved(position);
        listener.onCollectionRemoved(collection)
    }

    interface CollectionClickListener {
        fun onCollectionChanged(collection: Collection)
        fun onCollectionRemoved(collection: Collection)
    }

    inner class CollectionHolder(collectionView: View) : RecyclerView.ViewHolder(collectionView){
        val nameTextView: TextView = collectionView.findViewById(R.id.CollectionName)
        val descriptionTextView: TextView = collectionView.findViewById(R.id.CollectionDescription)

        var collection: Collection? = null

        init {
            itemView.setOnClickListener{
                val flashcardListIntent = Intent(listener, FlashcardActivity::class.java)
                flashcardListIntent.putExtra("collectionId", collection!!.id)
                Log.d("CollectionAdapter", "Collection id ${collection!!.id} Collection name ${collection!!.name}")
                listener.startActivity(flashcardListIntent)
            }
        }
    }
}