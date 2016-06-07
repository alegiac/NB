package app.wt.noolis;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import app.wt.noolis.adapter.ListAdapterNote;
import app.wt.noolis.data.DatabaseManager;
import app.wt.noolis.model.Category;
import app.wt.noolis.model.Note;
import app.wt.noolis.utils.Tools;

public class ActivityCategoryDetails extends AppCompatActivity {

    public final static String EXTRA_OBJCT = "app.wt.noolis.EXTRA_OBJECT_CATEGORY";

    private Toolbar toolbar;
    private ActionBar actionBar;

    private ImageView image;
    private TextView name;
    private AppBarLayout appbar;
    private Category ext_category;

    public RecyclerView recyclerView;
    public ListAdapterNote mAdapter;
    private LinearLayout lyt_not_found;
    private DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);

        // init db
        db = new DatabaseManager(this);

        // get extra object
        ext_category = (Category) getIntent().getSerializableExtra(EXTRA_OBJCT);
        iniComponent();
        if (ext_category != null) {
            setCategoryView();
        }
        initToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        lyt_not_found = (LinearLayout) findViewById(R.id.lyt_not_found);

        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityCategoryDetails.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        displayData(db.getNotesByCategoryId(ext_category.getId()));
    }

    private void iniComponent() {
        image = (ImageView) findViewById(R.id.image);
        name = (TextView) findViewById(R.id.name);
        appbar = (AppBarLayout) findViewById(R.id.appbar);
    }

    private void setCategoryView() {
        image.setImageResource(ext_category.getIcon());
        image.setColorFilter(Color.parseColor(ext_category.getColor()));
        name.setText(ext_category.getName());
        appbar.setBackgroundColor(Color.parseColor(ext_category.getColor()));
        Tools.systemBarLolipopCustom(ActivityCategoryDetails.this, ext_category.getColor());
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");
    }

    private void displayData(List<Note> items){
        mAdapter = new ListAdapterNote(getApplicationContext(), items);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new ListAdapterNote.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Note model) {
                Intent intent=new Intent(getApplicationContext(),ActivityEditNote.class);
                intent.putExtra(ActivityEditNote.EXTRA_OBJCT, model);
                startActivity(intent);
            }
        });
        if(mAdapter.getItemCount() == 0){
            lyt_not_found.setVisibility(View.VISIBLE);
        }else{
            lyt_not_found.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
