package com.example.phonenumbersapp

import android.Manifest.permission.READ_CONTACTS
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val permissionNumber = 1
    private var permissionsGiven: BooleanArray = BooleanArray(permissionNumber)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getPermissionToReadUserContacts();

        if (!permissionsGiven[READ_CONTACTS_PERMISSION_NUMBER]) {
            Toast
                .makeText(
                    this@MainActivity,
                    "Permission to use contacts wasn't given.",
                    Toast.LENGTH_SHORT
                )
                .show()
        } else {

            viewManager = LinearLayoutManager(this)
            viewAdapter = UserAdapter(fetchAllContacts()) {
                Toast
                    .makeText(
                        this@MainActivity,
                        "Clicked on user $it!",
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }

            recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }

    }

    data class Contact(val name: String, val phoneNumber: String)

    fun Context.fetchAllContacts(): List<Contact> {
        contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
            .use { cursor ->
                if (cursor == null) return emptyList()
                val builder = ArrayList<Contact>()
                while (cursor.moveToNext()) {
                    val name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                            ?: "N/A"
                    val phoneNumber =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            ?: "N/A"
                    Toast
                        .makeText(
                            this@MainActivity,
                            "Hallou dyadya!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    builder.add(Contact(name, phoneNumber))
                }

                return builder
            }
    }

//    val usersList = (0..30).map {
//        User("First name #$it", "Last name #$it")
//    }

    class UserViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
        val userNameText = root.user_name
        val userPhoneText = root.user_phone
    }

    class UserAdapter(
        val users: List<Contact>,
        val onClick: (Contact) -> Unit
    ) : RecyclerView.Adapter<UserViewHolder>() {
        override fun getItemCount(): Int = users.size
        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            holder.userNameText.text =
                users[position].name
            holder.userPhoneText.text =
                users[position].phoneNumber
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): UserViewHolder {
            val holder = UserViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.list_item,
                    parent,
                    false
                )
            )
            holder.root.setOnClickListener {
                onClick(users[holder.adapterPosition])
            }
            return holder
        }

    }

    private val READ_CONTACTS_PERMISSIONS_REQUEST = 1
    private val READ_CONTACTS_PERMISSION_NUMBER = 0

    fun getPermissionToReadUserContacts() {
        if (ContextCompat.checkSelfPermission(
                this,
                READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(
                arrayOf(READ_CONTACTS),
                READ_CONTACTS_PERMISSIONS_REQUEST
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show()
                permissionsGiven[READ_CONTACTS_PERMISSION_NUMBER] = true;
            } else {
                val showRationale =
                    shouldShowRequestPermissionRationale(READ_CONTACTS)

                if (showRationale) {
                } else {
                    Toast.makeText(this, "Read Contacts permission denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


}
