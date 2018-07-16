package com.github.bobekos.rxviewmodelexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.github.bobekos.rxviewmodel.subscribeCompletable
import com.github.bobekos.rxviewmodel.subscribeMaybe
import com.github.bobekos.rxviewmodel.nonNullObserver
import com.github.bobekos.rxviewmodel.subscribeSingle
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
            viewModel.insert(1, "Bobekos").subscribeCompletable(this,
                    onComplete = {
                        showToast("User inserted")
                    },
                    onError = {
                        showToast(it.message ?: "error from completable")
                    })
        }

        loadSingleBtn.setOnClickListener {
            viewModel.getFromSingle(1).subscribeSingle(this,
                    onSuccess = {
                        showToast("User ${it.username} loaded")
                    },
                    onError = {
                        showToast(it.message ?: "error from single")
                    })
        }

        loadMaybeBtn.setOnClickListener {
            viewModel.getFromMaybe(1).subscribeMaybe(this,
                    onSuccess = {
                        showToast("User ${it.username} loaded")
                    },
                    onError = {
                        showToast(it.message ?: "error from maybe")
                    },
                    onComplete = {
                        showToast("No user found")
                    })
        }

        updateBtn.setOnClickListener {
            viewModel.update(1, "NEW BOBEKOS!!!").subscribeCompletable(this)
        }

        deleteBtn.setOnClickListener {
            viewModel.delete(1, "Bobekos").subscribeCompletable(this,
                    onComplete = {
                        showToast("User deleted")
                    },
                    onError = {
                        showToast(it.message ?: "User delete error")
                    })
        }
    }

    private fun showToast(content: String) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
    }
}