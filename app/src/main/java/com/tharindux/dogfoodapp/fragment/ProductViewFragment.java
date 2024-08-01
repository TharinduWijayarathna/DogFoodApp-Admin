package com.tharindux.dogfoodapp.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import com.tharindux.dogfoodapp.R;
import com.tharindux.dogfoodapp.DownloadImageFromInternet;

public class ProductViewFragment extends Fragment {



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_view, container, false);
    }

    FirebaseFirestore firebaseFirestore ;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    @Override
    public void onViewCreated(@NonNull View ProductView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(ProductView, savedInstanceState);

        LinearLayout ProductViewFrame =  ProductView.findViewById(R.id.ProductViewFrame);
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        StorageReference storageRef = storage.getReference();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> categories = document.getData();
                                for (Map.Entry<String, Object> entry : categories.entrySet()) {
                                    Map<String, Object> productlist = (Map<String, Object>) entry.getValue();
                                    View view1 =  inflater.inflate(R.layout.product_view_layout, null);

                                    TextView textView8 = view1.findViewById(R.id.textView8);

                                    for (Map.Entry<String, Object> unite : productlist.entrySet()) {
                                        if (unite.getKey().equals("Name")) {

                                            textView8.setText(unite.getValue().toString());
                                        }

                                    }
                                    for (Map.Entry<String, Object> unite : productlist.entrySet()) {
                                        if (unite.getKey().equals("Price")) {
                                            TextView textView9 = view1.findViewById(R.id.textView9);
                                            textView9.setText("Rs."+unite.getValue().toString() + ".00");
                                        }
                                    }
                                    for (Map.Entry<String, Object> unite : productlist.entrySet()) {
                                        if (unite.getKey().equals("Qty")) {
                                            TextView textView6 = view1.findViewById(R.id.textView6);
                                            textView6.setText("Quantity: "+unite.getValue().toString());
                                        }
                                    }

                                    for (Map.Entry<String, Object> unite : productlist.entrySet()) {
                                        if (unite.getKey().equals("Status")) {
                                            TextView imageButton2 = view1.findViewById(R.id.button7);
                                            imageButton2.setText(unite.getValue().toString());
                                            imageButton2.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String status = null;
                                                    if(unite.getValue().toString().equals("Active")){
                                                        status = "Inactive";
                                                        imageButton2.setText(status);
                                                        Map<String, Object> Pds = new HashMap<>();
                                                        Map<String, Object> st = new HashMap<>();
                                                        st.put("Status",status);
                                                        Pds.put(entry.getKey().toString(),st);
                                                        firebaseFirestore.collection("Products").document(document.getId())
                                                                .set(Pds, SetOptions.merge());
                                                        System.out.println(unite.getValue().toString());
                                                    }
                                                    if (unite.getValue().toString().equals("Inactive")){

                                                        status = "Active";
                                                        imageButton2.setText(status);
                                                        Map<String, Object> Pds = new HashMap<>();
                                                        Map<String, Object> st = new HashMap<>();
                                                        st.put("Status",status);
                                                        Pds.put(entry.getKey().toString(),st);
                                                        firebaseFirestore.collection("Products").document(document.getId())
                                                                .set(Pds, SetOptions.merge());
                                                        System.out.println(unite.getValue().toString());
                                                    }


                                                }
                                            });
                                        }
                                    }
                                    for (Map.Entry<String, Object> unite : productlist.entrySet()) {
                                        if (unite.getKey().equals("PicName")) {
                                            storageRef.child("Products/"+unite.getValue().toString()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    new DownloadImageFromInternet((ImageView)
                                                            view1.findViewById(R.id.imageView4)).execute(uri.toString());
                                                }
                                            });

                                            Button button2 =view1.findViewById(R.id.button2);
                                            button2.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    Map<String, Object> Pds = new HashMap<>();

                                                    Pds.put(textView8.getText().toString(),  FieldValue.delete());
                                                    firebaseFirestore.collection("Products").document(document.getId())
                                                            .set(Pds, SetOptions.merge());

                                                    DeleteStroreg("Products/"+unite.getValue().toString());
                                                    ProductViewFrame.removeView(view1);
                                                }
                                            });
                                        }
                                    }

                                    ProductViewFrame.addView(view1);
                                }
                            }
                        }
                    }
                });


    }
    public void DeleteStroreg(String path){

        StorageReference storageRef = storage.getReference();
        StorageReference desertRef = storageRef.child(path);
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }
}