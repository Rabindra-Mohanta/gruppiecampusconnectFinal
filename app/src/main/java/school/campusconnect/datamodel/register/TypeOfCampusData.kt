package school.campusconnect.datamodel.register

import com.vivid.gruppie.model.TypeOfCampus
import school.campusconnect.datamodel.BaseResponse

data class TypeOfCampusData(
    val data: ArrayList<TypeOfCampus>? = null,
) : BaseResponse()
