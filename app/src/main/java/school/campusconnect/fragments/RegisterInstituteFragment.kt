package school.campusconnect.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.vivid.gruppie.R
import com.vivid.gruppie.interfaces.RegisterCallback
import com.vivid.gruppie.model.ClassInputData
import com.vivid.gruppie.model.ClassItem
import com.vivid.gruppie.model.RegisterRequestData
import com.vivid.gruppie.model.UniversityItem
import com.vivid.gruppie.view.RegisterClassSectionAdapter
import com.vivid.gruppie.view.RegisterUniversityAdapter
import school.campusconnect.BuildConfig
import school.campusconnect.activities.LoginPinActivity
import school.campusconnect.database.LeafPreference
import school.campusconnect.datamodel.BaseResponse
import school.campusconnect.datamodel.LoginResponse
import school.campusconnect.datamodel.register.*
import school.campusconnect.network.LeafManager
import school.campusconnect.network.LeafManager.OnCommunicationListener
import school.campusconnect.utils.AppLog
import school.campusconnect.utils.BaseFragment
import java.text.SimpleDateFormat
import java.util.*

open class RegisterInstituteFragment : BaseFragment(), OnCommunicationListener {

    var b = null
    //region Lifecycle
    var isLogout: Boolean = false
    var cleverTap: CleverTapAPI? = null
    var leafManager = LeafManager()

    val mListCategories = ArrayList<String>()
    val mListMediums = ArrayList<String>()
    val mListBoards = ArrayList<String>()

    val mListUniversities = ArrayList<UniversityItem>()
    val mListClasses = ArrayList<ClassItem>()

    var selectedCategory: String? = null
    var selectedBoard: String? = null
    var selectedUniversity: String? = null
    var selectedMedium: String = ""

    val strYear = ArrayList<String>()
    var mStartYear: String = ""
    var mEndYear: String = ""

    val classTypes = ArrayList<String>()
    val classSections = HashMap<String, Int>()

    val sectionsMap = HashMap<String, Int>()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.register_institute, container, false)
        setupUI(view)
      //  isLogout = arguments?.getBoolean("isDashboard",false)!!
        return view
    }


    companion object {

        fun newInstance(bundle: Bundle?): RegisterInstituteFragment {
            val fragment = RegisterInstituteFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            setupActions(it)
        }
    }

    override fun onStart() {
        super.onStart()


        apiGetCategories()
        apiGetCampusMedium()
        /*val universityItem1 = UniversityItem(name = "Karnataka State Board", image = "https://gruppiemedia.sgp1.cdn.digitaloceanspaces.com/university.jpeg")
        val universityItem2 = UniversityItem(name = "Tamil State Board", image = "https://gruppiemedia.sgp1.cdn.digitaloceanspaces.com/university.jpeg")

        val universities = ArrayList<UniversityItem>()
        universities.add(universityItem1)
        universities.add(universityItem2)
        mListUniversities.addAll(universities)

        showStep2()*/
    }

    //endregion

    //region BEGIN UI

    lateinit var ivStep1: ImageView
    lateinit var tvStep1: TextView
    lateinit var clStep1: ScrollView
    lateinit var progressBar : ProgressBar
    lateinit var btnNext1: Button

    lateinit var ivStep2: ImageView
    lateinit var tvStep2: TextView
    lateinit var clStep2: ScrollView
    lateinit var btnNext2: Button

    lateinit var ivStep3: ImageView
    lateinit var tvStep3: TextView
    lateinit var clStep3: ConstraintLayout
    lateinit var btnNext3: Button

    lateinit var etName: TextInputEditText
    lateinit var tilName: TextInputLayout
    lateinit var spCategory: Spinner
    lateinit var spBoard: Spinner
    lateinit var spMedium: Spinner
    lateinit var spYear: Spinner

    lateinit var rvUniversities: RecyclerView
    lateinit var rvClasses: RecyclerView

    private fun setupUI(view: View) {
        ivStep1 = view.findViewById(R.id.ivStep1)
        tvStep1 = view.findViewById(R.id.tvStep1)
        clStep1 = view.findViewById(R.id.slStep1)
        btnNext1 = view.findViewById(R.id.btnNext1)

        progressBar = view.findViewById(R.id.progressBar)
        ivStep2 = view.findViewById(R.id.ivStep2)
        tvStep2 = view.findViewById(R.id.tvStep2)
        clStep2 = view.findViewById(R.id.slStep2)
        btnNext2 = view.findViewById(R.id.btnNext2)

        ivStep3 = view.findViewById(R.id.ivStep3)
        tvStep3 = view.findViewById(R.id.tvStep3)
        clStep3 = view.findViewById(R.id.clStep3)
        btnNext3 = view.findViewById(R.id.btnNext3)

        etName = view.findViewById(R.id.etName)
        tilName = view.findViewById(R.id.tilName)

        spCategory = view.findViewById(R.id.spinnerCategory)
        spBoard = view.findViewById(R.id.spinnerBoard)
        spMedium = view.findViewById(R.id.spinnerMedium)
        spYear = view.findViewById(R.id.spinnerYear)

        rvUniversities = view.findViewById(R.id.rvUniversities)
        rvClasses = view.findViewById(R.id.rvClasses)

        etName.requestFocus()

        if(arguments != null)
        {
            isLogout = arguments!!.getBoolean("isDashboard", false)
            AppLog.e("RegisterInstituteFragment", "isLogout " + isLogout)
        }

    }

    private fun isStep1Completed(): Boolean {
        if (etName.text.toString().isNullOrEmpty()) {
            tilName.error = "Enter Institution Name"
            etName.requestFocus()
            return false
        }

        if (spCategory.selectedItemPosition == 0) {
            Toast.makeText(activity, "Please Select Category", Toast.LENGTH_SHORT).show()
            return false
        }

        if (spMedium.selectedItemPosition == 0) {
            Toast.makeText(activity, "Please Select Medium", Toast.LENGTH_SHORT).show()
            return false
        }

        if (spBoard.selectedItemPosition == 0) {
            Toast.makeText(activity, "Please Select Board", Toast.LENGTH_SHORT).show()
            return false
        }

        if (spYear.selectedItemPosition == 0) {
            Toast.makeText(activity, "Please Select Academic Year", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun showStep1() {


        clStep1.visibility = View.VISIBLE
        clStep2.visibility = View.GONE
        clStep3.visibility = View.GONE

        activity?.let {
            tvStep2.setTextColor(resources.getColor(R.color.colorTextLight))
            ivStep2.setImageDrawable(it.getDrawable(R.drawable.ic_step_2_inactive))

            tvStep3.setTextColor(resources.getColor(R.color.colorTextLight))
            ivStep3.setImageDrawable(it.getDrawable(R.drawable.ic_step_3_inactive))
        }
        closeKeyboard()
    }

    private fun showStep2() {
        clStep1.visibility = View.GONE
        clStep2.visibility = View.VISIBLE
        clStep3.visibility = View.GONE

        activity?.let {
            tvStep2.setTextColor(resources.getColor(R.color.colorPrimary))
            ivStep2.setImageDrawable(it.getDrawable(R.drawable.ic_step_2))

            tvStep3.setTextColor(resources.getColor(R.color.colorTextLight))
            ivStep3.setImageDrawable(it.getDrawable(R.drawable.ic_step_3_inactive))
        }

        closeKeyboard()
    }

    private fun showStep3() {
        clStep1.visibility = View.GONE
        clStep2.visibility = View.GONE
        clStep3.visibility = View.VISIBLE

        activity?.let {
            tvStep3.setTextColor(resources.getColor(R.color.colorPrimary))
            ivStep3.setImageDrawable(it.getDrawable(R.drawable.ic_step_3))
        }

        closeKeyboard()
    }

    //endregion END UI

    //region API Callbacks

    override fun onSuccess(apiId: Int, response: BaseResponse?) {
        if (activity == null) return

        when (apiId) {
            LeafManager.API_ID_TYPE_OF_CAMPUS -> onResponseCategory(response)
            LeafManager.API_ID_GET_CAMPUS_MEDIUM -> onResponseMedium(response)
            LeafManager.API_ID_GET_BOARDS_LIST -> onResponseBoards(response)
            LeafManager.API_ID_GET_UNIVERSITY_LIST -> onResponseUniversities(response)
            LeafManager.API_ID_GET_CLASSES_LIST -> onResponseGetClasses(response)
            LeafManager.API_ID_DO_REGISTER -> onRegistrationSuccess()
        }
    }

    override fun onFailure(apiId: Int, msg: String?) {
        hideLoadingBar()
        Toast.makeText(activity, msg + "", Toast.LENGTH_SHORT).show()
    }

    override fun onException(apiId: Int, msg: String?) {
        hideLoadingBar()
    }

    //endregion

    //region API Request

    private fun apiGetCategories() {
        leafManager.getTypeOfCampus(this)
    }

    private fun onResponseCategory(response: BaseResponse?) {
        val result = response as TypeOfCampusData
        result.data?.let { types ->

            mListCategories.clear()
            mListCategories.add("- select -")

            for (data in types) {
                data.typeOfCampus?.let { campusTypes ->
                    for (campus in campusTypes) {
                        mListCategories.add(campus)
                    }
                }
            }
        }

        activity?.let {
            val categoryAdapter: ArrayAdapter<String> = ArrayAdapter<String>(it, R.layout.spinner_text_with_border, mListCategories)
            spCategory.adapter = categoryAdapter
        }
    }

    private fun apiGetCampusMedium() {
        leafManager.getCampusMedium(this)
    }

    private fun onResponseMedium(response: BaseResponse?) {
        val result = response as CampusMediumData
        result.data?.let { types ->

            mListMediums.clear()
            mListMediums.add("- select -")

            for (data in types) {
                data.medium?.let { mediums ->
                    for (medium in mediums) {
                        mListMediums.add(medium)
                    }
                }
            }
        }

        activity?.let {
            val mediumAdapter: ArrayAdapter<String> = ArrayAdapter<String>(it, R.layout.spinner_text_with_border, mListMediums)
            spMedium.adapter = mediumAdapter
        }
    }

    private fun apiGetBoards(category: String) {
        leafManager.getBoardsListForCampus(category, this)
    }

    private fun onResponseBoards(response: BaseResponse?) {
        val result = response as BoardsData
        result.data?.let { types ->

            mListBoards.clear()
            mListBoards.add("- select -")

            for (data in types) {
                data.boards?.let { boards ->
                    for (board in boards) {
                        mListBoards.add(board)
                    }
                }
            }
        }

        activity?.let {
            val boardAdapter: ArrayAdapter<String> = ArrayAdapter<String>(it, R.layout.spinner_text_with_border, mListBoards)
            spBoard.adapter = boardAdapter
        }
    }

    private fun apiGetUniversities(board: String) {
        leafManager.getUniversitiesListForBoard(board, this)
    }

    private fun onResponseUniversities(response: BaseResponse?) {
        val result = response as UniversitiesData
        result.data?.let { types ->

            mListUniversities.clear()

            for (data in types) {
                data.university?.let { boardUniversities ->
                    for (university in boardUniversities) {
                        mListUniversities.add(university)
                    }
                }
            }
        }

        rvUniversities.adapter = RegisterUniversityAdapter(mListUniversities, object :
                RegisterCallback {
            override fun onUniversityClicked(universityName: String) {
                selectedUniversity = universityName
                showStep3()
                Toast.makeText(requireActivity(), "$selectedUniversity selected", Toast.LENGTH_SHORT).show()
            }

            override fun onCheckBoxChanged(typeID: String, isSelected: Boolean) {}
            override fun onCountChanged(key: String, value: Int) {}
        })
    }

    private fun apiGetClasses(category: String, board: String) {
        leafManager.getClassesList(category, board, this)
    }

    private fun onResponseGetClasses(response: BaseResponse?) {
        val result = response as ClassesListData
        result.data?.let { types ->
            mListClasses.clear()
            sectionsMap.clear()

            for (data in types) {
                data.classes?.let { classes ->
                    mListClasses.addAll(classes)
                }
            }
            for (classItemName in mListClasses) {
                classItemName._class?.let { cls ->
                    for (section in cls) {
                        sectionsMap[section] = 0
                    }
                }
            }
        }

        rvClasses.adapter = RegisterClassSectionAdapter(mListClasses, object : RegisterCallback {
            override fun onUniversityClicked(universityName: String) {}
            override fun onCheckBoxChanged(typeID: String, isSelected: Boolean) {
                if (isSelected) {
                    if (!classTypes.contains(typeID)) {
                        classTypes.add(typeID)
                    }
                } else {
                    if (classTypes.contains(typeID)) {
                        classTypes.remove(typeID)
                    }
                }
            }

            override fun onCountChanged(key: String, value: Int) {
                sectionsMap[key] = value
            }
        })
    }

    private fun apiDoRegister(userId: String, registerRequest: RegisterRequestData) {

        showLoadingBar(progressBar,true)
        Log.e("REgister","req "+Gson().toJson(registerRequest))
        leafManager.doRegister(userId, registerRequest, this)
    }

    //endregion API Request

    private fun setupActions(activity: Activity) {

        showStep1()

        mListBoards.clear()
        mListBoards.add("- select -")
        val boardAdapter: ArrayAdapter<String> = ArrayAdapter<String>(activity, R.layout.spinner_text_with_border, mListBoards)
        spBoard.adapter = boardAdapter

        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tilName.error = null
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        spCategory.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    mListCategories[position].let {
                        apiGetBoards(it)
                        selectedCategory = it
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spMedium.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    mListMediums[position].let {
                        selectedMedium = it
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spBoard.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    mListBoards[position].let {
                        apiGetUniversities(it)
                        selectedBoard = it

                        selectedCategory?.let { category ->
                            selectedBoard?.let { board ->
                                apiGetClasses(category, board)
                            }
                        }
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val year = SimpleDateFormat("yyyy", Locale.ENGLISH).format(Calendar.getInstance().time)
        val thisYear = Integer.parseInt(year)
        strYear.add("- select -")
        strYear.add("${(thisYear + 0)} - ${(thisYear + 1)}")
        strYear.add("${(thisYear + 1)} - ${(thisYear + 2)}")
        strYear.add("${(thisYear + 2)} - ${(thisYear + 3)}")
        strYear.add("${(thisYear + 3)} - ${(thisYear + 4)}")
        strYear.add("${(thisYear + 4)} - ${(thisYear + 5)}")
        val yearAdapter: ArrayAdapter<String> = ArrayAdapter<String>(activity, R.layout.spinner_text_with_border, strYear)
        spYear.adapter = yearAdapter

        spYear.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    val splitYear = strYear[position].split(" - ")
                    mStartYear = splitYear[0].replace(" - ", "")
                    mEndYear = splitYear[1].replace(" - ", "")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        btnNext1.setOnClickListener {
            if (isStep1Completed()) {
                showStep2()
            }
        }
        tvStep1.setOnClickListener { showStep1() }
        ivStep1.setOnClickListener { showStep1() }

        btnNext2.setOnClickListener { showStep3() }
        tvStep2.setOnClickListener { if (clStep2.visibility == View.VISIBLE  || clStep3.visibility == View.VISIBLE) showStep2() }
        ivStep2.setOnClickListener { if (clStep2.visibility == View.VISIBLE || clStep3.visibility == View.VISIBLE) showStep2() }

        btnNext3.setOnClickListener {
            classSections.clear()
            val keys = sectionsMap.keys
            for (key in keys) {
                classSections[key] = sectionsMap[key] ?: 0
            }

            selectedCategory?.let { category ->
                selectedBoard?.let { board ->
                    selectedUniversity?.let { university ->

                        val sections = ArrayList<ClassInputData>()
                        for (name in classSections.keys) {
                            val noOfSections = classSections[name]
                            noOfSections?.let {
                                if (it > 0) {
                                    sections.add(ClassInputData(name, noOfSections))
                                }
                            }
                        }

                        val registerRequest = RegisterRequestData(
                                appName = BuildConfig.AppName, name = etName.text.toString(), subCategory = category, board = board,
                                university = university, medium = selectedMedium,
                                classTypeId = classTypes, classSection = sections, academicStartYear = mStartYear,
                                academicEndYear = mEndYear
                        )
                        apiDoRegister(
                                LeafPreference.getInstance(requireActivity()).getString(LeafPreference.LOGIN_ID),
                                registerRequest
                        )
                    }
                }
            }
        }
    }

    private fun onRegistrationSuccess() {


        activity?.let {

            if (isLogout)
            {

                LeafPreference.getInstance(activity).setBoolean("group_list_refresh", true)
                activity?.finish()

               /* val skip_pin = LeafPreference.getInstance(activity).getString(LeafPreference.SKIP_PIN)
                val fingerPrint = LeafPreference.getInstance(activity).getBoolean(LeafPreference.FINGERPRINT)
                val pin = LeafPreference.getInstance(activity).getString(LeafPreference.PIN)

                val request = Gson().fromJson<LoginRequest>(LeafPreference.getInstance(context).getString(LeafPreference.LOGIN_REQ), object : TypeToken<LoginRequest?>() {}.type)

                AppLog.e("RegisterInstitute", "request : " + Gson().toJson(request))


                val manager = LeafManager()

                showLoadingDialog(getString(school.campusconnect.R.string.please_wait))

                manager.doLogin(object : OnCommunicationListener {
                    override fun onSuccess(apiId: Int, response: BaseResponse) {
                        hideLoadingDialog()

                        logoutForNewSchool()

                        val response1 = response as LoginResponse
                        LeafPreference.getInstance(activity).setString(LeafPreference.SKIP_PIN, skip_pin)
                        LeafPreference.getInstance(activity).setBoolean(LeafPreference.FINGERPRINT, fingerPrint)
                        LeafPreference.getInstance(activity).setString(LeafPreference.PIN, pin)
                        LeafPreference.getInstance(activity).setString(LeafPreference.TOKEN, response1.token)
                        LeafPreference.getInstance(activity).setString(LeafPreference.GROUP_ID, response1.groupId)
                        LeafPreference.getInstance(activity).setInt(LeafPreference.GROUP_COUNT, response1.groupCount)
                        LeafPreference.getInstance(activity).setString(LeafPreference.LOGIN_ID, response1.userId)
                        LeafPreference.getInstance(activity).setString(LeafPreference.ROLE, response1.role)
                        // LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.TOKEN, response1.token);
                        LeafPreference.getInstance(activity).setString(LeafPreference.NAME, response1.name)
                        LeafPreference.getInstance(activity).setString(LeafPreference.NUM, response1.phone)
                        LeafPreference.getInstance(activity).setString(LeafPreference.PROFILE_IMAGE_NEW, response1.image)
                        LeafPreference.getInstance(activity).setString(LeafPreference.PROFILE_NAME, response1.name)
                        LeafPreference.getInstance(activity).setString(LeafPreference.PROFILE_VOTERID, response1.voterId)
                        LeafPreference.getInstance(activity).setString(LeafPreference.CALLING_CODE, response1.counryTelephoneCode)
                        LeafPreference.getInstance(activity).setString(LeafPreference.COUNTRY_CODE, response1.countryAlpha2Code)
                        addCleverTapProfile(response1)


                        val login = Intent(activity, Home::class.java)
                        login.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(login)
                    }

                    override fun onFailure(apiId: Int, msg: String) {
                        Toast.makeText(activity, msg + "", Toast.LENGTH_SHORT).show()
                        hideLoadingDialog()
                    }

                    override fun onException(apiId: Int, msg: String) {
                        Toast.makeText(activity, msg + "", Toast.LENGTH_SHORT).show()
                        hideLoadingDialog()
                    }
                }, request)
                logoutForNewSchool()*/
            }
            else
            {

                var value = LeafPreference.getInstance(requireActivity()).getInt(LeafPreference.GROUP_COUNT)+1;
                LeafPreference.getInstance(activity).setInt(LeafPreference.GROUP_COUNT, value)

                AppLog.e(TAG,"Role "+LeafPreference.getInstance(requireActivity()).getString(LeafPreference.ROLE))
                AppLog.e(TAG,"token "+LeafPreference.getInstance(requireActivity()).getString(LeafPreference.TOKEN))
                AppLog.e(TAG,"groupCount "+value)
                AppLog.e(TAG,"groupID "+LeafPreference.getInstance(requireActivity()).getString(LeafPreference.GROUP_ID))

                val i = Intent(it, LoginPinActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                i.putExtra("Role",
                        LeafPreference.getInstance(requireActivity()).getString(LeafPreference.ROLE))
                i.putExtra("token",
                        LeafPreference.getInstance(requireActivity()).getString(LeafPreference.TOKEN))
                i.putExtra("groupCount",
                        LeafPreference.getInstance(requireActivity()).getInt(LeafPreference.GROUP_COUNT).toString())
                i.putExtra("groupID",
                        LeafPreference.getInstance(requireActivity()).getString(LeafPreference.GROUP_ID))

                it.startActivity(i)
                it.finish()
            }

        }
    }

    private fun closeKeyboard() {

    }
    open fun initObjects() {
        try {
            cleverTap = CleverTapAPI.getInstance(activity)
            AppLog.e(TAG, "Success to found cleverTap objects=>")
        } catch (e: CleverTapMetaDataNotFoundException) {
            AppLog.e(TAG, "CleverTapMetaDataNotFoundException=>$e")
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (e: CleverTapPermissionsNotSatisfied) {
            AppLog.e(TAG, "CleverTapPermissionsNotSatisfied=>$e")
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
    }

    open fun addCleverTapProfile(loginResponse: LoginResponse) {
        initObjects()
        val profileUpdate = java.util.HashMap<String, Any>()
        profileUpdate["Identity"] = loginResponse.userId // String or phone
        profileUpdate["Name"] = loginResponse.name // String
        profileUpdate["Phone"] = loginResponse.phone
        profileUpdate["Photo"] = loginResponse.image // Phone (with the countryCode code, starting with +)
        if (cleverTap != null) {
            cleverTap!!.profile.push(profileUpdate)
            AppLog.e(TAG, "CleverTap profile added.")
            profileUpdate.remove("Photo")
            cleverTap!!.event.push("Login", profileUpdate)
            AppLog.e(TAG, "CleverTap Login added.")
        } else {
            AppLog.e(TAG, "CleverTap Profile & login not added.")
        }
    }
}
