package com.nexustech.articlesearch3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), ToolbarTitleListener {

    private lateinit var bottomNavigationView: BottomNavigationView

    // Store the fragment tag to avoid recreating fragments on orientation change
    private var currentFragmentTag: String = BookFragment::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        if (savedInstanceState == null) {
            setDefaultFragment()
        } else {
            currentFragmentTag = savedInstanceState.getString("currentFragmentTag", BookFragment::class.java.simpleName)
            // Restore fragment state
            supportFragmentManager.findFragmentByTag(currentFragmentTag)?.let {
                replaceFragment(it)
            }
        }
        setupBottomNavigation()
    }

    private fun initializeViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
    }

    private fun setDefaultFragment() {
        replaceFragment(BookFragment(), BookFragment::class.java.simpleName)
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.action_books -> BookFragment()
                R.id.action_articles -> ArticlesFragment()
                else -> null
            }
            selectedFragment?.let {
                replaceFragment(it, it::class.java.simpleName)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment, tag: String? = null) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, tag)
            .commit()
    }

    override fun updateToolbar(iconResId: Int, title: String) {
        val toolbarIcon = findViewById<ImageView>(R.id.toolbar_icon)
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        toolbarIcon.setImageResource(iconResId)
        toolbarTitle.text = title
    }

    // Save the current fragment tag
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("currentFragmentTag", currentFragmentTag)
    }
}
