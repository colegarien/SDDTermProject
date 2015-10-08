package edu.uco.schambers.classmate.Adapter;

import org.json.JSONException;

/**
 * Created by calitova on 9/22/2015.
 */
public interface Callback<T> {
    void onComplete(T result) throws Exception;
}
