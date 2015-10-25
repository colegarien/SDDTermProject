package edu.uco.schambers.classmate.AdapterModels;

/**
 * Created by calitova on 10/12/2015.
 */
public class SpinnerItem {
    private String spinnerText;
    private String value;

    public SpinnerItem(String spinnerText, String value) {
        this.spinnerText = spinnerText;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return spinnerText;
    }
}
