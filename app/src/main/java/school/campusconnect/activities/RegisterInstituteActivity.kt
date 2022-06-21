package school.campusconnect.activities

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import school.campusconnect.R
import school.campusconnect.fragments.RegisterInstituteFragment
import school.campusconnect.utils.AppLog

class RegisterInstituteActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_institute)

        val mToolBar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(mToolBar)
        setBackEnabled(true)
        setTitle(resources.getString(R.string.register))


        val registerFragment = RegisterInstituteFragment.newInstance(intent.extras)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, registerFragment)
            .commit()
        //registerFragment.isLogout = intent.getBooleanExtra("isDashboard",false);
    }
}