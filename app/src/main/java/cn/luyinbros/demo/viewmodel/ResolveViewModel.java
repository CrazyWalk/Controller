package cn.luyinbros.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ResolveViewModel extends ViewModel {
    public final MutableLiveData<String> a = new MutableLiveData<>();
    private LiveData<String> b;

    public LiveData<String> getB() {
        return b;
    }


}
