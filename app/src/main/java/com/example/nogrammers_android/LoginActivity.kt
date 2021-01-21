package com.example.nogrammers_android

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.*
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException


const val NETID_MESSAGE = "com.example.nogrammers_android.NETID_MESSAGE"


/**
 * Login screen
 */
class LoginActivity : AppCompatActivity() {
    private val loginFormLink = "https://idp.rice.edu/idp/profile/cas/login"
    private val validateLink = "https://idp.rice.edu/idp/profile/cas/serviceValidate"
    private var executionCount = 1
    private var versionCount = 1
    private var currentTicket: String = ""
    private var validated = false
    private val queue = Volley.newRequestQueue(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        executionCount = 1
        versionCount = 1
        // GET request to obtain login url-encoded form
        obtainLoginForm()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /* Set button listener */
        findViewById<Button>(R.id.loginBtn).setOnClickListener {
            /* Pass netID information to main activity */

            val netID = findViewById<EditText>(R.id.editTextNetID).text.toString().trim()
            val pwd = findViewById<EditText>(R.id.editTextPassword).text.toString()

            if (netID == "" || netID.length < 3) Toast.makeText(
                applicationContext,
                "Please enter a net ID!",
                Toast.LENGTH_SHORT
            ).show()
            else if (pwd == "") Toast.makeText(
                applicationContext,
                "Please enter your password!",
                Toast.LENGTH_SHORT
            ).show()
            else {
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra(NETID_MESSAGE, netID)
                }
                // Try to obtain a ticket from CAS and validate user
                obtainTicketAndValidate()

                if (validated) {
                    startActivity(intent) // lets user go through
                    finish()
                } else {
                    executionCount += 1
                    versionCount += 1
                }
            }
        }
    }

    // preload the cas form info; our entries basically acts as the rice form
    // first, try to access the form and get the e and s nums (execution html param)
    private fun obtainLoginForm(){
        try {
            val formReq = StringRequest(
                Request.Method.GET,
                "$loginFormLink?service=https://httpbin.org/get",
                Response.Listener<String> { response ->
                    run {
                        var position = response.indexOf("execution=")
                        var eAndS = response.substring(position + 11, position + 15)
                        executionCount = eAndS[1].toInt()
                        versionCount = eAndS[3].toInt()
                    }
                },
                Response.ErrorListener { })
            queue.add(formReq)
            // if it fails, try to send the request again and look for a ticket.
            //  this means they already are signed in
        } catch (e: Exception) {
            val ticketReq = object: JsonObjectRequest(
                "$loginFormLink?service=https://httpbin.org/get",
                null, // empty body makes it a GET request
                Response.Listener<JSONObject>() { response ->
                    run {
                        var loc = response.getJSONObject("headers").getString("Location")
                        var potentialTicket = loc.substringAfter("ticket=", "")
                        if (potentialTicket.length > 1)
                            currentTicket = potentialTicket
                        else
                            findTicket(response)
                    }
                },
                Response.ErrorListener {}){
                override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {
                    try {
                        val headerString = HttpHeaderParser.parseCharset(response!!.headers, PROTOCOL_CHARSET)
                        val jsonResponse = JSONObject(headerString)
                        jsonResponse.put("headers", JSONObject(response!!.headers as Map<*, *>))
                        return Response.success(
                            jsonResponse,
                            HttpHeaderParser.parseCacheHeaders(response)
                        )
                    } catch (e: UnsupportedEncodingException) {
                        Response.error<JSONObject>(ParseError(e))
                    } catch (je: JSONException) {
                        Response.error<JSONObject>(ParseError(je))
                    }
                    return super.parseNetworkResponse(response)
                }
            }
            queue.add(ticketReq)
        }
    }

    private fun findTicket(response:JSONObject){
        try {
            var args = response.getJSONObject("args")
            currentTicket = args["ticket"] as String
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    private fun obtainTicketAndValidate() {

        // if no ticket, then send POST to get it
        if (currentTicket == "") {

            try {
                val signInReq = object:StringRequest(
                    Request.Method.POST,
                    "$loginFormLink?execution="+"e"+executionCount.toString()+"s"+versionCount.toString(),
                    Response.Listener<String> { response ->
                            findTicket(JSONObject(response))
                    },
                    Response.ErrorListener{  }){
                    override fun getBodyContentType(): String? {
                        var map : Map<String, String> = hashMapOf("Content-Type" to "application/x-www-form-urlencoded")
                        return "application/x-www-form-urlencoded"
                    }
                    override fun getParams(): MutableMap<String, String>? {
                        val netID = findViewById<EditText>(R.id.editTextNetID).text.toString().trim()
                        val pwd = findViewById<EditText>(R.id.editTextPassword).text.toString()
                        return hashMapOf("j_username" to netID,
                            "j_password" to pwd, "_eventId_proceed" to "")
                    }
                }
                queue.add(signInReq)
                // if it fails, try to send the request again and look for a ticket,
                //  means they already are signed in
            } catch (e:Exception) {
                e.printStackTrace()
            }
            // otherwise, lets validate the ticket with CAS and let them through
        } else {
            val validateReq = object:StringRequest(
                Request.Method.GET,
                validateLink,
                Response.Listener<String> { response ->
                    run{
                        if (response.contains("student") || response.contains("success"))
                            validated = true
                    }
                },
                Response.ErrorListener{  }){
                override fun getBodyContentType(): String? {
                    return "text/html;charset=utf-8"
                }
            }
            queue.add(validateReq)
        }


    }

}