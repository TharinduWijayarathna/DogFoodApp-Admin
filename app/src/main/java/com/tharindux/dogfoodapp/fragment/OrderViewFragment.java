package com.tharindux.dogfoodapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tharindux.dogfoodapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderViewFragment extends Fragment {

    private static final String TAG = "OrderViewFragment";
    private FirebaseFirestore firebaseFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View orderFragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(orderFragment, savedInstanceState);

        LinearLayout productViewFrame = orderFragment.findViewById(R.id.OrderViewFrame);
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean hasData = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "Document ID: " + document.getId());
                                Map<String, Object> userAll = document.getData();
                                for (Map.Entry<String, Object> paymentHistory : userAll.entrySet()) {
                                    Log.d(TAG, "Key: " + paymentHistory.getKey() + ", Value: " + paymentHistory.getValue());
                                    if (paymentHistory.getKey().equals("PaymentHistory")) {
                                        Map<String, Object> paymentHistoryAllDetails = (Map<String, Object>) paymentHistory.getValue();

                                        if (paymentHistoryAllDetails.entrySet().size() > 0) {
                                            hasData = true;
                                            for (Map.Entry<String, Object> historyDetails : paymentHistoryAllDetails.entrySet()) {
                                                Log.d(TAG, "History Key: " + historyDetails.getKey() + ", Value: " + historyDetails.getValue());
                                                View view1 = inflater.inflate(R.layout.order_history_view_layout, null);
                                                ArrayList<String> arrayList = new ArrayList<>();
                                                Map<String, Object> historyDetailsFields = (Map<String, Object>) historyDetails.getValue();

                                                for (Map.Entry<String, Object> productSet : historyDetailsFields.entrySet()) {
                                                    Log.d(TAG, "Product Key: " + productSet.getKey() + ", Value: " + productSet.getValue());
                                                    if (productSet.getKey().equals("product")) {
                                                        Map<String, Object> productFile = (Map<String, Object>) productSet.getValue();
                                                        for (Map.Entry<String, Object> productDetails : productFile.entrySet()) {
                                                            Map<String, Object> productFields = (Map<String, Object>) productDetails.getValue();

                                                            String name = null;
                                                            String qty = null;
                                                            String price = null;
                                                            int done = 0;

                                                            for (Map.Entry<String, Object> productFieldsDetails : productFields.entrySet()) {
                                                                Log.d(TAG, "Product Field Key: " + productFieldsDetails.getKey() + ", Value: " + productFieldsDetails.getValue());
                                                                if (productFieldsDetails.getKey().equals("Name")) {
                                                                    name = productFieldsDetails.getValue().toString();
                                                                    done++;
                                                                }
                                                                if (productFieldsDetails.getKey().equals("Qty")) {
                                                                    qty = productFieldsDetails.getValue().toString();
                                                                    done++;
                                                                }
                                                                if (productFieldsDetails.getKey().equals("Price")) {
                                                                    price = productFieldsDetails.getValue().toString();
                                                                    done++;
                                                                }
                                                                if (done == 3) {
                                                                    // Format the details into three lines
                                                                    String productInfo = "Name: " + name + "\nQty: " + qty + "\nPrice: " + "Rs." + DecimalFormat.getInstance().format(Double.parseDouble(price)) + ".00";
                                                                    arrayList.add(productInfo);
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if (productSet.getKey().equals("Total")) {
                                                        TextView textView31 = view1.findViewById(R.id.textView31);
                                                        textView31.setText("Rs." + DecimalFormat.getInstance().format(Double.parseDouble(productSet.getValue().toString())) + ".00");
                                                    }
                                                    if (productSet.getKey().equals("Status")) {
                                                        Button button3 = view1.findViewById(R.id.button3);
                                                        button3.setText(productSet.getValue().toString());
                                                        button3.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                button3.setText("Delivered");
                                                                Map<String, Object> us = new HashMap<>();
                                                                Map<String, Object> st = new HashMap<>();
                                                                Map<String, Object> pds = new HashMap<>();

                                                                pds.put("Status", "Delivered");
                                                                st.put(historyDetails.getKey(), pds);
                                                                us.put("PaymentHistory", st);
                                                                firebaseFirestore.collection("User").document(document.getId())
                                                                        .set(us, SetOptions.merge())
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Log.d(TAG, "Status updated successfully");
                                                                                } else {
                                                                                    Log.e(TAG, "Error updating status", task.getException());
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        });
                                                    }
                                                }

                                                Spinner spinner = view1.findViewById(R.id.spinner_languages);
                                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, arrayList);
                                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                spinner.setAdapter(adapter);

                                                TextView textView = view1.findViewById(R.id.textView35);
                                                textView.setText(historyDetails.getKey());
                                                productViewFrame.addView(view1);
                                            }
                                        }
                                    }
                                }
                            }

                            if (!hasData) {
                                Toast.makeText(getActivity(), "No payment history found", Toast.LENGTH_SHORT).show();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new ProductViewFragment()).commit();
                            }
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(getActivity(), "Error loading data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
