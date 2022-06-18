package school.campusconnect.datamodel.register

import com.vivid.gruppie.model.UniversityList
import school.campusconnect.datamodel.BaseResponse

data class UniversitiesData(
    val data: ArrayList<UniversityList>? = null,
) : BaseResponse()
