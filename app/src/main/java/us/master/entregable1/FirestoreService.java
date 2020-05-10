package us.master.entregable1;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import us.master.entregable1.entity.Trip;


public class FirestoreService {

    private static final String USERS_COLLECTION= "users";
    private static final String TRIPS_COLLECTION = "trips";

    public static String userId;
    private static FirebaseFirestore mDatabase;
    private static FirestoreService service;

    public static FirestoreService getServiceInstance() {
        if (service == null || mDatabase == null) {
            mDatabase = FirebaseFirestore.getInstance();
            service = new FirestoreService();
        }

        if (userId == null || userId.isEmpty()) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        }

        return service;
    }

    public void saveTrip(Trip trip, OnCompleteListener<DocumentReference> listener) {
        mDatabase.collection(USERS_COLLECTION).document(userId).collection(TRIPS_COLLECTION).add(trip).addOnCompleteListener(listener);
    }

    // Obtener la colección completa una única vez
    public void getTrips(OnCompleteListener<QuerySnapshot> querySnapshotOnCompleteListener) {
        mDatabase.collection(USERS_COLLECTION).document(userId).collection(TRIPS_COLLECTION).get().addOnCompleteListener(querySnapshotOnCompleteListener);
    }

    // Obtener la colección completa y mantener el canal abierto a través del Listener
    public ListenerRegistration getTrips(EventListener<QuerySnapshot> querySnapshotOnCompleteListener) {
        return mDatabase.collection(USERS_COLLECTION).document(userId).collection(TRIPS_COLLECTION).addSnapshotListener(querySnapshotOnCompleteListener);
    }

    public void getTripsFiltered(OnCompleteListener<QuerySnapshot> querySnapshotOnCompleteListener) {
        mDatabase.collection(USERS_COLLECTION).document(userId).collection(TRIPS_COLLECTION).limit(3).get().addOnCompleteListener(querySnapshotOnCompleteListener);
    }

    public void getTrip(String idTrip, EventListener<DocumentSnapshot> snapshotListener) {
        mDatabase.collection(USERS_COLLECTION).document(userId).collection(TRIPS_COLLECTION).document(idTrip).addSnapshotListener(snapshotListener);
    }
}