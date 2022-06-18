package school.campusconnect.datamodel.register

import com.vivid.gruppie.model.ClassesItem
import school.campusconnect.datamodel.BaseResponse

data class ClassesListData(
    val data: ArrayList<ClassesItem>? = null,
) : BaseResponse()
