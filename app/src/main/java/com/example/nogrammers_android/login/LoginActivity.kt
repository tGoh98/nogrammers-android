package com.example.nogrammers_android.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nogrammers_android.MainActivity
import com.example.nogrammers_android.R
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.CookieHandler
import java.net.CookieManager

const val NETID_MESSAGE = "com.example.nogrammers_android.login.NETID_MESSAGE"


/**
 * Login screen
 */
class LoginActivity : AppCompatActivity() {
    private val loginFormLink = "https://idp.rice.edu/idp/profile/cas/login"
    private val validateLink = "https://idp.rice.edu/idp/profile/cas/serviceValidate?service=https://httpbin.org/get"
    private var executionCount = 1 // e num: increases when u refresh
    private var versionCount = 1 // s num: increases when u enter pwd wrong
    private var currentTicket: String = ""
    private var validated = false
    private lateinit var queue: RequestQueue
    private var set = false
    private lateinit var moveOnIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {

        CookieHandler.setDefault(CookieManager()) // supposedly lets session cookies persist s

        queue = Volley.newRequestQueue(this)
        moveOnIntent = Intent(this, MainActivity::class.java)

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
                var intent = moveOnIntent.apply {
                    putExtra(NETID_MESSAGE, netID)
                }
                // Try to obtain a ticket from CAS and validate user
                Toast.makeText(
                    applicationContext,
                    "Validating credentials ... ",
                    Toast.LENGTH_SHORT
                ).show()

                obtainTicketAndValidate(netID)
//                runBlocking { delay(5000) }
                val handler = Handler()
                handler.postDelayed(Runnable {
                    checkValidation(intent)
                }, 2300)

            }

        }
    }

    private fun checkValidation(intent: Intent){
        if (validated) {
            startActivity(intent) // lets user go through
            finish()
        } else {
            Toast.makeText(
                applicationContext,
                "Invalid credentials, please try again",
                Toast.LENGTH_SHORT
            ).show()
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
                        try {
                            var position = response.indexOf("execution=")
                            var eAndS = response.substring(position + 11, position + 15)
                            executionCount = eAndS[0].toString().toInt()
                            versionCount = eAndS[2].toString().toInt()
                        } catch (e:NumberFormatException) {
                            println("Requesting ticket from already logged in response")
                            var potentialTicket = response.substringAfter("ticket=", "")
                            if (potentialTicket.length > 1)
                                currentTicket = potentialTicket
                            else
                                findTicket(JSONObject(response))
                        }
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
    private fun obtainTicketAndValidate(netID: String) {

        if (!validated) {
            // if no ticket, then send POST that submits form to obtain it
            if (currentTicket == "") {
                try {
                    val signInReq = object : StringRequest(
                        Request.Method.POST,
                        "$loginFormLink?execution=" + "e" + executionCount.toString() + "s" + versionCount.toString(),
                        Response.Listener<String> { response ->
                            run {
                                System.out.println(response)
                                try {
                                    findTicket(JSONObject(response))
                                    if (currentTicket != "") {
                                        validateTicket(netID)
                                    }
                                } catch (e: Exception) {
                                    System.out.println("obtaining login form again because no ticket found in response")
                                    obtainLoginForm()
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

                    }
                    queue.add(signInReq)
                } catch (e: Exception) {
                    obtainLoginForm()
                    System.out.println("obtaining login form again because submitting the form with credentials failed")
                    Toast.makeText(
                        applicationContext,
                        "Invalid credentials, please try again!",
                        Toast.LENGTH_SHORT
                    ).show()
                    e.printStackTrace()
                }
                // If we have a ticket, validate with CAS serviceValidate endpoint
            } else {
               validateTicket(netID)
            }
        }
    }

    private fun validateTicket(netID: String) {
        System.out.printf("Validating Service Ticket with Rice CAS Authentication Service %s", currentTicket)

        /**TRYING asyncThread stuff that hasn't worked, for some reason, RequestFutures dont work, even on separate thread
//        var threadA = ThreadA(applicationContext, currentTicket, moveOnIntent, netID)
//        var a =  threadA.execute().get()
//        if (a == "true")
//            validated = true
//        else
//            obtainLoginForm()
//            println("obtaining login again 3")
//            Toast.makeText(
//                applicationContext,
//                "Invalid credentials, please try again!",
//                Toast.LENGTH_SHORT
//            ).show()
        **/

        val validateReq = StringRequest(
            Request.Method.GET,
            "$validateLink&ticket=$currentTicket",
            Response.Listener<String> { response ->
                run {
                    if (response.contains("student") || response.contains("Success") || response.contains(
                            "Affiliation"
                        )
                    ) {// TODO: make this better
                        validated = true
                        set = true
                        println("Validation success")
                    } else {
                        obtainLoginForm()
                        println("obtaining login again 3")
                        Toast.makeText(
                            applicationContext,
                            "Invalid credentials, please try again!",
                            Toast.LENGTH_SHORT
                        ).show()
                        set = true
                    }
                }
            }, Response.ErrorListener {  })
        queue.add(validateReq)

    }


    }

