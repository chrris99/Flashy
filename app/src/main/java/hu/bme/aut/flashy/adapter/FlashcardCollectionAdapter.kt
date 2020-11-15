package hu.bme.aut.flashy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

import hu.bme.aut.flashy.R
import hu.bme.aut.flashy.data.FlashcardCollection

class FlashcardCollectionAdapter(private val listener: FlashcardCollectionClickListener) :
    RecyclerView.Adapter<FlashcardCollectionAdapter.FlashcardCollectionHolder>() {

    private val flashcardCollections = mutableListOf<FlashcardCollection>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardCollectionHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.collection_item, parent, false)
        return FlashcardCollectionHolder(itemView)
    }

    override fun onBindViewHolder(holder: FlashcardCollectionHolder, position: Int) {
        val flashcardCollection = flashcardCollections[position]
        holder.nameTextView.text = flashcardCollection.name
        holder.descriptionTextView.text = flashcardCollection.description

        holder.flashcardCollection = flashcardCollection
    }

    override fun getItemCount(): Int {
        return flashcardCollections.size
    }

    fun addItem(item: FlashcardCollection) {
        flashcardCollections.add(item)
        notifyItemInserted(flashcardCollections.size - 1)
    }

    fun update(shoppingItems: List<FlashcardCollection>) {
        flashcardCollections.clear()
        flashcardCollections.addAll(shoppingItems)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        val flashcardCollection = flashcardCollections[position]
        flashcardCollections.removeAt(position);
        notifyItemRemoved(position);
        listener.onFlashcardCollectionRemoved(flashcardCollection)
    }

    interface FlashcardCollectionClickListener {
        fun onFlashcardCollectionChanged(flashcardCollection: FlashcardCollection)
        fun onFlashcardCollectionRemoved(flashcardCollection: FlashcardCollection)
    }

    inner class FlashcardCollectionHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nameTextView: TextView = itemView.findViewById(R.id.FlashcardCollectionName)
        val descriptionTextView: TextView = itemView.findViewById(R.id.FlashcardCollectionDescription)

        var flashcardCollection: FlashcardCollection? = null
    }
}