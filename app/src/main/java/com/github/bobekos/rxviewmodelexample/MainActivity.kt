package com.github.bobekos.rxviewmodelexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.github.bobekos.rxviewmodel.nonNullObserver
import com.github.bobekos.rxviewmodelexample.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val viewModel by inject<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.loadUser().nonNullObserver(this, observer = {
            showToast("I'm observing ${it.username}")
        })

        inserBtn.setOnClickListener {
            viewModel.insert(1, "Bobekos").run(
                    {
                        showToast("User inserted")
                    },
                    {
                        showToast(it.message ?: "error from completable")
                    })
        }

        loadSingleBtn.setOnClickListener {
            viewModel.getFromSingle(1).get(
                    {
                        showToast("User ${it.username} loaded")
                    },
                    {
                        showToast(it.message ?: "error from single")
                    })
        }

        loadMaybeBtn.setOnClickListener {
            viewModel.getFromMaybe(1).get(
                    {
                        showToast("User ${it.username} loaded")
                    },
                    {
                        showToast(it.message ?: "error from maybe")
                    },
                    {
                        showToast("No user found")
                    }
            )
        }

        updateBtn.setOnClickListener {
            viewModel.update(1, "NEW BOBEKOS!!!").run()
        }

        deleteBtn.setOnClickListener {
            viewModel.delete(1, "Bobekos").run(
                    {
                        showToast("User deleted")
                    },
                    {
                        showToast(it.message ?: "User delete error")
                    })
        }
    }

    private fun showToast(content: String) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
    }
}

