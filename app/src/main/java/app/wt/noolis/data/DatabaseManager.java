package app.wt.noolis.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.wt.noolis.R;
import app.wt.noolis.model.Category;
import app.wt.noolis.model.Note;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DB_NAME         = "noolis.db";

    private static final String TABLE_NOTE      = "note";
    private static final String TABLE_CATEGORY  = "category";

    private static final String COL_N_ID        = "n_id";
    private static final String COL_N_TITLE     = "n_title";
    private static final String COL_N_CONTENT   = "n_content";
    private static final String COL_N_FAV       = "n_favourite";
    private static final String COL_N_LAST_EDIT = "n_last_edit";
    private static final String COL_N_CATEGORY  = "n_category";

    private static final String COL_C_ID        = "c_id";
    private static final String COL_C_NAME      = "c_name";
    private static final String COL_C_COLOR     = "c_color";
    private static final String COL_C_ICON      = "c_icon";

    private static final int DB_VERSION = 1;

    private final Context context;
    private SQLiteDatabase db;

    private int cat_id[];
    private String cat_name[];
    private String cat_color[];
    private TypedArray cat_icon;

    public DatabaseManager(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
        this.context = ctx;
        db = this.getWritableDatabase();

        cat_id      = ctx.getResources().getIntArray(R.array.category_id);
        cat_name    = ctx.getResources().getStringArray(R.array.category_name);
        cat_color   = ctx.getResources().getStringArray(R.array.category_color);
        cat_icon    = ctx.getResources().obtainTypedArray(R.array.category_icon);

        if(cat_id.length!=getCategorySize()){
            defineCategory(db);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableNote(db);
        createTableCategory(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        try{
            defineCategory(db);
        } catch (Exception e){ }
    }

    private void createTableNote(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NOTE + " ("
                + COL_N_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_N_TITLE + " TEXT, "
                + COL_N_CONTENT + " TEXT, "
                + COL_N_FAV + " INTEGER, "
                + COL_N_LAST_EDIT + " NUMERIC, "
                + COL_N_CATEGORY + " INTEGER, "
                + " FOREIGN KEY(" + COL_N_CATEGORY + ") REFERENCES " + TABLE_NOTE + "(" + COL_C_ID + ")"
                +")";
        db.execSQL(CREATE_TABLE);
    }

    private void createTableCategory(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_CATEGORY + " ("
                + COL_C_ID + " INTEGER PRIMARY KEY, "
                + COL_C_NAME + " TEXT, "
                + COL_C_COLOR + " TEXT, "
                + COL_C_ICON + " INTEGER "
                + " )";
        db.execSQL(CREATE_TABLE);
    }

    private void defineCategory(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + TABLE_CATEGORY); // refresh table content
        db.execSQL("VACUUM");
        for (int i = 0; i < cat_id.length; i++) {
            ContentValues values = new ContentValues();
            values.put(COL_C_ID, cat_id[i]);
            values.put(COL_C_NAME, cat_name[i]);
            values.put(COL_C_COLOR, cat_color[i]);
            values.put(COL_C_ICON, cat_icon.getResourceId(i, 0));
            Log.e("ICON : ", i+" | "+cat_icon.getResourceId(i, 0));
            db.insert(TABLE_CATEGORY, null, values); // Inserting Row
        }
    }


    /**
     * All Note transaction
     */
    public void insertNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(COL_N_TITLE, note.getTittle());
        values.put(COL_N_CONTENT, note.getContent());
        values.put(COL_N_FAV, note.getFavourite());
        values.put(COL_N_LAST_EDIT, note.getLastEdit());
        values.put(COL_N_CATEGORY, note.getCategory().getId());
        try {
            db.insert(TABLE_NOTE, null, values);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }

    public void deleteNote(long rowId) {
        try {
            db.delete(TABLE_NOTE, COL_N_ID + "=" + rowId, null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Db Error", e.toString());
        }
    }

    public void updateNote(Note note) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_N_TITLE, note.getTittle());
            contentValues.put(COL_N_CONTENT, note.getContent());
            contentValues.put(COL_N_LAST_EDIT, note.getLastEdit());
            contentValues.put(COL_N_CATEGORY, note.getCategory().getId());
            db.update(TABLE_NOTE, contentValues, COL_N_ID + "=" + note.getId(), null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Db Error", e.toString());
        }
    }

    public Note get(Long id) {
        Note note = new Note();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_NOTE + " WHERE " + COL_N_ID + " = ?", new String[]{id + ""});
            cur.moveToFirst();
            note = getNoteFromCursor(cur);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Db Error", e.toString());
        }
        return note;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_NOTE, null);
            cur.moveToFirst();
            if (!cur.isAfterLast()) {
                do {
                    notes.add(getNoteFromCursor(cur));
                } while (cur.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DB ERROR", e.toString());
        }
        return notes;
    }

    public List<Note> getNotesByCategoryId(Long cat_id) {
        List<Note> notes = new ArrayList<>();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_NOTE + " WHERE " + COL_N_CATEGORY + " = ?", new String[]{cat_id + ""});
            cur.moveToFirst();
            if (!cur.isAfterLast()) {
                do {
                    notes.add(getNoteFromCursor(cur));
                } while (cur.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DB ERROR", e.toString());
        }
        return notes;
    }

    public int getNotesCountByCategoryId(Long cat_id) {
        Cursor cursor = db.rawQuery("SELECT COUNT(" + COL_N_ID + ") FROM " + TABLE_NOTE + " WHERE " + COL_N_CATEGORY + " = ?", new String[]{ cat_id + "" });
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    private Note getNoteFromCursor(Cursor cur){
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NOTE + " ("
                + COL_N_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_N_TITLE + " TEXT, "
                + COL_N_CONTENT + " TEXT, "
                + COL_N_FAV + " INTEGER, "
                + COL_N_LAST_EDIT + " NUMERIC, "
                + COL_N_CATEGORY + " INTEGER, "
                + " FOREIGN KEY(" + COL_N_CATEGORY + ") REFERENCES " + TABLE_NOTE + "(" + COL_C_ID + ")"
                +")";

        Note n = new Note();
        n.setId(cur.getLong(0));
        n.setTittle(cur.getString(1));
        n.setContent(cur.getString(2));
        n.setFavourite(cur.getInt(3));
        n.setLastEdit(cur.getLong(4));
        n.setCategory(getCategoryById(cur.getLong(5)));
        return n;
    }

    /**
     * All Category transaction
     */

    public Category getCategoryById(long id){
        Category category = new Category();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_CATEGORY + " WHERE " + COL_C_ID + " = ?", new String[]{id + ""});
            cur.moveToFirst();
            category  = getCategoryByCursor(cur);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Db Error", e.toString());
        }
        return category;
    }

    public List<Category> getAllCategory(){
        List<Category> categories = new ArrayList<>();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_CATEGORY, null);
            cur.moveToFirst();
            if (!cur.isAfterLast()) {
                do {
                    categories.add(getCategoryByCursor(cur));
                } while (cur.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Db Error", e.toString());
        }
        return categories;
    }

    private Category getCategoryByCursor(Cursor cur){
        Category c = new Category();
        c.setId(cur.getLong(0));
        c.setName(cur.getString(1));
        c.setColor(cur.getString(2));
        c.setIcon(cur.getInt(3));
        c.setNote_count(getNotesCountByCategoryId(c.getId()));
        return c;
    }

    /**
     * All Favorites Note transaction
     */
    public List<Note> getAllFavNote() {
        List<Note> notes = new ArrayList<>();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_NOTE + " WHERE " + COL_N_FAV + " = ?", new String[]{"1"});
            cur.moveToFirst();
            if (!cur.isAfterLast()) {
                do {
                    notes.add(getNoteFromCursor(cur));
                } while (cur.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DB ERROR", e.toString());
        }
        return notes;
    }


    public void setFav(long id) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_N_FAV, 1);
            db.update(TABLE_NOTE, contentValues, COL_N_ID + "=" + id, null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Db Error", e.toString());
        }
    }

    public void removeFav(long id) {
        if(isFavoriteExist(id)){
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_N_FAV, 0);
            db.update(TABLE_NOTE, contentValues, COL_N_ID + "=" + id, null);
        }
    }


    /**
     * Support method
     */
    private boolean isFavoriteExist(long id) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTE + " WHERE " + COL_N_ID + " = ?", new String[]{id + ""});
        int count = cursor.getCount();
        cursor.close();
        return  (count > 0);
    }

    public int getCategorySize() {
        Cursor cursor = db.rawQuery("SELECT COUNT(" + COL_C_ID + ") FROM " + TABLE_CATEGORY, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

}
