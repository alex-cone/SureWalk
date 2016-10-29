package Users;

import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by Sharmistha on 10/20/2016.
 */

public class profileFactory {

    ArrayList<String> ApprovedWalkers;
    ArrayList<SureWalkProfile> AllAccounts;

    public profileFactory(ArrayList<String> walkerList){
        this.ApprovedWalkers = walkerList;
    }

    public SureWalkProfile createProfile(String userType){
        if (userType == "Walker") {
            return new Walker();
        }
        else{
            return new Requester();
        }

    }

    public String getUserType(EditText email){
        String em = email.toString();
        if(ApprovedWalkers.contains(em)){
            return "Walker";
        }
        else{
            return "Requester";
        }
    }
}
