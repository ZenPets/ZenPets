package co.zenpets.kennels.creator.inventory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.kennels.R;
import co.zenpets.kennels.utils.AppPrefs;
import co.zenpets.kennels.utils.TypefaceSpan;
import co.zenpets.kennels.utils.adapters.inventory.InventoryTypesAdapter;
import co.zenpets.kennels.utils.models.helpers.ZenApiClient;
import co.zenpets.kennels.utils.models.inventory.InventoriesAPI;
import co.zenpets.kennels.utils.models.inventory.Inventory;
import co.zenpets.kennels.utils.models.inventory.Type;
import co.zenpets.kennels.utils.models.inventory.Types;
import co.zenpets.kennels.utils.models.inventory.TypesAPI;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryCreator extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN KENNEL'S ID **/
    private String KENNEL_ID = null;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_STORAGE_CONSTANT = 201;

    /** THE INVENTORY ITEM DETAILS **/
    String INVENTORY_TYPE_ID = null;
    String KENNEL_INVENTORY_NAME = null;
    String KENNEL_INVENTORY_COST = null;
    String KENNEL_INVENTORY_STATUS = "Available";
    private Uri INVENTORY_PHOTO_URI = null;
    String INVENTORY_PHOTO_FILE_NAME = null;
    private String INVENTORY_PHOTO = null;

    /** AN INVENTORY TYPES ARRAY LIST **/
    ArrayList<Type> arrTypes = new ArrayList<>();

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog progressDialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.spnInventoryType) Spinner spnInventoryType;
    @BindView(R.id.inputInventoryName) TextInputLayout inputInventoryName;
    @BindView(R.id.edtInventoryName) TextInputEditText edtInventoryName;
    @BindView(R.id.inputInventoryCost) TextInputLayout inputInventoryCost;
    @BindView(R.id.edtInventoryCost) TextInputEditText edtInventoryCost;
    @BindView(R.id.imgvwKennelPhoto) ImageView imgvwKennelPhoto;

    /** SELECT THE INVENTORY PHOTO / LOGO **/
    @OnClick(R.id.imgvwKennelPhoto) void selectInventoryPhoto() {
        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_creator_new);
        ButterKnife.bind(this);

        /* THE EASY IMAGE CONFIGURATION */
        EasyImage.configuration(this)
                .setImagesFolderName("Zen Pets")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(true)
                .setAllowMultiplePickInGallery(false);

        /* GET THE KENNEL ID */
        KENNEL_ID = getApp().getKennelID();
//        Log.e("KENNEL ID", KENNEL_ID);
        if (KENNEL_ID != null)  {
            /* FETCH THE LIST OF INVENTORY TYPES */
            fetchInventoryTypes();
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Failed to get required info...",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* SELECT THE INVENTORY TYPE */
        spnInventoryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                INVENTORY_TYPE_ID = arrTypes.get(position).getInventoryTypeID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /** CHECK INVENTORY DETAILS **/
    private void checkInventoryDetails() {
        /* HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtInventoryName.getWindowToken(), 0);
        }

        /* COLLECT THE INVENTORY ITEM DETAILS */
        KENNEL_INVENTORY_NAME = edtInventoryName.getText().toString().trim();
        KENNEL_INVENTORY_COST = edtInventoryCost.getText().toString().trim();

        if (TextUtils.isEmpty(KENNEL_INVENTORY_NAME)) {
            inputInventoryName.setError("Provide a unique name for the inventory item");
            inputInventoryName.requestFocus();
            inputInventoryName.setErrorEnabled(true);
            inputInventoryCost.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_INVENTORY_COST)) {
            inputInventoryCost.setError("Provide the per day / night cost");
            inputInventoryCost.requestFocus();
            inputInventoryCost.setErrorEnabled(true);
            inputInventoryName.setErrorEnabled(false);
        } else if (INVENTORY_PHOTO_URI == null) {
            Toast.makeText(
                    getApplicationContext(),
                    "Please select a photo for the new inventory item",
                    Toast.LENGTH_LONG).show();
            /* DISABLE ALL ERRORS */
            inputInventoryCost.setErrorEnabled(false);
            inputInventoryName.setErrorEnabled(false);
        } else {
            /* DISABLE ALL ERRORS */
            inputInventoryCost.setErrorEnabled(false);
            inputInventoryName.setErrorEnabled(false);

            /* SHOW THE PROGRESS DIALOG **/
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Checking for unique inventory name...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

            /* CHECK FOR UNIQUE NAME */
            checkUniqueName();
        }
    }

    /** CHECK FOR UNIQUE NAME **/
    private void checkUniqueName() {
        InventoriesAPI api = ZenApiClient.getClient().create(InventoriesAPI.class);
        Call<Inventory> call = api.checkUniqueKennelInventory(KENNEL_ID, KENNEL_INVENTORY_NAME, INVENTORY_TYPE_ID);
        call.enqueue(new Callback<Inventory>() {
            @Override
            public void onResponse(Call<Inventory> call, Response<Inventory> response) {
//                Log.e("UNIQUE RESPONSE", String.valueOf(response.raw()));

                /* DISMISS THE PROGRESS DIALOG */
                progressDialog.dismiss();

                Inventory inventory = response.body();
                if (!inventory.getError())  {
                    /* SHOW THE PROGRESS DIALOG **/
                    progressDialog = new ProgressDialog(InventoryCreator.this);
                    progressDialog.setMessage("Adding the new inventory item...");
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    /* UPLOAD THE INVENTORY PHOTO */
                    uploadInventoryPhoto();
                } else {
                    new MaterialDialog.Builder(InventoryCreator.this)
                            .icon(ContextCompat.getDrawable(InventoryCreator.this, R.drawable.ic_info_black_24dp))
                            .title("Name Exists!")
                            .cancelable(true)
                            .content("An Inventory Item of the same name and type already exists in your inventory. Please choose a unique name for your new Inventory item. Unique names help managing your Kennel's inventory better.")
                            .positiveText("Got It")
                            .theme(Theme.LIGHT)
                            .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Intent signUp = new Intent(InventoryCreator.this, InventoryCreator.class);
                                    startActivity(signUp);
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(Call<Inventory> call, Throwable t) {
//                Log.e("UNIQUE FAILURE", t.getMessage());
            }
        });
    }

    /** UPLOAD THE INVENTORY PHOTO **/
    private void uploadInventoryPhoto() {
        INVENTORY_PHOTO_FILE_NAME = KENNEL_INVENTORY_NAME.replaceAll(" ", "_").toLowerCase().trim() + "_" + KENNEL_ID;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference refStorage = storageReference.child("Kennel Inventories").child(INVENTORY_PHOTO_FILE_NAME);
        UploadTask uploadTask = refStorage.putFile(INVENTORY_PHOTO_URI);
        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                /* CONTINUE WITH THE TASK TO GET THE IMAGE URL */
                return refStorage.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    INVENTORY_PHOTO = String.valueOf(task.getResult());
                    if (INVENTORY_PHOTO != null)    {

                        /* ADD THE NEW INVENTORY ITEM TO THE KENNEL'S ACCOUNT */
                        addInventoryItem();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(
                                getApplicationContext(),
                                "There was a problem creating your new account. Please try again by clicking the Save button.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    /** ADD THE NEW INVENTORY ITEM TO THE KENNEL'S ACCOUNT **/
    private void addInventoryItem() {
        InventoriesAPI api = ZenApiClient.getClient().create(InventoriesAPI.class);
        Call<Inventory> call = api.createKennelInventoryRecord(
                INVENTORY_TYPE_ID, KENNEL_ID, KENNEL_INVENTORY_NAME, INVENTORY_PHOTO,
                KENNEL_INVENTORY_COST, KENNEL_INVENTORY_STATUS);
        call.enqueue(new Callback<Inventory>() {
            @Override
            public void onResponse(Call<Inventory> call, Response<Inventory> response) {
                Inventory inventory = response.body();
                if (!inventory.getError())  {
                    String kennelInventoryID = response.body().getKennelInventoryID();
                    Log.e("INVENTORY ID", kennelInventoryID);

                    progressDialog.dismiss();
                    Intent success = new Intent();
                    setResult(RESULT_OK, success);
                    Toast.makeText(
                            getApplicationContext(),
                            "New inventory item published successfully...",
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            "Failed to publish new inventory item...",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Inventory> call, Throwable t) {

            }
        });
    }

    /** FETCH THE LIST OF INVENTORY TYPES **/
    private void fetchInventoryTypes() {
        TypesAPI api = ZenApiClient.getClient().create(TypesAPI.class);
        Call<Types> call = api.fetchInventoryTypes();
        call.enqueue(new Callback<Types>() {
            @Override
            public void onResponse(Call<Types> call, Response<Types> response) {
                if (response.body() != null && response.body().getTypes() != null) {
                    arrTypes = response.body().getTypes();
                    if (arrTypes.size() > 0) {
                        spnInventoryType.setAdapter(new InventoryTypesAdapter(
                                InventoryCreator.this,
                                R.layout.inventory_types_row,
                                arrTypes));
                    }
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Failed to get required info...",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Types> call, Throwable t) {
//                Log.e("TYPES FAILURE", t.getMessage());
            }
        });
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "New Inventory Item";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getApplicationContext()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(InventoryCreator.this);
        inflater.inflate(R.menu.activity_save_cancel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuSave:
                /* CHECK INVENTORY DETAILS */
                checkInventoryDetails();
                break;
            case R.id.menuCancel:
                this.finish();
                break;
            default:
                break;
        }
        return false;
    }

    /***** CHECK STORAGE PERMISSION *****/
    private void checkStoragePermission() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(InventoryCreator.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)   {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))    {
                /* SHOW THE DIALOG */
                new MaterialDialog.Builder(this)
                        .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_black_24dp))
                        .title(getString(R.string.storage_permission_title))
                        .cancelable(false)
                        .content(getString(R.string.storage_permission_message))
                        .positiveText(getString(R.string.permission_grant))
                        .negativeText(getString(R.string.permission_deny))
                        .theme(Theme.LIGHT)
                        .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.cancel();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(
                                        InventoryCreator.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE_CONSTANT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ACCESS_STORAGE_CONSTANT);
            }
        } else {
            final BottomSheetDialog sheetDialog = new BottomSheetDialog(InventoryCreator.this);
            @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.image_picker_sheet, null);
            sheetDialog.setContentView(view);
            sheetDialog.show();

            /* CAST THE CHOOSER ELEMENTS */
            LinearLayout linlaGallery = view.findViewById(R.id.linlaGallery);
            LinearLayout linlaCamera = view.findViewById(R.id.linlaCamera);

            /* SELECT A GALLERY IMAGE */
            linlaGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sheetDialog.dismiss();
                    EasyImage.openGallery(InventoryCreator.this, 0);
                }
            });

            /* SELECT A CAMERA IMAGE */
            linlaCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sheetDialog.dismiss();
                    EasyImage.openCamera(InventoryCreator.this, 0);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_STORAGE_CONSTANT)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                final BottomSheetDialog sheetDialog = new BottomSheetDialog(InventoryCreator.this);
                @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.image_picker_sheet, null);
                sheetDialog.setContentView(view);
                sheetDialog.show();

                /* CAST THE CHOOSER ELEMENTS */
                LinearLayout linlaGallery = view.findViewById(R.id.linlaGallery);
                LinearLayout linlaCamera = view.findViewById(R.id.linlaCamera);

                /* SELECT A GALLERY IMAGE */
                linlaGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheetDialog.dismiss();
                        EasyImage.openGallery(InventoryCreator.this, 0);
                    }
                });

                /* SELECT A CAMERA IMAGE */
                linlaCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheetDialog.dismiss();
                        EasyImage.openCamera(InventoryCreator.this, 0);
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                onPhotoReturned(imageFiles);
            }
        });
    }

    /***** PROCESS THE SELECTED IMAGE AND GRAB THE URI *****/
    private void onPhotoReturned(List<File> imageFiles) {
        try {
            File compressedFile = new Compressor(this)
                    .setMaxWidth(800)
                    .setMaxHeight(800)
                    .setQuality(80)
                    .setCompressFormat(Bitmap.CompressFormat.PNG)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .compressToFile(imageFiles.get(0));
            Uri uri = Uri.fromFile(compressedFile);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            imgvwKennelPhoto.setImageBitmap(bitmap);
            imgvwKennelPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);

            /* STORE THE BITMAP AS A FILE AND USE THE FILE'S URI */
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/ZenPets");
            myDir.mkdirs();
            String fName = "photo.jpg";
            File file = new File(myDir, fName);
            if (file.exists()) file.delete();

            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                /* GET THE FINAL URI */
                INVENTORY_PHOTO_URI = Uri.fromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EasyImage.clearConfiguration(this);
    }
}