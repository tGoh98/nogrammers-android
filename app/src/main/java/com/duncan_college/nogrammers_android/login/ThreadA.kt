package com.duncan_college.nogrammers_android.login

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException



class ThreadA(context: Context, ticket: String, moveOnIntent: Intent, netID: String): AsyncTask<Void, Void, String>() {
    private var currentTicket = ""
    private val loginFormLink = "https://idp.rice.edu/idp/profile/cas/login"
    private val validateLink = "https://idp.rice.edu/idp/profile/cas/serviceValidate?service=https://httpbin.org/get"
//    private val cref: WeakReference<Context> = WeakReference(context)
    private val intent = moveOnIntent
    private val cref = context
    private val netID = netID


    override fun doInBackground(vararg p0: Void?): String {
        var q:RequestQueue = Volley.newRequestQueue(cref)
        var blockingReq : RequestFuture<String> = RequestFuture.newFuture()
        val validateReq = StringRequest(
            Request.Method.GET,
            "$validateLink&ticket=$currentTicket",
            blockingReq, blockingReq)
        q.add(validateReq)
        var response: String = ""
        try {
            while (response == "") {
                publishProgress()
                response = blockingReq.get(10, TimeUnit.SECONDS)
                println(response)
                if (response.contains("student") || response.contains("Success") || response.contains("Affiliation")) {// TODO: make this better
                    return "true"
                    println("Validation success")
                } else {
                    return "false"
                }
            }
        } catch (e: InterruptedException) {
            e.printStackTrace();
        } catch (e: ExecutionException) {
            e.printStackTrace();
        } catch (e: TimeoutException) {
            e.printStackTrace();
        }
        return "false"
    }


    override fun onProgressUpdate(vararg values: Void?) {
        super.onProgressUpdate(*values)
        Toast.makeText(cref,
            "Authenticating your credentials ...",
            Toast.LENGTH_SHORT
        ).show()

    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        var moveOn = intent.apply {
            putExtra(NETID_MESSAGE, netID)
        }
        startActivity(cref, moveOn, null)

    }

}