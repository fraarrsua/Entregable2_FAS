package us.master.entregable1;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import us.master.entregable1.entity.Trip;

public class FirebaseDatabaseService {
    private static String userId;
    private static FirebaseDatabaseService service;
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabaseService getServiceInstance() {
        if (service == null || mDatabase == null) {
            service = new FirebaseDatabaseService();
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        if (userId == null || userId.isEmpty()) {
            userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        }
        return service;
    }

    public DatabaseReference getTrip(String triplId) {
        return mDatabase.getReference("user/" + userId + "/trip/" + triplId).getRef();
    }

    public DatabaseReference getTripPrecio(String triplId) {
        return mDatabase.getReference("user/" + userId + "/trip/" + triplId + "/precio").getRef();
    }

    public DatabaseReference getTrip() {
        return mDatabase.getReference("user/" + userId + "/trip/").getRef();
    }


    public void saveTrip(Trip trip, DatabaseReference.CompletionListener completionListener) {
       mDatabase.getReference("user/" + userId +"/trip").push().setValue(trip, completionListener);
    }


}

