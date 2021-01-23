package com.example.nogrammers_android

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.*
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookieStore

const val NETID_MESSAGE = "com.example.nogrammers_android.NETID_MESSAGE"


/**
 * Login screen
 */
class LoginActivity : AppCompatActivity() {
    private val loginFormLink = "https://idp.rice.edu/idp/profile/cas/login"
    private val validateLink = "https://idp.rice.edu/idp/profile/cas/serviceValidate?service=https://httpbin.org/get"
    private var executionCount = 1
    private var versionCount = 1
    private var currentTicket: String = ""
    private var validated = false
    private lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {

        CookieHandler.setDefault(CookieManager()) // supposedly lets session cookies persist s

        queue = Volley.newRequestQueue(this)

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
                }
            }
        }
    }

    // Loads the cas form info; our entries basically acts as the rice form
    // first, try to access the form and get the e and s nums (execution html param)
    private fun obtainLoginForm(){
        // Possibility 1: users are not logged in and the GET returns the form
        try {
            val formReq = StringRequest(
                Request.Method.GET,
                "$loginFormLink?service=https://httpbin.org/get",
                Response.Listener<String> { response ->
                    run {
                        var position = response.indexOf("execution=")
                        // TODO: check for -1 in position
                        var eAndS = response.substring(position + 11, position + 15)
                        executionCount = eAndS[0].toString().toInt()
                        versionCount = eAndS[2].toString().toInt()
                    }
                },
                Response.ErrorListener { })
            queue.add(formReq)
        } catch (e: Exception) {
            // Possibility 2: Users already signed in, look for ticket in headers and body
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

    // Helper to extract ticket from json body
    private fun findTicket(response:JSONObject){
        try {
            var args = response.getJSONObject("args")
            currentTicket = args["ticket"] as String
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    // After we get the form, we need to POST with execution info to get service ticket
    private fun obtainTicketAndValidate() {

        if (!validated) {

            // if no ticket, then send POST to get it
            if (currentTicket == "") {
                try {
                    val signInReq = object : StringRequest(
                        Request.Method.POST,
                        "$loginFormLink?execution=" + "e" + executionCount.toString() + "s" + versionCount.toString(),
                        Response.Listener<String> { response ->
                            run {
                                try {
                                    findTicket(JSONObject(response))
                                    if (currentTicket != "") {
                                        validateTicket()
                                    }

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        },
                        Response.ErrorListener { }) {
                        override fun getBodyContentType(): String? {
                            var map: Map<String, String> =
                                hashMapOf("Content-Type" to "application/x-www-form-urlencoded")
                            return "application/x-www-form-urlencoded"
                        }

                        override fun getParams(): MutableMap<String, String>? {
                            val netID =
                                findViewById<EditText>(R.id.editTextNetID).text.toString().trim()
                            val pwd = findViewById<EditText>(R.id.editTextPassword).text.toString()
                            return hashMapOf(
                                "j_username" to netID,
                                "j_password" to pwd, "_eventId_proceed" to ""
                            )
                        }

//                        override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
//                            try {
//                                var ticketStr = response!!.headers.get("Location")
//                                var ticket = ticketStr!!.substringAfter("ticket=")
//                                if (ticket.length > 1)
//                                    currentTicket = ticket
//                            } catch (e: UnsupportedEncodingException) {
//                                Response.error<JSONObject>(ParseError(e))
//                            } catch (je: JSONException) {
//                                Response.error<JSONObject>(ParseError(je))
//                            } catch (e:Exception) {
//                                return super.parseNetworkResponse(response)
//                            }
//                            return super.parseNetworkResponse(response)
//                        }
                    }
                    queue.add(signInReq)

                    // TODO: If this fails we probably need to reload the form again, and grab new execution param
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                // If we have a ticket, validate with CAS serviceValidate endpoint
            } else {
               validateTicket()
            }
        }
    }

    private fun validateTicket() {
        val validateReq = object : StringRequest(
            Request.Method.GET,
            "$validateLink&ticket=$currentTicket",
            Response.Listener<String> { response ->
                run {
                    if (response.contains("student") || response.contains("success")) // TODO: make this better
                        validated = true
                }
            },
            Response.ErrorListener { }) {
            override fun getBodyContentType(): String? {
                return "text/html;charset=utf-8"
            }
        }
        queue.add(validateReq)
    }

}