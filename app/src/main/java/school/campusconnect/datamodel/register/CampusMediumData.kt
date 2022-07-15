package school.campusconnect.datamodel.register

import com.vivid.gruppie.model.CampusMedium
import school.campusconnect.datamodel.BaseResponse

data class CampusMediumData(
    val data: ArrayList<CampusMedium>? = null,
) : BaseResponse()
