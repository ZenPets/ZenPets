package biz.zenpets.users.creator.pet;

//public class PetCreator extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
//
//    private AppPrefs getApp()	{
//        return (AppPrefs) getApplication();
//    }
//
//    /****** DATA TYPES FOR THE PET DETAILS *****/
//    private String USER_ID = null;
//    private String PET_TYPE_ID = null;
//    private String PET_BREED_ID = null;
//    private String PET_NAME = null;
//    private String PET_GENDER = "Male";
//    private String PET_DOB = null;
//    private String PET_PROFILE = null;
//    private String FILE_NAME = null;
//    private Uri PET_URI = null;
//
//    /** A PROGRESS DIALOG **/
//    private ProgressDialog dialog;
//
//    /** PERMISSION REQUEST CONSTANT **/
//    private static final int ACCESS_STORAGE_CONSTANT = 201;
//
//    /** THE PET TYPES ADAPTER AND ARRAY LIST **/
//    private PetTypesAdapter petTypesAdapter;
//    private final ArrayList<PetTypesData> arrPetTypes = new ArrayList<>();
//
//    /** THE BREEDS ADAPTER AND ARRAY LIST **/
//    private BreedsAdapter breedsAdapter;
//    private final ArrayList<BreedsData> arrBreeds = new ArrayList<>();
//
//    /** CAST THE LAYOUT ELEMENTS **/
//    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
//    @BindView(R.id.spnPetTypes) AppCompatSpinner spnPetTypes;
//    @BindView(R.id.spnBreeds) AppCompatSpinner spnBreeds;
//    @BindView(R.id.inputPetName) TextInputLayout inputPetName;
//    @BindView(R.id.edtPetName) AppCompatEditText edtPetName;
//    @BindView(R.id.rdgGender) RadioGroup rdgGender;
//    @BindView(R.id.txtPetDOB) AppCompatTextView txtPetDOB;
//    @BindView(R.id.imgvwPetThumb) AppCompatImageView imgvwPetThumb;
//
//    /** SELECT AN IMAGE OF THE MEDICINE **/
//    @OnClick(R.id.imgvwPetThumb) void selectImage()    {
//        /* CHECK STORAGE PERMISSION */
//        checkStoragePermission();
//    }
//
//    /** SELECT THE PET'S DATE OF BIRTH **/
//    @OnClick(R.id.btnDOBSelector) void selectDOB()   {
//        Calendar now = Calendar.getInstance();
//        DatePickerDialog pickerDialog = DatePickerDialog.newInstance(
//                PetCreator.this,
//                now.get(Calendar.YEAR),
//                now.get(Calendar.MONTH),
//                now.get(Calendar.DAY_OF_MONTH));
//        pickerDialog.show(getFragmentManager(), "DatePickerDialog");
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pet_creator);
//        ButterKnife.bind(this);
//
//        /* THE EASY IMAGE CONFIGURATION */
//        EasyImage.configuration(this)
//                .setImagesFolderName("Zen Pets")
//                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
//                .setCopyPickedImagesToPublicGalleryAppFolder(true)
//                .setAllowMultiplePickInGallery(false);
//
//        /* SET THE CURRENT DATE */
//        setCurrentDate();
//
//        /* CONFIGURE THE TOOLBAR */
//        configTB();
//
//        /* GET THE USER ID */
//        USER_ID = getApp().getUserID();
//
//        /* FETCH ALL PET TYPES */
//        fetchPetTypes();
//
//        /* SELECT THE PET'S GENDER */
//        rdgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//                switch (checkedId) {
//                    case R.id.rdbtnMale:
//                        /* SET THE GENDER TO MALE */
//                        PET_GENDER = "Male";
//                        break;
//                    case R.id.rdbtnFemale:
//                        /* SET THE GENDER TO FEMALE */
//                        PET_GENDER = "Female";
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//
//        /* SELECT A PET TYPE */
//        spnPetTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, ConsultationView view, int position, long id) {
//                /* GET THE SELECTED PET TYPE ID */
//                PET_TYPE_ID = arrPetTypes.get(position).getPetTypeID();
//
//                /* CLEAR THE BREEDS ARRAY LIST */
//                arrBreeds.clear();
//
//                /* FETCH THE LIST OF BREEDS IN THE SELECTED PET TYPE */
//                fetchBreeds();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//
//        /* SELECT A BREED */
//        spnBreeds.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, ConsultationView view, int position, long id) {
//                PET_BREED_ID = arrBreeds.get(position).getBreedID();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }
//
//    /***** FETCH THE LIST OF BREEDS IN THE SELECTED PET TYPE *****/
//    private void fetchBreeds() {
//        String URL_BREEDS = getString(R.string.url_all_breeds);
//        HttpUrl.Builder builder = HttpUrl.parse(URL_BREEDS).newBuilder();
//        builder.addQueryParameter("petTypeID", PET_TYPE_ID);
//        String FINAL_URL = builder.build().toString();
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(FINAL_URL)
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                Log.e("FAILURE", e.toString());
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                try {
//                    String strResult = response.body().string();
//                    JSONObject JORoot = new JSONObject(strResult);
//                    if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
//                        JSONArray JABreeds = JORoot.getJSONArray("breeds");
//
//                        /* AND INSTANCE OF THE BREEDS DATA MODEL */
//                        BreedsData data;
//
//                        for (int i = 0; i < JABreeds.length(); i++) {
//                            JSONObject JOBreeds = JABreeds.getJSONObject(i);
////                            Log.e("BREEDS", String.valueOf(JOBreeds));
//
//                            /* INSTANTIATE THE PET TYPES DATA INSTANCE */
//                            data = new BreedsData();
//
//                            /* GET THE BREED ID */
//                            if (JOBreeds.has("breedID"))    {
//                                data.setBreedID(JOBreeds.getString("breedID"));
//                            } else {
//                                data.setBreedID(null);
//                            }
//
//                            /* GET THE PET TYPE ID */
//                            if (JOBreeds.has("petTypeID"))  {
//                                data.setPetTypeID(JOBreeds.getString("petTypeID"));
//                            } else {
//                                data.setPetTypeID(null);
//                            }
//
//                            /* GET THE BREED NAME */
//                            if (JOBreeds.has("breedName"))  {
//                                data.setBreedName(JOBreeds.getString("breedName"));
//                            } else {
//                                data.setBreedName(null);
//                            }
//
//                            /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
//                            arrBreeds.add(data);
//                        }
//
//                        /* INSTANTIATE THE BREEDS ADAPTER AND SET THE ADAPTER TO THE SPINNER */
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                /* INSTANTIATE THE BREEDS ADAPTER */
//                                breedsAdapter = new BreedsAdapter(PetCreator.this, arrBreeds);
//
//                                /* SET THE ADAPTER TO THE BREEDS SPINNER */
//                                spnBreeds.setAdapter(breedsAdapter);
//                            }
//                        });
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    /***** FETCH ALL PET TYPES *****/
//    private void fetchPetTypes() {
//        String URL_PET_TYPES = getString(R.string.url_pet_types);
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(URL_PET_TYPES)
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                Log.e("FAILURE", e.toString());
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                try {
//                    String strResult = response.body().string();
//                    JSONObject JORoot = new JSONObject(strResult);
//                    if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
//                        JSONArray JATypes = JORoot.getJSONArray("types");
//
//                        /* AND INSTANCE OF THE PET TYPE DATA MODEL */
//                        PetTypesData data;
//
//                        for (int i = 0; i < JATypes.length(); i++) {
//                            JSONObject JOTypes = JATypes.getJSONObject(i);
////                            Log.e("TYPES", String.valueOf(JOTypes));
//
//                            /* INSTANTIATE THE PET TYPES DATA INSTANCE */
//                            data = new PetTypesData();
//
//                            /* GET THE PET TYPE ID */
//                            if (JOTypes.has("petTypeID"))   {
//                                data.setPetTypeID(JOTypes.getString("petTypeID"));
//                            } else {
//                                data.setPetTypeID(null);
//                            }
//
//                            /* GET THE PET TYPE */
//                            if (JOTypes.has("petTypeName")) {
//                                data.setPetTypeName(JOTypes.getString("petTypeName"));
//                            } else {
//                                data.setPetTypeName(null);
//                            }
//
//                            /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
//                            arrPetTypes.add(data);
//                        }
//
//                        /* INSTANTIATE THE PET TYPES ADAPTER AND SET THE ADAPTER TO THE SPINNER */
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                /* INSTANTIATE THE PET TYPES ADAPTER */
//                                petTypesAdapter = new PetTypesAdapter(PetCreator.this, arrPetTypes);
//
//                                /* SET THE ADAPTER TO THE PET TYPES SPINNER */
//                                spnPetTypes.setAdapter(petTypesAdapter);
//                            }
//                        });
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    /***** CONFIGURE THE TOOLBAR *****/
//    private void configTB() {
//        Toolbar myToolbar = findViewById(R.id.myToolbar);
//        setSupportActionBar(myToolbar);
//        String strTitle = "Add a new Pet";
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setTitle(strTitle);
//        getSupportActionBar().setSubtitle(null);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = new MenuInflater(PetCreator.this);
//        inflater.inflate(R.menu.activity_save_cancel, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                this.finish();
//                break;
//            case R.id.menuSave:
//                /* CHECK FOR ALL PET DETAILS  */
//                checkPetDetails();
//                break;
//            case R.id.menuCancel:
//                /* CANCEL NEW PET CREATION */
//                finish();
//                break;
//            default:
//                break;
//        }
//        return false;
//    }
//
//    /***** CHECK FOR ALL PET DETAILS  *****/
//    private void checkPetDetails() {
//        /* HIDE THE KEYBOARD */
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.hideSoftInputFromWindow(edtPetName.getWindowToken(), 0);
//        }
//
//        /* GET THE REQUIRED TEXTS */
//        PET_NAME = edtPetName.getText().toString().trim();
//        if (!TextUtils.isEmpty(PET_NAME))   {
//            FILE_NAME = PET_NAME.replaceAll(" ", "_").toLowerCase().trim();
//        }
//
//        if (TextUtils.isEmpty(PET_NAME))   {
//            inputPetName.setError("Enter the Pet's name");
//        } else if (PET_URI == null)   {
//            Toast.makeText(getApplicationContext(), "Please select your Pet's image", Toast.LENGTH_LONG).show();
//        } else {
//            /* POST THE PETS DISPLAY PICTURE */
//            postPetPicture();
//        }
//    }
//
//    /***** POST THE PETS DISPLAY PICTURE *****/
//    private void postPetPicture() {
//        /* INSTANTIATE THE PROGRESS DIALOG INSTANCE */
//        dialog = new ProgressDialog(this);
//        dialog.setMessage("Please wait while we add the new Pet to your account..");
//        dialog.setIndeterminate(false);
//        dialog.setCancelable(false);
//        dialog.show();
//
//        /* PUBLISH THE PET PROFILE TO FIREBASE */
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//        StorageReference refStorage = storageReference.child("Pets").child(FILE_NAME);
//        refStorage.putFile(PET_URI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Uri downloadURL = taskSnapshot.getDownloadUrl();
//                if (downloadURL != null)    {
//                    PET_PROFILE = String.valueOf(downloadURL);
//                    if (PET_PROFILE != null)    {
//                        /* PUBLISH THE NEW PET */
//                        publishNewPet();
//                    } else {
//                        dialog.dismiss();
//                        Toast.makeText(
//                                getApplicationContext(),
//                                "There was a problem adding your new Pet. Please try again by clicking the Save button.",
//                                Toast.LENGTH_LONG).show();
//                    }
//                } else {
//                    dialog.dismiss();
//                    Toast.makeText(
//                            getApplicationContext(),
//                            "There was a problem adding your new Pet. Please try again by clicking the Save button.",
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    /***** PUBLISH THE NEW PET *****/
//    private void publishNewPet() {
//        String URL_NEW_PET = getString(R.string.url_user_pet_new);
//        OkHttpClient client = new OkHttpClient();
//        RequestBody body = new FormBody.Builder()
//                .add("userID", USER_ID)
//                .add("petTypeID", PET_TYPE_ID)
//                .add("breedID", PET_BREED_ID)
//                .add("petName", PET_NAME)
//                .add("petGender", PET_GENDER)
//                .add("petDOB", PET_DOB)
//                .add("petProfile", PET_PROFILE)
//                .build();
//        Request request = new Request.Builder()
//                .url(URL_NEW_PET)
//                .post(body)
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                Log.e("FAILURE", e.toString());
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                try {
//                    String strResult = response.body().string();
//                    JSONObject JORoot = new JSONObject(strResult);
//                    if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
//                        /* DISMISS THE DIALOG */
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                dialog.dismiss();
//                                Toast.makeText(getApplicationContext(), "Successfully added your Pet", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent();
//                                setResult(RESULT_OK, intent);
//                                finish();
//                            }
//                        });
//                    } else {
//                        /* DISMISS THE DIALOG */
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                dialog.dismiss();
//                                Toast.makeText(getApplicationContext(), "There was an error adding your Pet. Please try again", Toast.LENGTH_LONG).show();
//                            }
//                        });
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    /***** SET THE CURRENT DATE *****/
//    private void setCurrentDate() {
//        /* SET THE CURRENT DATE (DISPLAY ONLY !!!!) */
//        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
//        String formattedDate = dateFormat.format(new Date());
//        txtPetDOB.setText(formattedDate);
//
//        /* SET THE CURRENT DATE (DATABASE ONLY !!!!) */
//        Calendar cal = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        PET_DOB = sdf.format(cal.getTime());
//    }
//
//    @Override
//    public void onDateSet(DatePickerDialog view, int year, int month, int date) {
//        Calendar cal = Calendar.getInstance();
//        cal.set(year, month, date);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//
//        /* FOR THE DATABASE ONLY !!!! */
//        PET_DOB = sdf.format(cal.getTime());
////        Log.e("INITIAL PET DOB", PET_DOB);
//
//        /* FOR DISPLAY ONLY !!!! */
//        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
//        String selectedDate = dateFormat.format(cal.getTime());
//        txtPetDOB.setText(selectedDate);
//    }
//
//    /***** CHECK STORAGE PERMISSION *****/
//    private void checkStoragePermission() {
//        /* CHECK FOR PERMISSION STATUS */
//        if (ContextCompat.checkSelfPermission(PetCreator.this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED)   {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))    {
//                /* SHOW THE DIALOG */
//                new MaterialDialog.Builder(this)
//                        .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp))
//                        .title(getString(R.string.storage_permission_title))
//                        .cancelable(false)
//                        .content(getString(R.string.storage_permission_message))
//                        .positiveText(getString(R.string.permission_grant))
//                        .negativeText(getString(R.string.permission_deny))
//                        .theme(Theme.LIGHT)
//                        .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
//                        .onNegative(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                dialog.cancel();
//                            }
//                        })
//                        .onPositive(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                dialog.cancel();
//                                ActivityCompat.requestPermissions(
//                                        PetCreator.this,
//                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE_CONSTANT);
//                            }
//                        }).show();
//            } else {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        ACCESS_STORAGE_CONSTANT);
//            }
//        } else {
//            final BottomSheetDialog sheetDialog = new BottomSheetDialog(PetCreator.this);
//            @SuppressLint("InflateParams") ConsultationView view = getLayoutInflater().inflate(R.layout.image_picker_sheet, null);
//            sheetDialog.setContentView(view);
//            sheetDialog.show();
//
//            /* CAST THE CHOOSER ELEMENTS */
//            LinearLayout linlaGallery = view.findViewById(R.id.linlaGallery);
//            LinearLayout linlaCamera = view.findViewById(R.id.linlaCamera);
//
//            /* SELECT A GALLERY IMAGE */
//            linlaGallery.setOnClickListener(new ConsultationView.OnClickListener() {
//                @Override
//                public void onClick(ConsultationView v) {
//                    sheetDialog.dismiss();
//                    EasyImage.openGallery(PetCreator.this, 0);
//                }
//            });
//
//            /* SELECT A CAMERA IMAGE */
//            linlaCamera.setOnClickListener(new ConsultationView.OnClickListener() {
//                @Override
//                public void onClick(ConsultationView v) {
//                    sheetDialog.dismiss();
//                    EasyImage.openCamera(PetCreator.this, 0);
//                }
//            });
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == ACCESS_STORAGE_CONSTANT)  {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
//                final BottomSheetDialog sheetDialog = new BottomSheetDialog(PetCreator.this);
//                @SuppressLint("InflateParams") ConsultationView view = getLayoutInflater().inflate(R.layout.image_picker_sheet, null);
//                sheetDialog.setContentView(view);
//                sheetDialog.show();
//
//                /* CAST THE CHOOSER ELEMENTS */
//                LinearLayout linlaGallery = view.findViewById(R.id.linlaGallery);
//                LinearLayout linlaCamera = view.findViewById(R.id.linlaCamera);
//
//                /* SELECT A GALLERY IMAGE */
//                linlaGallery.setOnClickListener(new ConsultationView.OnClickListener() {
//                    @Override
//                    public void onClick(ConsultationView v) {
//                        sheetDialog.dismiss();
//                        EasyImage.openGallery(PetCreator.this, 0);
//                    }
//                });
//
//                /* SELECT A CAMERA IMAGE */
//                linlaCamera.setOnClickListener(new ConsultationView.OnClickListener() {
//                    @Override
//                    public void onClick(ConsultationView v) {
//                        sheetDialog.dismiss();
//                        EasyImage.openCamera(PetCreator.this, 0);
//                    }
//                });
//            } else {
//            }
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
//            @Override
//            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
//                onPhotoReturned(imageFiles);
//            }
//        });
//    }
//
//    /***** PROCESS THE SELECTED IMAGE AND GRAB THE URI *****/
//    private void onPhotoReturned(List<File> imageFiles) {
//        try {
//            File compressedFile = new Compressor(this)
//                    .setMaxWidth(800)
//                    .setMaxHeight(800)
//                    .setQuality(100)
//                    .setCompressFormat(Bitmap.CompressFormat.PNG)
//                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
//                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
//                    .compressToFile(imageFiles.get(0));
//            Uri uri = Uri.fromFile(compressedFile);
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//            imgvwPetThumb.setImageBitmap(bitmap);
//            imgvwPetThumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
//
//            /* STORE THE BITMAP AS A FILE AND USE THE FILE'S URI */
//            String root = Environment.getExternalStorageDirectory().toString();
//            File myDir = new File(root + "/ZenPets");
//            myDir.mkdirs();
//            String fName = "photo.jpg";
//            File file = new File(myDir, fName);
//            if (file.exists()) file.delete();
//
//            try {
//                FileOutputStream out = new FileOutputStream(file);
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                out.flush();
//                out.close();
//
//                /* GET THE FINAL URI */
//                PET_URI = Uri.fromFile(file);
////                Log.e("URI", String.valueOf(PET_URI));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        EasyImage.clearConfiguration(this);
//    }
//}