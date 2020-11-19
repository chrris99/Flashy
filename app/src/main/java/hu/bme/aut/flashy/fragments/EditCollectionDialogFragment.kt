package hu.bme.aut.flashy.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hu.bme.aut.flashy.R
import hu.bme.aut.flashy.data.collection.Collection

class EditCollectionDialogFragment : DialogFragment() {

    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText

    interface EditCollectionDialogListener {
        fun onCollectionChanged(changedCollection: Collection)
    }

    private lateinit var listener: EditCollectionDialogListener
    private lateinit var collection: Collection

    fun setCollection(collection: Collection?) {
        if (collection != null) {
            this.collection = collection
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? EditCollectionDialogListener
            ?: throw RuntimeException("Activity must implement the EditCollectionDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit_collection)
            .setView(getContentView())
            .setPositiveButton(R.string.ok) { dialogInterface, i ->
                if (isValid()) {
                    listener.onCollectionChanged(getCollection())
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    private fun isValid() = nameEditText.text.isNotEmpty()

    private fun getCollection(): Collection {
        return Collection(
            id = collection.id,
            name = nameEditText.text.toString(),
            description = descriptionEditText.text.toString(),
            color = collection.color
        )
    }

    private fun getContentView(): View {
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_new_collection, null)
        nameEditText = contentView.findViewById(R.id.CollectionNameEditText)
        descriptionEditText = contentView.findViewById(R.id.CollectionDescriptionEditText)

        nameEditText.setText(collection.name, TextView.BufferType.EDITABLE)
        descriptionEditText.setText(collection.description, TextView.BufferType.EDITABLE)

        return contentView
    }

    companion object {
        const val TAG = "EditCollectionDialogFragment"
    }
}