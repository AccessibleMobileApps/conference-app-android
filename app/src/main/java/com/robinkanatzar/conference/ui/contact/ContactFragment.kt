package com.robinkanatzar.conference.ui.contact

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*
import com.robinkanatzar.conference.R
import com.robinkanatzar.conference.di.Injectable
import com.robinkanatzar.conference.util.Constants
import com.robinkanatzar.conference.util.PreferenceHelper
import com.robinkanatzar.conference.util.PreferenceHelper.get
import com.robinkanatzar.conference.vo.Contact
import kotlinx.android.synthetic.main.contact_fragment.*
import kotlinx.android.synthetic.main.contact_fragment.view.*
import timber.log.Timber

class ContactFragment : Fragment(), Injectable {

    lateinit var firestore: FirebaseFirestore
    lateinit var query: Query

    private var conferenceId: String? = null
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        if (BuildConfig.DEBUG) {
            FirebaseFirestore.setLoggingEnabled(true)
        }

        firestore = FirebaseFirestore.getInstance()

        context?.let { prefs = PreferenceHelper.defaultPrefs(it) }
        conferenceId = prefs[Constants.SP_CONFERENCE_ID]
        if (this.conferenceId == null) {
            val action = ContactFragmentDirections.chooseConferenceAction()
            findNavController().navigate(action)
        }

        val v = inflater.inflate(R.layout.contact_fragment, null)
        v.btn_contact.setOnClickListener { onSubmitClicked() }

        v.cl_contact.setOnClickListener {
            it.hideKeyboard()
        }

        return v
    }

    private fun onSubmitClicked() {

        if (et_contact_first_name.text != null && et_contact_first_name.text.isNotEmpty()
                && et_contact_last_name.text != null && et_contact_last_name.text.isNotEmpty()
                && et_contact_email.text != null && et_contact_email.text.isNotEmpty()
                && et_contact_message.text != null && et_contact_message.text.isNotEmpty()) {

            if (Patterns.EMAIL_ADDRESS.matcher(et_contact_email.text.toString().trim()).matches()) {
                val contact = Contact(
                        et_contact_first_name.text.toString().trim(),
                        et_contact_last_name.text.toString().trim(),
                        et_contact_email.text.toString().trim(),
                        et_contact_message.text.toString().trim(),
                        conferenceId)

                onSubmit(contact)
            } else {
                activity?.let {
                    Snackbar.make(it.findViewById(android.R.id.content),
                            resources.getString(R.string.contact_error_email_format), Snackbar.LENGTH_LONG).show()
                }
            }

        } else {
            activity?.let {
                Snackbar.make(it.findViewById(android.R.id.content),
                        resources.getString(R.string.contact_error_required_fields), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun onSubmit(contact: Contact) {

        firestore.collection("contact")
            .add(contact)
            .addOnSuccessListener { documentReference ->
                et_contact_first_name.text.clear()
                et_contact_last_name.text.clear()
                et_contact_email.text.clear()
                et_contact_message.text.clear()

                activity?.let {
                    Snackbar.make(it.findViewById(android.R.id.content),
                            resources.getString(R.string.contact_success_sending), Snackbar.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Timber.i("FAILURE sending, e = " + e.localizedMessage)
                activity?.let {
                    Snackbar.make(it.findViewById(android.R.id.content),
                            resources.getString(R.string.contact_error_sending), Snackbar.LENGTH_LONG).show()
                }
            }
    }

    fun View.hideKeyboard() {
        val inputMethodManager = context!!.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(this.windowToken, 0)
    }
}