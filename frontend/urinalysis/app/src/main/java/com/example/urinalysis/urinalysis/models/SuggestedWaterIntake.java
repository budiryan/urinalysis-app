package com.example.urinalysis.urinalysis.models;

public class SuggestedWaterIntake {
    private Float suggested_water_intake;
    private Float urine_color_difference;
    private String previous_test_date;
    private String previous_test_time;
    private String current_test_date;
    private String current_test_time;

    public SuggestedWaterIntake(Float suggested_water_intake, Float urine_color_difference, String previous_test_date, String previous_test_time, String current_test_date, String current_test_time) {
        this.suggested_water_intake = suggested_water_intake;
        this.urine_color_difference = urine_color_difference;
        this.previous_test_date = previous_test_date;
        this.previous_test_time = previous_test_time;
        this.current_test_date = current_test_date;
        this.current_test_time = current_test_time;
    }

    public Float getSuggestedWaterIntake() {
        return suggested_water_intake;
    }

    public Float getUrineColorDifference() {
        return urine_color_difference;
    }

    public String getPreviousTestDate() {
        return previous_test_date;
    }

    public String getPreviousTestTime() {
        return previous_test_time;
    }

    public String getCurrentTestDate() {
        return current_test_date;
    }

    public String getCurrentTestTime() {
        return current_test_time;
    }
}
