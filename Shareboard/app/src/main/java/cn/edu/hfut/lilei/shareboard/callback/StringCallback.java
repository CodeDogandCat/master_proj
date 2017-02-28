package cn.edu.hfut.lilei.shareboard.callback;

import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.convert.StringConvert;

import okhttp3.Response;

public abstract class StringCallback extends AbsCallback<String> {

    @Override
    public String convertSuccess(Response response) throws Exception {
        String s = StringConvert.create()
                .convertSuccess(response);
        response.close();
        return s;
    }
}
