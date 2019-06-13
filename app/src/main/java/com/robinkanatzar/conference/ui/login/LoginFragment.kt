package com.robinkanatzar.conference.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.robinkanatzar.conference.MainActivity
import com.robinkanatzar.conference.R
import com.robinkanatzar.conference.di.Injectable
import com.robinkanatzar.conference.util.Constants
import com.robinkanatzar.conference.util.PreferenceHelper
import com.robinkanatzar.conference.util.PreferenceHelper.set
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.android.synthetic.main.login_fragment.view.*

class LoginFragment : Fragment(), Injectable, View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var prefs: SharedPreferences
    private var conferenceId: String? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        if (BuildConfig.DEBUG) {
            FirebaseFirestore.setLoggingEnabled(true)
        }

        val safeArgs: LoginFragmentArgs by navArgs()
        conferenceId = safeArgs.conferenceId

        auth = FirebaseAuth.getInstance()
        context?.let { prefs = PreferenceHelper.defaultPrefs(it) }

        val v = inflater.inflate(R.layout.login_fragment, null)

        v.btn_login_sign_in.setOnClickListener { onClick(btn_login_sign_in) }
        v.btn_login_create_account.setOnClickListener { onClick(btn_login_create_account) }
        v.btn_login_sign_out.setOnClickListener { onClick(btn_login_sign_out) }
        v.btn_login_verify_email.setOnClickListener { onClick(btn_login_verify_email) }
        v.btn_login_continue.setOnClickListener{ onClick(btn_login_continue) }

        return v
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun createAccount(email: String, password: String) {
        if (!validateForm()) {
            return
        }

        this@LoginFragment.activity?.let {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        Snackbar.make(it.findViewById(android.R.id.content),
                                task.exception?.localizedMessage.toString(), Snackbar.LENGTH_LONG).show()
                        updateUI(null)
                    }
                }
        }
    }

    private fun signIn(email: String, password: String) {
        if (!validateForm()) {
            return
        }

        this@LoginFragment.activity?.let {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        Snackbar.make(it.findViewById(android.R.id.content),
                                getString(R.string.success), Snackbar.LENGTH_LONG).show()
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        Snackbar.make(it.findViewById(android.R.id.content),
                                task.exception?.localizedMessage.toString(), Snackbar.LENGTH_LONG).show()
                        updateUI(null)
                    }
                }
        }
    }

    private fun signOut() {
        auth.signOut()
        updateUI(null)
    }

    private fun sendEmailVerification() {
        btn_login_verify_email.isEnabled = false

        val user = auth.currentUser
        this@LoginFragment.activity?.let {
            user?.sendEmailVerification()
                ?.addOnCompleteListener(it) { task ->
                    btn_login_verify_email.isEnabled = true

                    if (task.isSuccessful) {
                        Snackbar.make(it.findViewById(android.R.id.content),
                                getString(R.string.login_validate_email_success), Snackbar.LENGTH_LONG).show()
                    } else {
                        Snackbar.make(it.findViewById(android.R.id.content),
                                task.exception?.localizedMessage.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = et_login_email.text.toString()
        if (TextUtils.isEmpty(email)) {
            et_login_email.error = getString(R.string.login_required_field)
            valid = false
        } else {
            et_login_email.error = null
        }

        val password = et_login_password.text.toString()
        if (TextUtils.isEmpty(password)) {
            et_login_password.error = getString(R.string.login_required_field)
            valid = false
        } else {
            et_login_password.error = null
        }

        return valid
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            cl_login_connected.visibility = View.VISIBLE
            cl_login_not_connected.visibility = View.GONE

            if (!user.isEmailVerified) {
                tv_login_verify_email.visibility = View.VISIBLE
                btn_login_verify_email.visibility = View.VISIBLE
            } else {
                tv_login_verify_email.visibility = View.GONE
                btn_login_verify_email.visibility = View.GONE
            }

            tv_login_connected_title.text = getString(R.string.login_continue_as, user.email)

            prefs?.let { prefs[Constants.SP_USER_ID] = user.uid }
            conferenceId?.let {
                prefs[Constants.SP_CONFERENCE_ID] = it
            }
        } else {
            cl_login_connected.visibility = View.GONE
            cl_login_not_connected.visibility = View.VISIBLE

            prefs?.let { prefs[Constants.SP_USER_ID] = null }
            prefs[Constants.SP_CONFERENCE_ID] = null
        }
    }

    private fun continueToSchedule() {
        val activity = activity as MainActivity?
        activity?.let { it.setUpNavigationMenuHeader(conferenceId.toString()) }

        val action = LoginFragmentDirections.nextAction(conferenceId)
        findNavController().navigate(action)
    }

    override fun onClick(v: View) {
        val i = v.id
        when (i) {
            R.id.btn_login_create_account -> createAccount(et_login_email.text.toString(), et_login_password.text.toString())
            R.id.btn_login_sign_in -> signIn(et_login_email.text.toString(), et_login_password.text.toString())
            R.id.btn_login_sign_out -> signOut()
            R.id.btn_login_verify_email -> sendEmailVerification()
            R.id.btn_login_continue -> continueToSchedule()
        }
    }
}