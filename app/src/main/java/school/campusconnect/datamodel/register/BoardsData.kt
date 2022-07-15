package school.campusconnect.datamodel.register

import com.vivid.gruppie.model.BoardsList
import school.campusconnect.datamodel.BaseResponse

data class BoardsData(
    val data: ArrayList<BoardsList>? = null,
) : BaseResponse()
