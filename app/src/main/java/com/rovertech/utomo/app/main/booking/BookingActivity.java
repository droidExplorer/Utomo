package com.rovertech.utomo.app.main.booking;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rovertech.utomo.app.R;
import com.rovertech.utomo.app.account.model.UserProfileOutput;
import com.rovertech.utomo.app.addCar.AddCarActivity;
import com.rovertech.utomo.app.bookings.BookingPresenter;
import com.rovertech.utomo.app.bookings.BookingPresenterImpl;
import com.rovertech.utomo.app.bookings.BookingView;
import com.rovertech.utomo.app.bookings.model.BookingRequest;
import com.rovertech.utomo.app.helper.AppConstant;
import com.rovertech.utomo.app.helper.Functions;
import com.rovertech.utomo.app.helper.IntentConstant;
import com.rovertech.utomo.app.helper.PrefUtils;
import com.rovertech.utomo.app.main.booking.model.AddressItem;
import com.rovertech.utomo.app.main.booking.model.DropPojo;
import com.rovertech.utomo.app.main.booking.model.PickupPojo;
import com.rovertech.utomo.app.main.centreDetail.model.FetchServiceCentreDetailPojo;
import com.rovertech.utomo.app.main.drawer.DrawerActivityRevised;
import com.rovertech.utomo.app.profile.carlist.CarPojo;
import com.rovertech.utomo.app.widget.dialog.AddressDialog;
import com.rovertech.utomo.app.widget.dialog.AddressDialogRevised;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BookingActivity extends AppCompatActivity implements BookingView, View.OnClickListener {

    private Toolbar toolbar;
    private TextView txtCustomTitle;
    private View parentView;
    private LinearLayout linearLayout;
    private BookingPresenter presenter;
    private Button btnBook;
    private TextView btnPickUpAddressAdd, btnDropAddressAdd;
    private TextView btnPickUpAddressRemove;
    private TextView btnDropAddressRemove;
    private EditText edtDescription;

    // Service Centre Section
    private TextView txtTitle;

    // User Section
    private TextView txtUsername, txtCarName, txtCarNo;

    // Schedule Section
    private TextView txtDate, txtTime;
    private CheckBox checkService, checkBodyWash;

    // Address Section
    private TextView txtDropoffAddress, txtPickupAddress, txtAddress, txtSelectCar,
            txtPickupTitle, txtDropoffTitle;

    private DropPojo dropPojo;
    private PickupPojo pickupPojo;

    // Promo Section
    private CardView promoCardView, edtPromoCard;
    private RadioGroup radioGroup;

    private int ADDRESS_PICK_UP = 1;
    private int ADDRESS_DROP_OFF = 2;
    private BookingRequest bookingRequest;
    private UserProfileOutput userProfileOutput;
    private FetchServiceCentreDetailPojo centreDetailPojo;
    private CarPojo carPojo;
    private LinearLayout addressHolder;
    private static final String bookingDateTimeFormate = "dd MMM, yyyy KK:mm a";

    private String dealerShip = "", date, time;
    private int redirectFrom = 0;
    private boolean isCarSelected = false;

    private ArrayList<CarPojo> carList;

    private View pickupLayout;

    private boolean isDropOffSame = false, isPickupSame = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        redirectFrom = getIntent().getIntExtra(IntentConstant.BOOKING_PAGE, 0);

        init();

        presenter = new BookingPresenterImpl(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.fetchDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_requestbook, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_home_requestbook:
                Functions.hideKeyPad(this, parentView);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        this);
                alertDialogBuilder.setTitle("Alert !!")
                        .setMessage("Are you sure want to navigate to Dashboard?")
                        .setCancelable(false)
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                gotoDashboard();
                            }
                        })
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void gotoDashboard() {
        Intent iDashboard = new Intent(BookingActivity.this, DrawerActivityRevised.class);
        iDashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(iDashboard);
        finish();
    }

    private void init() {

        initToolbar();

        pickupLayout = (View) findViewById(R.id.pickupLayout);
        txtSelectCar = (TextView) findViewById(R.id.txtSelectCar);
        edtDescription = (EditText) findViewById(R.id.edtDescription);
        parentView = findViewById(android.R.id.content);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        addressHolder = (LinearLayout) findViewById(R.id.addressHolder);
        btnBook = (Button) findViewById(R.id.btnBook);
        btnPickUpAddressAdd = (TextView) findViewById(R.id.btnPickUpAddressAdd);
        btnDropAddressAdd = (TextView) findViewById(R.id.btnDropUpAddressAdd);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtAddress = (TextView) findViewById(R.id.txtAddress);

        txtUsername = (TextView) findViewById(R.id.txtUsername);
        txtCarName = (TextView) findViewById(R.id.txtCarName);
        txtCarNo = (TextView) findViewById(R.id.txtCarNo);

        txtDate = (TextView) findViewById(R.id.txtDate);
        txtTime = (TextView) findViewById(R.id.txtTime);
        checkService = (CheckBox) findViewById(R.id.checkService);
        checkBodyWash = (CheckBox) findViewById(R.id.checkBodyWash);

        txtPickupAddress = (TextView) findViewById(R.id.txtPickupAddress);
        txtDropoffAddress = (TextView) findViewById(R.id.txtDropoffAddress);

        btnPickUpAddressRemove = (TextView) findViewById(R.id.btnPickUpAddressRemove);

        btnDropAddressRemove = (TextView) findViewById(R.id.btnDropAddressRemove);

        promoCardView = (CardView) findViewById(R.id.promoCardView);
        edtPromoCard = (CardView) findViewById(R.id.edtPromoCard);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        txtPickupTitle = (TextView) findViewById(R.id.txtPickupTitle);
        txtDropoffTitle = (TextView) findViewById(R.id.txtDropoffTitle);

        txtDate.setOnClickListener(this);
        txtTime.setOnClickListener(this);
        btnBook.setOnClickListener(this);
        txtSelectCar.setOnClickListener(this);

        btnPickUpAddressAdd.setOnClickListener(this);
        btnDropAddressAdd.setOnClickListener(this);

        btnPickUpAddressRemove.setOnClickListener(this);

        btnDropAddressRemove.setOnClickListener(this);

        setTypeface();

        bookingRequest = new BookingRequest();

    }

    private void setTypeface() {
        txtTitle.setTypeface(Functions.getBoldFont(this), Typeface.BOLD);
        txtAddress.setTypeface(Functions.getRegularFont(this));

        txtCarName.setTypeface(Functions.getRegularFont(this), Typeface.BOLD);
        txtCarNo.setTypeface(Functions.getRegularFont(this));

        txtDate.setTypeface(Functions.getRegularFont(this), Typeface.BOLD);
        txtTime.setTypeface(Functions.getRegularFont(this), Typeface.BOLD);

        checkService.setTypeface(Functions.getRegularFont(this), Typeface.BOLD);
        checkBodyWash.setTypeface(Functions.getRegularFont(this), Typeface.BOLD);

        txtPickupTitle.setTypeface(Functions.getBoldFont(this), Typeface.BOLD);
        txtDropoffTitle.setTypeface(Functions.getBoldFont(this), Typeface.BOLD);
        btnPickUpAddressAdd.setTypeface(Functions.getBoldFont(this));
        btnDropAddressAdd.setTypeface(Functions.getBoldFont(this));
        btnPickUpAddressRemove.setTypeface(Functions.getBoldFont(this));
        btnDropAddressRemove.setTypeface(Functions.getBoldFont(this));

        txtPickupAddress.setTypeface(Functions.getRegularFont(this));
        txtDropoffAddress.setTypeface(Functions.getRegularFont(this));

        btnBook.setTypeface(Functions.getBoldFont(this), Typeface.BOLD);

        txtSelectCar.setTypeface(Functions.getBoldFont(this), Typeface.BOLD);

        edtDescription.setTypeface(Functions.getRegularFont(this));

        txtUsername.setTypeface(Functions.getBoldFont(this), Typeface.BOLD);

    }

    private void initToolbar() {
        txtCustomTitle = (TextView) findViewById(R.id.txtCustomTitle);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow);
        setSupportActionBar(toolbar);

        txtCustomTitle.setText("Booking");
        txtCustomTitle.setTypeface(Functions.getBoldFont(this), Typeface.BOLD);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    @Override
    public void setDetails() {

        centreDetailPojo = PrefUtils.getCurrentCenter(this);

        if (centreDetailPojo.IsPickupDrop) {
            pickupLayout.setVisibility(View.VISIBLE);
        } else {
            pickupLayout.setVisibility(View.GONE);
        }

        dealerShip = centreDetailPojo.Dealership;

        if (redirectFrom == AppConstant.FROM_SC_LIST) {

            isCarSelected = true;
            carPojo = PrefUtils.getCurrentCarSelected(this);
            userProfileOutput = PrefUtils.getUserFullProfileDetails(this);

            if (userProfileOutput != null) {
                txtUsername.setText(String.format("%s", userProfileOutput.Name));
            }

            if (carPojo != null) {
                txtCarName.setText(String.format("%s %s", carPojo.Make, carPojo.Model));
                txtCarNo.setText(String.format("%s", carPojo.VehicleNo));
            }

        } else {
            carList = new ArrayList<>();
            presenter.fetchCarList(this, centreDetailPojo.Dealership);
        }

        txtTitle.setText(centreDetailPojo.ServiceCentreName);
        txtAddress.setText(centreDetailPojo.Address1);

    }

    @Override
    public void setDate(String convertedDate) {
        date = convertedDate;
        txtDate.setText(convertedDate);

        txtTime.setText("Select Time");
        time = "";
    }

    @Override
    public void setTime(String strTime) {
        time = strTime;

        if (date != null) {
            SimpleDateFormat df2 = new SimpleDateFormat("dd MMMM, yyyy hh:mm aa");
            String eDate = date + " " + time;
            Date CurrentDate = new Date();
            Date eDDte;
            try {
                eDDte = df2.parse(eDate);

                if (eDDte.compareTo(CurrentDate) <= 0) {
                    txtTime.setText("Select Time");
                    Functions.showErrorAlert(BookingActivity.this, "Select valid Date And Time", AppConstant.INVALID_TIME);
                } else {
                    txtTime.setText(strTime);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            txtTime.setText(strTime);
        }

    }

    @Override
    public void setSelectedCar(CarPojo carPojo) {
        this.carPojo = carPojo;
        txtCarName.setVisibility(View.VISIBLE);
        txtCarNo.setVisibility(View.VISIBLE);
        txtCarName.setText(String.format("%s %s", carPojo.Make, carPojo.Model));
        txtCarNo.setText(String.format("%s", carPojo.VehicleNo));
        isCarSelected = true;
    }

    @Override
    public void setCarList(ArrayList<CarPojo> carList) {
        this.carList = carList;

        if (carList.size() > 0) {
            carPojo = carList.get(0);

            Log.e("car", Functions.jsonString(carList.get(0)));

            isCarSelected = true;
            txtUsername.setVisibility(View.GONE);
            txtCarNo.setVisibility(View.VISIBLE);
            txtCarName.setVisibility(View.VISIBLE);
            txtSelectCar.setVisibility(View.VISIBLE);

            txtCarName.setText(String.format("%s %s", carPojo.Make, carPojo.Model));
            txtCarNo.setText(String.format("%s", carPojo.VehicleNo));

        } else {
            isCarSelected = false;
            txtUsername.setVisibility(View.GONE);
            txtCarNo.setVisibility(View.GONE);
            txtCarName.setVisibility(View.GONE);
            txtSelectCar.setVisibility(View.VISIBLE);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    BookingActivity.this);
            alertDialogBuilder.setTitle("No Car")
                    .setMessage("You have no " + dealerShip + " car.")
                    .setCancelable(false)
                    .setPositiveButton("Add New Car", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            PrefUtils.setRedirectLogin(BookingActivity.this, AppConstant.FROM_NO_CAR);
                            Intent addCarIntent = new Intent(BookingActivity.this, AddCarActivity.class);
                            addCarIntent.putExtra(AppConstant.SKIP, false);
                            startActivity(addCarIntent);

                        }
                    })
                    .setNegativeButton("Dashboard", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            gotoDashboard();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    @Override
    public void setAddress(ArrayList<AddressItem> data) {
        dropPojo = data.get(0).DropDetails.get(0);
        if (dropPojo != null) {
            // bookingRequest.DropAddress = dropPojo.DropAddress;
            // bookingRequest.DropCity = dropPojo.DropCity;
            // bookingRequest.DropArea = dropPojo.DropArea;
            // bookingRequest.DropZipCode = dropPojo.DropZipCode;

            txtDropoffAddress.setVisibility(View.VISIBLE);
            String addressString = String.format("%s, %s, %s, %s", dropPojo.DropAddress, dropPojo.DropArea, dropPojo.DropCity, dropPojo.DropZipCode);
            txtDropoffAddress.setText(addressString);
            btnDropAddressAdd.setText(getString(R.string.item_booking_edit));
        } else {
            dropPojo = new DropPojo();
        }

        pickupPojo = data.get(0).PickUpDetails.get(0);

        if (pickupPojo != null) {
            // bookingRequest.PickAddress = pickupPojo.PickAddress;
            // bookingRequest.PickArea = pickupPojo.PickArea;
            // bookingRequest.PickCity = pickupPojo.PickCity;
            // bookingRequest.PickZipCode = pickupPojo.PickZipCode;

            txtPickupAddress.setVisibility(View.VISIBLE);
            String addressString = String.format("%s, %s, %s, %s", pickupPojo.PickAddress, pickupPojo.PickArea, pickupPojo.PickCity, pickupPojo.PickZipCode);
            txtPickupAddress.setText(addressString);
            btnPickUpAddressAdd.setText(getString(R.string.item_booking_edit));
        } else {
            pickupPojo = new PickupPojo();
        }
    }

    @Override
    public void onClick(View v) {

        Functions.hideKeyPad(this, parentView);

        switch (v.getId()) {
            case R.id.txtDate:
                presenter.selectDate(this);
                break;

            case R.id.txtTime:
                presenter.selectTime(this, date);
                break;

            case R.id.btnPickUpAddressAdd:
                openAddressDialog(AppConstant.TYPE_PICK_UP, isDropOffSame, pickupPojo, dropPojo);
                break;

            case R.id.btnDropUpAddressAdd:
                openAddressDialog(AppConstant.TYPE_DROP_OFF, isPickupSame, pickupPojo, dropPojo);
                break;

            case R.id.txtApply:
                break;

            case R.id.radioDefault:
                edtPromoCard.setVisibility(View.GONE);
                break;

            case R.id.radioPromo:
                edtPromoCard.setVisibility(View.VISIBLE);
                break;

            case R.id.btnBook:
                if (isCarSelected)
                    if (carPojo.CurrentBooking) {
                        Functions.showErrorAlert(BookingActivity.this, AppConstant.ALREADY_BOOK, false);
                    } else {
                        bookRequest();
                    }
                else
                    Functions.showToast(this, "Select car");
                break;

            case R.id.btnPickUpAddressRemove:
                removePickAddress();
                break;

            case R.id.btnDropAddressRemove:
                removeDropAddress();
                break;

            case R.id.txtSelectCar:
                presenter.openCarList(this, centreDetailPojo.Dealership);
                break;
        }
    }

    private void openAddressDialog(int typePickUp, boolean isDropOff, PickupPojo pojo1, DropPojo pojo2) {
        AddressDialogRevised dialogRevised = new AddressDialogRevised(this, typePickUp, isDropOff, pojo1, pojo2);
        dialogRevised.setOnSubmitListener(new AddressDialogRevised.onSubmitListener() {
            @Override
            public void onSubmit(int addressType, PickupPojo finalPickup, DropPojo finalDropOff, boolean isSame) {

                Log.e("finalPickup", Functions.jsonString(finalPickup));
                Log.e("finalDropOff", Functions.jsonString(finalDropOff));

                isDropOffSame = isSame;

                // set pickup
                pickupPojo = new PickupPojo();
                pickupPojo.PickZipCode = finalPickup.PickZipCode;
                pickupPojo.PickArea = finalPickup.PickArea;
                pickupPojo.PickAddress = finalPickup.PickAddress;
                pickupPojo.PickCity = finalPickup.PickCity;

                // set drop-off
                dropPojo = new DropPojo();
                dropPojo.DropAddress = finalDropOff.DropAddress;
                dropPojo.DropCity = finalDropOff.DropCity;
                dropPojo.DropZipCode = finalDropOff.DropZipCode;
                dropPojo.DropArea = finalDropOff.DropArea;

                // set pick-up text
                String pickupString = String.format("%s, %s, %s, %s", finalPickup.PickAddress, finalPickup.PickArea, finalPickup.PickCity, finalPickup.PickZipCode);
                if (TextUtils.isEmpty(pickupString) || pickupString.equals(", , ,")) {
                    btnPickUpAddressAdd.setText("Add");
                    txtPickupAddress.setVisibility(View.INVISIBLE);
                } else {
                    txtPickupAddress.setVisibility(View.VISIBLE);
                    txtPickupAddress.setText(pickupString);
                    btnPickUpAddressAdd.setText(getString(R.string.item_booking_edit));
                }

                // set drop-off text
                String dropoffString = String.format("%s, %s, %s, %s", finalDropOff.DropAddress, finalDropOff.DropArea, finalDropOff.DropCity, finalDropOff.DropZipCode);
                if (TextUtils.isEmpty(dropoffString) || dropoffString.equals(", , , ")) {
                    btnDropAddressAdd.setText("Add");
                    txtDropoffAddress.setVisibility(View.INVISIBLE);
                } else {
                    txtDropoffAddress.setText(dropoffString);
                    txtDropoffAddress.setVisibility(View.VISIBLE);
                    btnDropAddressAdd.setText(getString(R.string.item_booking_edit));
                }
            }
        });
        dialogRevised.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void pickAddressSet(boolean isSameAddress) {

        AddressDialog addressDialog = new AddressDialog(this, ADDRESS_PICK_UP, isSameAddress);
        addressDialog.setOnSubmitListener(new AddressDialog.onSubmitListener() {
            @Override
            public void onSubmit(String address, String area, String city, String zipCode, boolean isSame) {

                isDropOffSame = isSame;

                txtPickupAddress.setVisibility(View.VISIBLE);
                String addressString = String.format("%s, %s, %s, %s", address, area, city, zipCode);

                txtPickupAddress.setText(addressString);
                bookingRequest.PickAddress = address;
                bookingRequest.PickArea = area;
                bookingRequest.PickCity = city;
                bookingRequest.PickZipCode = zipCode;
                btnPickUpAddressAdd.setText(getString(R.string.item_booking_edit));

                if (isSame) {
                    txtDropoffAddress.setVisibility(View.VISIBLE);
                    txtDropoffAddress.setText(addressString);
                    bookingRequest.DropAddress = address;
                    bookingRequest.DropArea = area;
                    bookingRequest.DropCity = city;
                    bookingRequest.DropZipCode = zipCode;
                    btnDropAddressAdd.setText(getString(R.string.item_booking_edit));

                } else {
                    txtDropoffAddress.setVisibility(View.INVISIBLE);
                }
            }

        });
        addressDialog.show();
        addressDialog.setAddressDetails(bookingRequest, true);
    }

    private void dropAddressSet() {
        AddressDialog addressDialog1 = new AddressDialog(this, ADDRESS_DROP_OFF, false);
        addressDialog1.setOnSubmitListener(new AddressDialog.onSubmitListener() {
            @Override
            public void onSubmit(String address, String area, String city, String zipCode, boolean isSame) {
                if (isSame) {

                    if (txtPickupAddress.getText().toString().length() == 0) {
                        txtDropoffAddress.setVisibility(View.GONE);
                        Functions.showToast(BookingActivity.this, "No Pick-up address");
                    } else {
                        txtDropoffAddress.setVisibility(View.VISIBLE);
                        txtDropoffAddress.setText(txtPickupAddress.getText().toString());
                    }

                    bookingRequest.PickAddress = address;
                    bookingRequest.PickArea = area;
                    bookingRequest.PickCity = city;
                    bookingRequest.PickZipCode = zipCode;
                } else {
                    txtDropoffAddress.setVisibility(View.VISIBLE);
                    String addressString = String.format("%s, %s, %s, %s", address, area, city, zipCode);
                    txtDropoffAddress.setText(addressString);
                    bookingRequest.DropAddress = address;
                    bookingRequest.DropArea = area;
                    bookingRequest.DropCity = city;
                    bookingRequest.DropZipCode = zipCode;
                }
                btnDropAddressAdd.setText(getString(R.string.item_booking_edit));
            }

        });
        addressDialog1.show();
        addressDialog1.setAddressDetails(bookingRequest, false);
    }

    private void removePickAddress() {
        btnPickUpAddressAdd.setText(getString(R.string.item_booking_add));
        txtPickupAddress.setText("");
        bookingRequest.PickAddress = "";
        bookingRequest.PickArea = "";
        bookingRequest.PickCity = "";
        bookingRequest.PickZipCode = "";

        pickupPojo = new PickupPojo();
    }

    private void removeDropAddress() {
        btnDropAddressAdd.setText(getString(R.string.item_booking_add));
        txtDropoffAddress.setText("");

        bookingRequest.DropAddress = "";
        bookingRequest.DropArea = "";
        bookingRequest.DropCity = "";
        bookingRequest.DropZipCode = "";

        dropPojo = new DropPojo();

    }

    public void bookRequest() {

        try {

            bookingRequest.Description = Functions.toStr(edtDescription);

            bookingRequest.IsBodyShop = checkBodyWash.isChecked();

            if (!checkBodyWash.isChecked() && !checkService.isChecked()) {
                Toast.makeText(this, "Select Service Type.", Toast.LENGTH_SHORT).show();
                return;
            }

            bookingRequest.IsService = checkService.isChecked();

            String bookingDateAndTime = txtDate.getText() + " " + txtTime.getText();
            boolean isValidDateTime = checkValidBookDateAndTime(bookingDateAndTime);

            if (!isValidDateTime) {
                Toast.makeText(this, "Select Date and Time.", Toast.LENGTH_SHORT).show();
                return;
            } else {
                bookingRequest.PreferredDateTime = Functions.parseDate(bookingDateAndTime, bookingDateTimeFormate, Functions.ServerDateTimeFormat);
            }

            bookingRequest.ServiceCentreID = centreDetailPojo.ServiceCentreID;

            bookingRequest.UserID = PrefUtils.getUserID(this);

            bookingRequest.VehicleID = carPojo.VehicleID;

            if (pickupPojo != null) {
                bookingRequest.PickAddress = pickupPojo.PickAddress;
                bookingRequest.PickArea = pickupPojo.PickArea;
                bookingRequest.PickCity = pickupPojo.PickCity;
                bookingRequest.PickZipCode = pickupPojo.PickZipCode;
            }

            if (dropPojo != null) {
                bookingRequest.DropAddress = dropPojo.DropAddress;
                bookingRequest.DropArea = dropPojo.DropArea;
                bookingRequest.DropCity = dropPojo.DropCity;
                bookingRequest.DropZipCode = dropPojo.DropZipCode;
            }

            if (!TextUtils.isEmpty(bookingRequest.PickAddress) && !TextUtils.isEmpty(bookingRequest.PickArea)
                    && !TextUtils.isEmpty(bookingRequest.PickCity) && !TextUtils.isEmpty(bookingRequest.PickZipCode)) {

                bookingRequest.IsPickup = true;
            } else {
                bookingRequest.IsPickup = false;
            }

            if (!TextUtils.isEmpty(bookingRequest.DropAddress) && !TextUtils.isEmpty(bookingRequest.DropArea)
                    && !TextUtils.isEmpty(bookingRequest.DropCity) && !TextUtils.isEmpty(bookingRequest.DropZipCode)) {

                bookingRequest.IsDrop = true;
            } else {

                bookingRequest.IsDrop = false;
            }

            Log.e("book_req", Functions.jsonString(bookingRequest));

            presenter.book(this, bookingRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean checkValidBookDateAndTime(String dateTime) {
        boolean validDateAndTime;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(bookingDateTimeFormate);
            simpleDateFormat.parse(dateTime);
            validDateAndTime = true;
        } catch (Exception e) {
            validDateAndTime = false;
        }
        return validDateAndTime;
    }
}
