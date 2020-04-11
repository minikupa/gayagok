package com.gayagok.utils;

import java.util.ArrayList;

public interface ScheduleListener {

    void onLoadSuccess(ArrayList contents, ArrayList dates);

    void onLoadError();

}
