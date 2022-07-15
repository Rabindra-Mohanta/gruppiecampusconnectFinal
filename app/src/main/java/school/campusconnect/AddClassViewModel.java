package school.campusconnect;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import school.campusconnect.datamodel.student.StudentRes;

public  class AddClassViewModel extends ViewModel {


    public MutableLiveData<StudentRes.StudentData> studentDataMutableLiveData=new MutableLiveData<StudentRes.StudentData>(null);

public void setData(MutableLiveData<StudentRes.StudentData>  studentDataMutableLiveData)
{

    this.studentDataMutableLiveData=studentDataMutableLiveData;
}



}
