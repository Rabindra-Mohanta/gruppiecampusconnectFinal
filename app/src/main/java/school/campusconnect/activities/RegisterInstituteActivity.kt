package school.campusconnect.activities

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import school.campusconnect.R
import school.campusconnect.fragments.RegisterInstituteFragment

class RegisterInstituteActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_institute)

        val mToolBar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(mToolBar)
        setBackEnabled(true)
        setTitle(resources.getString(R.string.register))

        val registerFragment = RegisterInstituteFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, registerFragment)
            .commit()
    }
}