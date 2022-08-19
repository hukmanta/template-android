@file:Suppress("unused")

package com.example.template

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.template.infrastructure.networking.RetrofitBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.regex.Pattern

//helper function to validate inputField
fun TextInputLayout.validateNeeded(): Boolean {
    val inputString = this.editText?.text.toString().trim()
    return if (inputString.isEmpty()) {
        this.error = "This Field can't be empty"
        false
    } else {
        this.error = null
        this.isErrorEnabled = false
        true
    }
}

//helper function to validate inputField
fun TextInputLayout.overMaximum(): Boolean {
    val inputString = this.editText?.text.toString().trim()
    return when {
        inputString.isEmpty() -> {
            this.error = "This Field can't be empty"
            false
        }
        inputString.count() > 50 -> {
            this.error = "This Field character exceed 50"
            false
        }
        else -> {
            this.error = null
            this.isErrorEnabled = false
            true
        }
    }
}

fun TextInputLayout.validateDate(): Boolean {
    val regex = "^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/(19|20)[0-9]{2}$"
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(this.editText?.text.toString().trim())
    val inputString = this.editText?.text.toString().trim()
    return if (inputString.isEmpty()) {
        this.error = "This Field can't be empty"
        false
    } else if (!matcher.matches()) {
        this.error = "Not Valid date format"
        false
    } else {
        this.error = null
        this.isErrorEnabled = false
        true
    }
}

fun TextInputLayout.validateYear(): Boolean {
    val regex = "^(20|19)[0-9]{2}$"
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(this.editText?.text.toString().trim())
    val inputString = this.editText?.text.toString().trim()
    return if (inputString.isEmpty()) {
        this.error = "This Field can't be empty"
        false
    } else if (!matcher.matches()) {
        this.error = "Not Valid date Year"
        false
    } else {
        this.error = null
        this.isErrorEnabled = false
        true
    }
}

fun Fragment.showToastLong(message: String) {
    if (this.isVisible || this.isResumed) {
        val toast =
            Toast.makeText(this.requireContext().applicationContext, message, Toast.LENGTH_LONG).also { it.setGravity(Gravity.CENTER,0,0) }
        toast.show()
    }
}

fun Activity.showToastLong(message: String) {
    if (!this.isFinishing) {
        val toast = Toast.makeText(this.applicationContext, message, Toast.LENGTH_LONG).also { it.setGravity(Gravity.CENTER,0,0) }
        toast.show()
    }
}

fun Fragment.showSnackBarLong(message: String) {
    if (this.isVisible || this.isResumed) {
        val snackbar = Snackbar.make(this.requireView(), message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }
}

fun Fragment.showSnackBarShort(message: String) {
    if (this.isVisible || this.isResumed) {
        val snackbar = Snackbar.make(this.requireView(), message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }
}

fun TextInputLayout?.show() {
    this?.visibility = View.VISIBLE
}

fun TextInputLayout?.hide() {
    this?.visibility = View.GONE
}


@SuppressLint("InlinedApi")
fun FragmentActivity?.hideKeyboard() {
    val imm = this?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = this.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun String.simplify(): String {
    val maxLength = 15
    var allSpaceIndex = listOf<Int>()
    this.removePrefix(" ")
    this.removeSuffix(" ")
    var index = 0
    for (char in this) {
        if (char == ' ') {
            allSpaceIndex = allSpaceIndex.plus(index)
        }
        index++
    }
    var find = false
    while (!find) {
        if (allSpaceIndex.lastIndex > maxLength) {
            allSpaceIndex = allSpaceIndex.minus(allSpaceIndex.lastIndex)
        } else {
            find = true
        }
    }
    return substring(0, (allSpaceIndex[allSpaceIndex.lastIndex]))
}

fun Fragment.serverError(e: HttpException) = runBlocking {
    val body = e.response()!!.errorBody()
    val errorConverter = RetrofitBuilder.errorConverter
    val serverErrorParser = withContext(IO) {
        errorConverter.convert(body!!)
    }

    withContext(Main) {
        this@serverError.showSnackBarLong(
            serverErrorParser?.statusMessage
                ?: "Connection Reset, Silahkan coba lagi beberapa saat yang akan datang"
        )
        Log.e(
            this@serverError.javaClass.simpleName,
            serverErrorParser?.statusMessage ?: e.message()
        )
        e.printStackTrace()
    }
}