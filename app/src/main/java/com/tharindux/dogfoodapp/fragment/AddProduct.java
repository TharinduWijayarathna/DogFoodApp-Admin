package com.tharindux.dogfoodapp.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import com.tharindux.dogfoodapp.R;


public class AddProduct extends Fragment {

    public int RESULT_OK = -1;

    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;

    EditText ProductPicName;
    Button button5;
    Uri ImageUri =null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_product, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public void onViewCreated(@NonNull View AddProductFragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(AddProductFragment, savedInstanceState);


        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();



        button5 = AddProductFragment.findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFile();
            }
        });



        Button button = AddProductFragment.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText Category = AddProductFragment.findViewById(R.id.editTextText);
                EditText ProductName = AddProductFragment.findViewById(R.id.editTextText2);
                EditText ProductPrice = AddProductFragment.findViewById(R.id.editTextText3);
                EditText ProductQty = AddProductFragment.findViewById(R.id.editTextText4);
                EditText ProductDescription = AddProductFragment.findViewById(R.id.editTextText5);
                ProductPicName = AddProductFragment.findViewById(R.id.editTextText6);


                if(Category.getText().length()>0){
                    if(ProductName.getText().length()>0){
                        if(ProductPrice.getText().length()>0){
                            if (ProductQty.getText().length()>0){
                                if(ProductDescription.getText().length()>0){
                                    if(ProductPicName.getText().length()>0) {
                                        if(ImageUri!=null){

                                            firebaseFirestore.collection("Products").document(Category.getText().toString())
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                            if (task.isSuccessful()) {
                                                                DocumentSnapshot document = task.getResult();
                                                                int done = 0;
                                                                if (document.getData() != null) {
                                                                    Map<String, Object> ProductAllDetails = document.getData();
                                                                    for (Map.Entry<String, Object> Products : ProductAllDetails.entrySet()) {
                                                                        if (Products.getKey().equals(ProductName.getText().toString())) {
                                                                            done = 1;
                                                                        }
                                                                    }
                                                                }
                                                                if(done==1){
                                                                    Toast.makeText(getActivity(), "This product name Already Exits", Toast.LENGTH_LONG).show();
                                                                }else {
                                                                    //productaddPert
                                                                    Map<String, Object> docData = new HashMap<>();
                                                                    Map<String, Object> nestedData = new HashMap<>();


                                                                    nestedData.put("Name", ProductName.getText().toString());
                                                                    nestedData.put("Price", ProductPrice.getText().toString());
                                                                    nestedData.put("Qty", ProductQty.getText().toString());
                                                                    nestedData.put("Description", ProductDescription.getText().toString());
                                                                    nestedData.put("PicName", ProductPicName.getText().toString()+".png");
                                                                    nestedData.put("Status", "Active");

                                                                    docData.put(ProductName.getText().toString(), nestedData);
                                                                    firebaseFirestore.collection("Products").document(Category.getText().toString())
                                                                            .set(docData, SetOptions.merge())
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    UploadFile(ImageUri);

//                                                                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new ProductViewFragment()).commit();
                                                                                }
                                                                            });
                                                                }
                                                            }

                                                        }
                                                    });
////////////////////////
                                        }else {
                                            Toast.makeText(getActivity(), "Please Enter the ImageFile", Toast.LENGTH_LONG).show();
                                        }
                                    }else {
                                        Toast.makeText(getActivity(), "Please Enter the Filename", Toast.LENGTH_LONG).show();
                                    }
                                }else {
                                    Toast.makeText(getActivity(), "Please Enter the Description", Toast.LENGTH_LONG).show();
                                }
                            }else {
                                Toast.makeText(getActivity(), "Please Enter the Qty", Toast.LENGTH_LONG).show();
                            }
                        }else {
                            Toast.makeText(getActivity(), "Please Enter the Price", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getActivity(), "Please Enter the Name", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getActivity(), "Please Enter the Category", Toast.LENGTH_LONG).show();
                }
            }
        });



    }

    private void selectFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select file"),1);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            ImageUri = selectedImageUri;
            button5.setText("Found File"+ requestCode);
//            UploadFile(ImageUri);
        }
    }

    private void UploadFile(Uri data){
        Toast.makeText(getActivity(), "Uploading image...", Toast.LENGTH_SHORT).show();

        StorageReference reference = storageReference.child("Products/"+ProductPicName.getText().toString()+".png");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getActivity(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new ProductViewFragment()).commit();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });

    }
}