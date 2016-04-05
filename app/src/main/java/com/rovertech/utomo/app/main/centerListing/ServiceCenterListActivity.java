package com.rovertech.utomo.app.main.centerListing;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rovertech.utomo.app.R;
import com.rovertech.utomo.app.account.adapter.CityAdapter;
import com.rovertech.utomo.app.account.model.City;
import com.rovertech.utomo.app.helper.AppConstant;
import com.rovertech.utomo.app.helper.Functions;
import com.rovertech.utomo.app.helper.PrefUtils;
import com.rovertech.utomo.app.widget.familiarrecyclerview.FamiliarRecyclerView;
import com.rovertech.utomo.app.widget.familiarrecyclerview.FamiliarRecyclerViewOnScrollListener;

import java.util.ArrayList;
import java.util.List;

public class ServiceCenterListActivity extends AppCompatActivity implements ServiceCentreView, View.OnClickListener {

    private TextView txtCustomTitle, txtNoData;
    private AutoCompleteTextView edtCity;
    private Button btnSearch;
    private ImageView imgFilter;
    private LinearLayout searchLayout;
    private ServiceCentreLisPresenter presenter;
    private List<ServiceCenterPojo> centerList;
    private ServiceCentreListAdapter adapter;
    private DrawerLayout drawerLayout;

    private TextView txtFilterTitle;
    private Button btnApply, btnReset;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FamiliarRecyclerView recyclerView;
    private View mFooterLoadMoreView;
    private int lastCentreId = 0;
    private int cityId = 0;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_center_list);

        Double userLatitude = getIntent().getDoubleExtra("lat", 0.0);
        Double userLongitude = getIntent().getDoubleExtra("lng", 0.0);
        cityId = PrefUtils.getUserFullProfileDetails(this).CityID;

        if (userLatitude == 0.0 || userLongitude == 0.0) {
            type = AppConstant.BY_CITY;
        } else {
            type = AppConstant.BY_LAT_LNG;
        }

        init();

        presenter = new ServiceCentreListPresenterImpl(this);
        presenter.setLocation(userLatitude, userLongitude, cityId);

        centerList = new ArrayList<>();
        adapter = new ServiceCentreListAdapter(ServiceCenterListActivity.this, centerList);
        recyclerView.setAdapter(adapter);

        presenter.fetchCentreList(lastCentreId, this, type);

    }

    private void init() {

        initToolbar();

        edtCity = (AutoCompleteTextView) findViewById(R.id.edtCity);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        txtNoData = (TextView) findViewById(R.id.txtNoData);
        imgFilter = (ImageView) findViewById(R.id.imgFilter);
        imgFilter.setOnClickListener(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        txtFilterTitle = (TextView) findViewById(R.id.txtFilterTitle);
        btnApply = (Button) findViewById(R.id.btnApply);
        btnReset = (Button) findViewById(R.id.btnReset);
        searchLayout = (LinearLayout) findViewById(R.id.searchLayout);
        searchLayout.setVisibility(View.GONE);

        setTypeface();

        initRecycler();

        edtCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0)
                    presenter.fetchCity(ServiceCenterListActivity.this, s.toString());
            }
        });

        btnSearch.setOnClickListener(this);
    }

    private void initRecycler() {
        mFooterLoadMoreView = View.inflate(this, R.layout.footer_view_load_more, null);
        mFooterLoadMoreView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mSwipeRefreshLayout);
        recyclerView = (FamiliarRecyclerView) findViewById(R.id.recyclerView);
        recyclerView.addFooterView(mFooterLoadMoreView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lastCentreId = 0;
                        centerList = new ArrayList<ServiceCenterPojo>();
                        presenter.fetchCentreList(lastCentreId, ServiceCenterListActivity.this, type);
                    }
                }, 1000);
            }
        });

        recyclerView.addOnScrollListener(new FamiliarRecyclerViewOnScrollListener(linearLayoutManager) {
            @Override
            public void onScrolledToTop() {

            }

            @Override
            public void onScrolledToBottom() {
                presenter.fetchCentreList(lastCentreId, ServiceCenterListActivity.this, type);
            }
        });
    }

    private void setTypeface() {
        edtCity.setTypeface(Functions.getNormalFont(this));
        txtNoData.setTypeface(Functions.getBoldFont(this));
        btnSearch.setTypeface(Functions.getBoldFont(this));
        txtFilterTitle.setTypeface(Functions.getBoldFont(this));
        btnApply.setTypeface(Functions.getBoldFont(this));
        btnReset.setTypeface(Functions.getBoldFont(this));
    }

    private void initToolbar() {

        txtCustomTitle = (TextView) findViewById(R.id.txtCustomTitle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow);
        setSupportActionBar(toolbar);

        txtCustomTitle.setText("Nearby Centres");
        txtCustomTitle.setTypeface(Functions.getBoldFont(this));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgFilter:
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }
                break;

            case R.id.btnSearch:
                lastCentreId = 0;
                type = AppConstant.BY_CITY;
                presenter.setLocation(0.0, 0.0, cityId);
                presenter.fetchCentreList(lastCentreId, this, type);
                break;
        }
    }

    @Override
    public void setListing(List<ServiceCenterPojo> centerArrayList, int lastCentreId) {

        this.lastCentreId = lastCentreId;

        if (centerArrayList.size() > 0) {
            centerList.addAll(centerArrayList);
            adapter.setCentreList(centerList);
        }

        if (centerList.size() == 0) {
            searchLayout.setVisibility(View.VISIBLE);
            imgFilter.setVisibility(View.GONE);

        } else {
            searchLayout.setVisibility(View.GONE);
            imgFilter.setVisibility(View.VISIBLE);

        }

        hideScroll();
    }

    @Override
    public void hideScroll() {
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);

        if (mFooterLoadMoreView.isShown())
            recyclerView.removeFooterView(mFooterLoadMoreView);
    }

    @Override
    public void setCityAdapter(CityAdapter adapter, final ArrayList<City> cityArrayList) {
        edtCity.setAdapter(adapter);
        edtCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtCity.getWindowToken(), 0);
                City city = cityArrayList.get(position);
                cityId = city.CityID;
            }
        });
    }
}