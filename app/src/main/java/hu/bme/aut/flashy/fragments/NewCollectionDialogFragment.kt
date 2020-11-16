package hu.bme.aut.flashy.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hu.bme.aut.flashy.R
import hu.bme.aut.flashy.data.collection.Collection

class NewCollectionDialogFragment : DialogFragment() {

    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText

    interface NewCollectionDialogListener {
        fun onCollectionCreated(newCollection: Collection)
    }

    private lateinit var listener: NewCollectionDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewCollectionDialogListener
            ?: throw RuntimeException("Activity must implement the NewCollectionDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_collection)
            .setView(getContentView())
            .setPositiveButton(R.string.ok) { dialogInterface, i ->
                if (isValid()) {
                    listener.onCollectionCreated(getCollection())
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    private fun isValid() = nameEditText.text.isNotEmpty()

    private fun getCollection(): Collection{
        val background = (1..4).shuffled().first()

        return Collection(
            id = null,
            name = nameEditText.text.toString(),
            description = descriptionEditText.text.toString(),
            color = Collection.CollectionColor.getByOrdinal(background)
                ?: Collection.CollectionColor.YELLOW
        )
    }



    private fun getContentView(): View {
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_new_collection, null)
        nameEditText = contentView.findViewById(R.id.CollectionNameEditText)
        descriptionEditText = contentView.findViewById(R.id.CollectionDescriptionEditText)

        return contentView
    }

    companion object {
        const val TAG = "NewCollectionDialogFragment"
    }
}