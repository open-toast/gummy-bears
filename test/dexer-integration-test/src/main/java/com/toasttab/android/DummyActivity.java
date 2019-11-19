package com.toasttab.android;

import android.app.Activity;
import android.os.Bundle;
import com.toasttab.android.stub.java_lang_Boolean;

public class DummyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // just making sure the stubs are packaged
        new java_lang_Boolean() { }.hashCode(true);

        super.onCreate(savedInstanceState);
    }
}
