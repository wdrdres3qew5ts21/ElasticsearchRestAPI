/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntt.siriraj.transform.dto;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author s.trakulmaiphol
 */
public class AssessmentList {
    
    private ArrayList<HashMap<String, String>> asessment;

    public ArrayList<HashMap<String, String>> getAsessment() {
        return asessment;
    }

    public void setAsessment(ArrayList<HashMap<String, String>> asessment) {
        this.asessment = asessment;
    }

    @Override
    public String toString() {
        return "AssessmentList{" + "asessment=" + asessment + '}';
    }
    
}
